package dev.orangecast.shared.data.security

expect object CryptoUtils {
    fun sha256(input: ByteArray): ByteArray
    fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray
}