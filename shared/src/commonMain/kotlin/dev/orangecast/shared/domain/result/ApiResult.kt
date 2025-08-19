package dev.orangecast.shared.domain.result

import dev.orangecast.shared.domain.error.ApiError

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val error: ApiError) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
    
    val isSuccess: Boolean
        get() = this is Success
    
    val isError: Boolean
        get() = this is Error
    
    val isLoading: Boolean
        get() = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun getErrorOrNull(): ApiError? = when (this) {
        is Error -> error
        else -> null
    }
    
    inline fun onSuccess(action: (value: T) -> Unit): ApiResult<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }
    
    inline fun onError(action: (error: ApiError) -> Unit): ApiResult<T> {
        if (this is Error) {
            action(error)
        }
        return this
    }
    
    inline fun <R> map(transform: (T) -> R): ApiResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }
    
    companion object {
        fun <T> success(data: T): ApiResult<T> = Success(data)
        fun error(error: ApiError): ApiResult<Nothing> = Error(error)
        fun loading(): ApiResult<Nothing> = Loading
    }
}

// Extension to convert Kotlin Result to ApiResult
fun <T> Result<T>.toApiResult(): ApiResult<T> = fold(
    onSuccess = { ApiResult.success(it) },
    onFailure = { throwable ->
        val apiError = when (throwable) {
            is java.net.UnknownHostException, 
            is java.net.ConnectException -> ApiError.NetworkError(errorCause = throwable)
            is java.net.SocketTimeoutException -> ApiError.TimeoutError(errorCause = throwable)
            else -> ApiError.UnknownError(errorCause = throwable)
        }
        ApiResult.error(apiError)
    }
)