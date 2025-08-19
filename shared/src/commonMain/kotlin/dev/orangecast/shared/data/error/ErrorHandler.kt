package dev.orangecast.shared.data.error

import dev.orangecast.shared.domain.error.ApiError
import dev.orangecast.shared.domain.result.ApiResult
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException

object ErrorHandler {
    
    suspend fun <T> safeApiCall(
        operation: String,
        call: suspend () -> T
    ): ApiResult<T> {
        return try {
            val result = call()
            Napier.d("API call succeeded: $operation", tag = "ErrorHandler")
            ApiResult.success(result)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e // Don't catch coroutine cancellation
                else -> {
                    val apiError = mapExceptionToApiError(e, operation)
                    logError(apiError, operation)
                    ApiResult.error(apiError)
                }
            }
        }
    }
    
    private fun mapExceptionToApiError(exception: Throwable, operation: String): ApiError {
        return when (exception) {
            // Network errors
            is java.net.UnknownHostException,
            is java.net.ConnectException,
            is java.net.NoRouteToHostException -> {
                ApiError.NetworkError(errorCause = exception)
            }
            
            // Timeout errors
            is java.net.SocketTimeoutException,
            is TimeoutCancellationException -> {
                ApiError.TimeoutError(errorCause = exception)
            }
            
            // HTTP errors from Ktor
            is ClientRequestException -> {
                when (exception.response.status.value) {
                    429 -> ApiError.RateLimitError(errorCause = exception)
                    in 400..499 -> ApiError.ClientError(
                        code = exception.response.status.value,
                        errorMessage = "Request failed for $operation",
                        errorCause = exception
                    )
                    else -> ApiError.UnknownError(errorCause = exception)
                }
            }
            
            is ServerResponseException -> {
                ApiError.ServerError(
                    code = exception.response.status.value,
                    errorMessage = "Server error during $operation",
                    errorCause = exception
                )
            }
            
            // Serialization/parsing errors
            is SerializationException,
            is IllegalArgumentException -> {
                ApiError.ParseError(
                    errorMessage = "Failed to parse response for $operation",
                    errorCause = exception
                )
            }
            
            // Unknown errors
            else -> {
                ApiError.UnknownError(
                    errorMessage = "Unexpected error during $operation: ${exception.message}",
                    errorCause = exception
                )
            }
        }
    }
    
    private fun logError(apiError: ApiError, operation: String) {
        val errorDetails = buildString {
            appendLine("API Error Details:")
            appendLine("Operation: $operation")
            appendLine("Error Type: ${apiError::class.simpleName}")
            appendLine("User Message: ${apiError.getUserMessage()}")
            appendLine("Technical Message: ${apiError.message}")
            apiError.cause?.let { cause ->
                appendLine("Root Cause: ${cause::class.simpleName}")
                appendLine("Cause Message: ${cause.message}")
                appendLine("Stack Trace: ${cause.stackTraceToString()}")
            }
        }
        
        when (apiError) {
            is ApiError.NetworkError,
            is ApiError.TimeoutError -> {
                Napier.w(errorDetails, tag = "ErrorHandler")
            }
            is ApiError.ServerError,
            is ApiError.ParseError,
            is ApiError.UnknownError -> {
                Napier.e(errorDetails, tag = "ErrorHandler")
            }
            is ApiError.ClientError,
            is ApiError.RateLimitError -> {
                Napier.w(errorDetails, tag = "ErrorHandler")
            }
        }
    }
    
    fun <T> Result<T>.toApiResult(operation: String): ApiResult<T> {
        return fold(
            onSuccess = { ApiResult.success(it) },
            onFailure = { throwable ->
                val apiError = mapExceptionToApiError(throwable, operation)
                logError(apiError, operation)
                ApiResult.error(apiError)
            }
        )
    }
}