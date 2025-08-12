package dev.orangecast.shared.data.security

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

actual object CryptoUtils {
    actual fun sha256(input: ByteArray): ByteArray {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            digest.digest(input)
        } catch (e: Exception) {
            throw SecurityException("SHA-256 algorithm not available on Android", e)
        }
    }

    actual fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(key, "HmacSHA256")
            mac.init(secretKey)
            mac.doFinal(data)
        } catch (e: Exception) {
            throw SecurityException("HMAC-SHA256 algorithm not available on Android", e)
        }
    }
}