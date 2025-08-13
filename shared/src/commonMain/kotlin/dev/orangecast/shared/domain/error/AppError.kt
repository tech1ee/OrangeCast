package dev.orangecast.shared.domain.error

sealed class AppError(
    open val message: String,
    open val cause: Throwable? = null
) {
    data class NetworkError(
        override val message: String = "No internet connection. Please check your network settings.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
    
    data class ServerError(
        val statusCode: Int,
        override val message: String = "Server error occurred. Please try again later.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
    
    data class NotFoundError(
        val resourceType: String,
        override val message: String = "$resourceType not found.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
    
    data class ParseError(
        override val message: String = "Failed to parse data. The content might be corrupted.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
    
    data class InvalidUrlError(
        val url: String,
        override val message: String = "Invalid URL: $url",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
    
    data class RssFeedError(
        override val message: String = "Failed to load podcast episodes. The RSS feed might be unavailable.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
    
    data class SubscriptionError(
        override val message: String = "Failed to update subscription. Please try again.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
    
    data class UnknownError(
        override val message: String = "An unexpected error occurred. Please try again.",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
}

fun Throwable.toAppError(): AppError {
    return when (this) {
        is java.net.UnknownHostException -> AppError.NetworkError(cause = this)
        is java.net.SocketTimeoutException -> AppError.NetworkError(
            message = "Connection timeout. Please check your network connection.",
            cause = this
        )
        is java.net.ConnectException -> AppError.NetworkError(cause = this)
        is kotlinx.serialization.SerializationException -> AppError.ParseError(cause = this)
        is IllegalArgumentException -> {
            when {
                message?.contains("Invalid URL") == true -> AppError.InvalidUrlError(
                    url = message ?: "unknown",
                    cause = this
                )
                else -> AppError.UnknownError(
                    message = message ?: "Invalid data provided.",
                    cause = this
                )
            }
        }
        else -> AppError.UnknownError(
            message = message ?: "An unexpected error occurred.",
            cause = this
        )
    }
}