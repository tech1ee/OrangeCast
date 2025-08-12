package dev.orangecast.shared.data.network.security

import kotlinx.cinterop.*
import platform.CoreCrypto.*
import platform.Foundation.*
import platform.Security.*
import dev.orangecast.shared.data.security.CryptoUtils

@OptIn(ExperimentalForeignApi::class)
actual object CertificatePinner {
    private val pinnedCertificates = mutableMapOf<String, List<String>>()
    
    actual fun pin(hostname: String, certificateHashes: List<String>) {
        pinnedCertificates[hostname] = certificateHashes
    }
    
    actual fun validateCertificate(hostname: String, certificateChain: List<ByteArray>): Boolean {
        val expectedPins = pinnedCertificates[hostname] ?: return true
        
        return certificateChain.any { certBytes ->
            try {
                val hash = CryptoUtils.sha256(certBytes)
                val hashString = hash.joinToString("") { byte -> 
                    (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
                }
                expectedPins.any { pin -> pin.equals(hashString, ignoreCase = true) }
            } catch (e: Exception) {
                false
            }
        }
    }
}