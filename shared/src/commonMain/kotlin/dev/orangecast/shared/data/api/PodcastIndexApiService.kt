package dev.orangecast.shared.data.api

import dev.orangecast.shared.data.api.model.PodcastIndexResponse
import dev.orangecast.shared.data.api.model.PodcastIndexPodcast
import dev.orangecast.shared.data.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class PodcastIndexApiService(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val apiSecret: String
) {
    
    suspend fun searchPodcasts(query: String, limit: Int = 20): PodcastIndexResponse {
        val timestamp = Clock.System.now().epochSeconds
        val authHash = generateAuthHash(apiKey, apiSecret, timestamp)
        
        return httpClient.get(ApiConfig.PODCASTINDEX_SEARCH_URL) {
            header("User-Agent", ApiConfig.USER_AGENT)
            header("X-Auth-Key", apiKey)
            header("X-Auth-Date", timestamp)
            header("Authorization", authHash)
            parameter("q", query)
            parameter("max", limit)
            parameter("clean", "true") // Get clean metadata
        }.body()
    }
    
    suspend fun lookupPodcast(feedId: String): PodcastIndexResponse {
        val timestamp = Clock.System.now().epochSeconds
        val authHash = generateAuthHash(apiKey, apiSecret, timestamp)
        
        return httpClient.get(ApiConfig.PODCASTINDEX_LOOKUP_URL) {
            header("User-Agent", ApiConfig.USER_AGENT)
            header("X-Auth-Key", apiKey)
            header("X-Auth-Date", timestamp)
            header("Authorization", authHash)
            parameter("id", feedId)
        }.body()
    }
    
    suspend fun searchByTitle(title: String, limit: Int = 10): PodcastIndexResponse {
        val timestamp = Clock.System.now().epochSeconds
        val authHash = generateAuthHash(apiKey, apiSecret, timestamp)
        
        return httpClient.get(ApiConfig.PODCASTINDEX_SEARCH_TITLE_URL) {
            header("User-Agent", ApiConfig.USER_AGENT)
            header("X-Auth-Key", apiKey)
            header("X-Auth-Date", timestamp)
            header("Authorization", authHash)
            parameter("q", title)
            parameter("max", limit)
            parameter("clean", "true")
        }.body()
    }
    
    private fun generateAuthHash(apiKey: String, apiSecret: String, timestamp: Long): String {
        // PodcastIndex requires SHA-1 hash of (apiKey + apiSecret + timestamp)
        val data = apiKey + apiSecret + timestamp
        return sha1Hash(data)
    }
    
    private fun sha1Hash(input: String): String {
        val bytes = input.encodeToByteArray()
        val digest = sha1Digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    private fun sha1Digest(input: ByteArray): ByteArray {
        // Simple SHA-1 implementation following RFC 3174
        val h = intArrayOf(
            0x67452301.toInt(),
            0xEFCDAB89.toInt(), 
            0x98BADCFE.toInt(),
            0x10325476,
            0xC3D2E1F0.toInt()
        )
        
        val paddedInput = padMessage(input)
        
        for (chunkStart in paddedInput.indices step 64) {
            val chunk = paddedInput.sliceArray(chunkStart until chunkStart + 64)
            val w = IntArray(80)
            
            // Break chunk into sixteen 32-bit big-endian words
            for (i in 0 until 16) {
                w[i] = (chunk[i * 4].toInt() and 0xFF shl 24) or
                       (chunk[i * 4 + 1].toInt() and 0xFF shl 16) or
                       (chunk[i * 4 + 2].toInt() and 0xFF shl 8) or
                       (chunk[i * 4 + 3].toInt() and 0xFF)
            }
            
            // Extend the sixteen 32-bit words into eighty 32-bit words
            for (i in 16 until 80) {
                w[i] = leftRotate(w[i - 3] xor w[i - 8] xor w[i - 14] xor w[i - 16], 1)
            }
            
            var a = h[0]
            var b = h[1] 
            var c = h[2]
            var d = h[3]
            var e = h[4]
            
            for (i in 0 until 80) {
                val (f, k) = when (i) {
                    in 0 until 20 -> Pair((b and c) or ((b.inv()) and d), 0x5A827999)
                    in 20 until 40 -> Pair(b xor c xor d, 0x6ED9EBA1)
                    in 40 until 60 -> Pair((b and c) or (b and d) or (c and d), 0x8F1BBCDC.toInt())
                    else -> Pair(b xor c xor d, 0xCA62C1D6.toInt())
                }
                
                val temp = leftRotate(a, 5) + f + e + k + w[i]
                e = d
                d = c
                c = leftRotate(b, 30)
                b = a
                a = temp
            }
            
            h[0] += a
            h[1] += b
            h[2] += c
            h[3] += d
            h[4] += e
        }
        
        val result = ByteArray(20)
        for (i in h.indices) {
            result[i * 4] = (h[i] shr 24).toByte()
            result[i * 4 + 1] = (h[i] shr 16).toByte()
            result[i * 4 + 2] = (h[i] shr 8).toByte()
            result[i * 4 + 3] = h[i].toByte()
        }
        return result
    }
    
    private fun padMessage(input: ByteArray): ByteArray {
        val originalLength = input.size
        val bitLength = originalLength * 8L
        
        // Padding: append '1' bit followed by zeros
        val paddingLength = (55 - originalLength % 64 + 64) % 64
        val totalLength = originalLength + 1 + paddingLength + 8
        
        val padded = ByteArray(totalLength)
        input.copyInto(padded)
        padded[originalLength] = 0x80.toByte() // append '1' bit
        
        // Append original length as 64-bit big-endian integer
        for (i in 0 until 8) {
            padded[totalLength - 8 + i] = (bitLength shr (56 - i * 8)).toByte()
        }
        
        return padded
    }
    
    private fun leftRotate(value: Int, amount: Int): Int {
        return (value shl amount) or (value ushr (32 - amount))
    }
}