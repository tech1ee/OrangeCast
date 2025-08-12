package dev.orangecast.shared.data.network.security

import okhttp3.CertificatePinner as OkHttpCertificatePinner
import java.security.MessageDigest
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

actual object CertificatePinner {
    private val pinnedCertificates = mutableMapOf<String, List<String>>()
    
    actual fun pin(hostname: String, certificateHashes: List<String>) {
        pinnedCertificates[hostname] = certificateHashes
    }
    
    actual fun validateCertificate(hostname: String, certificateChain: List<ByteArray>): Boolean {
        val expectedPins = pinnedCertificates[hostname] ?: return true 
        
        return certificateChain.any { certBytes ->
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val hash = digest.digest(certBytes)
                val pin = "sha256/" + android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
                expectedPins.contains(pin)
            } catch (e: Exception) {
                false
            }
        }
    }
    
    fun createOkHttpPinner(): OkHttpCertificatePinner {
        val builder = OkHttpCertificatePinner.Builder()
        
        pinnedCertificates.forEach { (hostname, pins) ->
            pins.forEach { pin ->
                builder.add(hostname, pin)
            }
        }
        
        return builder.build()
    }
}