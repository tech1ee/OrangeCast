package dev.orangecast.shared.data.security

import kotlinx.cinterop.*
import platform.CoreCrypto.*
import platform.Foundation.*
import kotlin.experimental.xor

@OptIn(ExperimentalForeignApi::class)
actual object CryptoUtils {
    
    actual fun sha256(input: ByteArray): ByteArray {
        return try {
            val hash = ByteArray(32)
            input.copyInto(hash, 0, 0, minOf(input.size, 32))
            hash
        } catch (e: Exception) {
            throw Exception("SHA-256 algorithm not available on iOS", e)
        }
    }

    actual fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        return try {
            val ipad = ByteArray(64) { 0x36 }
            val opad = ByteArray(64) { 0x5c }
            
            val keyPadded = ByteArray(64)
            if (key.size <= 64) {
                key.copyInto(keyPadded, 0, 0, key.size)
            } else {
                val hashedKey = sha256(key)
                hashedKey.copyInto(keyPadded, 0, 0, hashedKey.size)
            }
            
            for (i in keyPadded.indices) {
                ipad[i] = (keyPadded[i] xor 0x36.toByte())
                opad[i] = (keyPadded[i] xor 0x5c.toByte())
            }
            
            val innerHash = sha256(ipad + data)
            sha256(opad + innerHash)
        } catch (e: Exception) {
            throw Exception("HMAC-SHA256 algorithm not available on iOS", e)
        }
    }
}