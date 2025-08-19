package dev.orangecast.shared.domain.error

sealed class ApiError(
    val message: String,
    val cause: Throwable? = null
) {
    data class NetworkError(
        val errorMessage: String = "Unable to connect to the internet",
        val errorCause: Throwable? = null
    ) : ApiError(errorMessage, errorCause)
    
    data class ServerError(
        val code: Int,
        val errorMessage: String = "Server error occurred",
        val errorCause: Throwable? = null
    ) : ApiError(errorMessage, errorCause)
    
    data class ClientError(
        val code: Int,
        val errorMessage: String = "Request failed",
        val errorCause: Throwable? = null
    ) : ApiError(errorMessage, errorCause)
    
    data class ParseError(
        val errorMessage: String = "Failed to process data",
        val errorCause: Throwable? = null
    ) : ApiError(errorMessage, errorCause)
    
    data class UnknownError(
        val errorMessage: String = "An unexpected error occurred",
        val errorCause: Throwable? = null
    ) : ApiError(errorMessage, errorCause)
    
    data class TimeoutError(
        val errorMessage: String = "Request timed out",
        val errorCause: Throwable? = null
    ) : ApiError(errorMessage, errorCause)
    
    data class RateLimitError(
        val errorMessage: String = "Too many requests, please try again later",
        val errorCause: Throwable? = null
    ) : ApiError(errorMessage, errorCause)
    
    // User-friendly message for UI display
    fun getUserMessage(): String = when (this) {
        is NetworkError -> "Please check your internet connection and try again"
        is ServerError -> "Our servers are experiencing issues. Please try again in a few minutes"
        is ClientError -> "Something went wrong with your request. Please try again"
        is ParseError -> "We're having trouble loading content. Please try again"
        is TimeoutError -> "Request is taking too long. Please try again"
        is RateLimitError -> "You're making requests too quickly. Please wait a moment and try again"
        is UnknownError -> "Something unexpected happened. Please try again"
    }
}