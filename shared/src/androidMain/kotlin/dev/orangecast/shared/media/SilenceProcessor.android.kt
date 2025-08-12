package dev.orangecast.shared.media

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import dev.orangecast.shared.domain.model.AudioAnalysisResult
import dev.orangecast.shared.domain.model.SilenceDetectionConfig
import dev.orangecast.shared.domain.model.SilenceSegment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sqrt

class AndroidSilenceProcessor(
    private val config: SilenceDetectionConfig,
    private val onSilenceDetected: (SilenceSegment) -> Unit,
    private val scope: CoroutineScope
) : AudioProcessor {

    private var inputAudioFormat: AudioFormat = AudioFormat.NOT_SET
    private var outputAudioFormat: AudioFormat = AudioFormat.NOT_SET
    private var isActive: Boolean = false
    
    private var audioBuffer = mutableListOf<Short>()
    private var silenceStartTime: Long? = null
    private var currentTime: Long = 0L
    private var sampleRate: Int = 44100
    private val analysisWindowSizeMs = 100L
    private var analysisBuffer = mutableListOf<Float>()

    override fun configure(inputAudioFormat: AudioFormat): AudioFormat {
        this.inputAudioFormat = inputAudioFormat
        this.outputAudioFormat = inputAudioFormat
        this.sampleRate = inputAudioFormat.sampleRate
        return outputAudioFormat
    }

    override fun isActive(): Boolean = isActive

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!isActive || !inputBuffer.hasRemaining()) return

        val audioData = convertByteBufferToShortArray(inputBuffer)
        audioBuffer.addAll(audioData.toList())
        
        scope.launch {
            processAudioData(audioData)
        }
    }

    override fun getOutput(): ByteBuffer? {
        if (audioBuffer.isEmpty()) return AudioProcessor.EMPTY_BUFFER

        val outputData = audioBuffer.toShortArray()
        audioBuffer.clear()
        
        return convertShortArrayToByteBuffer(outputData)
    }

    override fun isEnded(): Boolean = false

    override fun flush() {
        audioBuffer.clear()
        analysisBuffer.clear()
        silenceStartTime = null
        currentTime = 0L
    }

    override fun reset() {
        flush()
        inputAudioFormat = AudioFormat.NOT_SET
        outputAudioFormat = AudioFormat.NOT_SET
        isActive = false
    }

    fun setActive(active: Boolean) {
        isActive = active
    }

    private suspend fun processAudioData(audioData: ShortArray) = withContext(Dispatchers.Default) {
        val startProcessingTime = System.currentTimeMillis()
        
        for (sample in audioData) {
            analysisBuffer.add(sample.toFloat())
            currentTime = (analysisBuffer.size * 1000L) / sampleRate
            
            if (analysisBuffer.size >= (sampleRate * analysisWindowSizeMs / 1000)) {
                analyzeWindow()
                analysisBuffer.clear()
            }
        }
        
        val processingTime = System.currentTimeMillis() - startProcessingTime
        if (processingTime > 100) {
        }
    }

    private fun analyzeWindow() {
        val rmsVolume = calculateRMSVolume(analysisBuffer)
        val volumeDb = if (rmsVolume > 0) 20 * log10(rmsVolume) else -60.0f
        
        val isSilence = volumeDb <= config.silenceThresholdDb
        
        if (isSilence && silenceStartTime == null) {
            silenceStartTime = currentTime
        } else if (!isSilence && silenceStartTime != null) {
            val silenceDuration = currentTime - silenceStartTime!!
            
            if (silenceDuration >= config.minimumSilenceDurationMs) {
                val silenceSegment = SilenceSegment(
                    startTimeMs = silenceStartTime!!,
                    endTimeMs = currentTime,
                    averageVolumeDb = volumeDb,
                    confidenceScore = calculateConfidenceScore(volumeDb, silenceDuration)
                )
                
                if (silenceSegment.shouldSkip(config)) {
                    onSilenceDetected(silenceSegment)
                }
            }
            
            silenceStartTime = null
        }
    }

    private fun calculateRMSVolume(samples: List<Float>): Float {
        if (samples.isEmpty()) return 0f
        
        val sumOfSquares = samples.map { it * it }.sum()
        return sqrt(sumOfSquares / samples.size)
    }

    private fun calculateConfidenceScore(volumeDb: Float, durationMs: Long): Float {
        val volumeConfidence = when {
            volumeDb <= -50.0f -> 1.0f
            volumeDb <= -40.0f -> 0.8f
            volumeDb <= -30.0f -> 0.6f
            else -> 0.3f
        }
        
        val durationConfidence = when {
            durationMs >= 3000L -> 1.0f
            durationMs >= 2000L -> 0.8f
            durationMs >= 1000L -> 0.6f
            else -> 0.4f
        }
        
        return (volumeConfidence + durationConfidence) / 2.0f
    }

    private fun convertByteBufferToShortArray(buffer: ByteBuffer): ShortArray {
        val shortBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val shortArray = ShortArray(shortBuffer.remaining())
        shortBuffer.get(shortArray)
        return shortArray
    }

    private fun convertShortArrayToByteBuffer(shortArray: ShortArray): ByteBuffer {
        val byteBuffer = ByteBuffer.allocate(shortArray.size * 2)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        
        val shortBuffer = byteBuffer.asShortBuffer()
        shortBuffer.put(shortArray)
        
        return byteBuffer
    }

    companion object {
        fun createAnalysisResult(
            segments: List<SilenceSegment>,
            processingTimeMs: Long,
            averageVolumeDb: Float
        ): AudioAnalysisResult {
            return AudioAnalysisResult(
                silenceSegments = segments,
                totalSilenceDurationMs = segments.sumOf { it.durationMs },
                averageVolumeDb = averageVolumeDb,
                processingTimeMs = processingTimeMs
            )
        }
    }
}