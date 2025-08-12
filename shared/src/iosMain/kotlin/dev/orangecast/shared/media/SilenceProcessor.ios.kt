package dev.orangecast.shared.media

import dev.orangecast.shared.domain.model.AudioAnalysisResult
import dev.orangecast.shared.domain.model.SilenceDetectionConfig
import dev.orangecast.shared.domain.model.SilenceSegment
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.AVFoundation.*
import platform.Foundation.*
import kotlin.math.log10
import kotlin.math.sqrt

@OptIn(ExperimentalForeignApi::class)
class IosSilenceProcessor(
    private val config: SilenceDetectionConfig,
    private val onSilenceDetected: (SilenceSegment) -> Unit,
    private val scope: CoroutineScope
) {

    private var audioEngine: AVAudioEngine? = null
    private var playerNode: AVAudioPlayerNode? = null
    private var audioUnit: AVAudioUnit? = null
    private var isProcessing = false
    
    private var silenceStartTime: Double? = null
    private var currentTime: Double = 0.0
    private val analysisWindowDuration = 0.1 // 100ms
    private var audioBuffer = mutableListOf<Float>()

    fun startProcessing() {
        setupAudioEngine()
        isProcessing = true
    }

    fun stopProcessing() {
        isProcessing = false
        audioEngine?.stop()
        audioEngine = null
        playerNode = null
        audioUnit = null
        audioBuffer.clear()
        silenceStartTime = null
        currentTime = 0.0
    }

    private fun setupAudioEngine() {
        audioEngine = AVAudioEngine()
        playerNode = AVAudioPlayerNode()
        
        val engine = audioEngine!!
        val player = playerNode!!
        
        engine.attachNode(player)
        
        val format = AVAudioFormat(
            standardFormatWithSampleRate = 44100.0,
            channels = 2u
        )
        
        engine.connect(player, engine.outputNode, format)
        
        installAudioTap(format)
        
        try {
            engine.startAndReturnError(null)
        } catch (e: Exception) {
        }
    }

    private fun installAudioTap(format: AVAudioFormat?) {
        format ?: return
        
        val bufferSize = 4096u
        
        playerNode?.installTapOnBusWithBufferSizeFormatBlock(
            0u,
            bufferSize,
            format
        ) { buffer, _ ->
            if (!isProcessing) return@installTapOnBusWithBufferSizeFormatBlock
            
            scope.launch {
                processAudioBuffer(buffer)
            }
        }
    }

    private suspend fun processAudioBuffer(buffer: AVAudioPCMBuffer?) = withContext(Dispatchers.Default) {
        buffer ?: return@withContext
        
        val frameLength = buffer.frameLength.toInt()
        if (frameLength == 0) return@withContext
        
        val startProcessingTime = NSDate().timeIntervalSince1970
        
        val channelData = buffer.floatChannelData ?: return@withContext
        val leftChannel = channelData[0] ?: return@withContext
        
        for (i in 0 until frameLength) {
            val sample = leftChannel[i]
            audioBuffer.add(sample)
            
            currentTime += 1.0 / 44100.0
            
            if (audioBuffer.size >= (44100 * analysisWindowDuration).toInt()) {
                analyzeAudioWindow()
                audioBuffer.clear()
            }
        }
        
        val processingTime = NSDate().timeIntervalSince1970 - startProcessingTime
        if (processingTime > 0.1) {
        }
    }

    private fun analyzeAudioWindow() {
        val rmsVolume = calculateRMSVolume(audioBuffer)
        val volumeDb = if (rmsVolume > 0) 20 * log10(rmsVolume.toDouble()).toFloat() else -60.0f
        
        val isSilence = volumeDb <= config.silenceThresholdDb
        
        if (isSilence && silenceStartTime == null) {
            silenceStartTime = currentTime
        } else if (!isSilence && silenceStartTime != null) {
            val silenceDuration = ((currentTime - silenceStartTime!!) * 1000).toLong()
            
            if (silenceDuration >= config.minimumSilenceDurationMs) {
                val silenceSegment = SilenceSegment(
                    startTimeMs = (silenceStartTime!! * 1000).toLong(),
                    endTimeMs = (currentTime * 1000).toLong(),
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

    fun performRealTimeAnalysis(audioData: FloatArray, sampleRate: Double): List<SilenceSegment> {
        val segments = mutableListOf<SilenceSegment>()
        val windowSize = (sampleRate * analysisWindowDuration).toInt()
        
        for (i in audioData.indices step windowSize) {
            val windowEnd = (i + windowSize).coerceAtMost(audioData.size)
            val window = audioData.sliceArray(i until windowEnd)
            
            val rmsVolume = calculateRMSVolume(window.toList())
            val volumeDb = if (rmsVolume > 0) 20 * log10(rmsVolume.toDouble()).toFloat() else -60.0f
            
            if (volumeDb <= config.silenceThresholdDb) {
                val startTimeMs = (i / sampleRate * 1000).toLong()
                val endTimeMs = (windowEnd / sampleRate * 1000).toLong()
                val duration = endTimeMs - startTimeMs
                
                if (duration >= config.minimumSilenceDurationMs) {
                    segments.add(
                        SilenceSegment(
                            startTimeMs = startTimeMs,
                            endTimeMs = endTimeMs,
                            averageVolumeDb = volumeDb,
                            confidenceScore = calculateConfidenceScore(volumeDb, duration)
                        )
                    )
                }
            }
        }
        
        return mergeContinuousSegments(segments)
    }

    private fun mergeContinuousSegments(segments: List<SilenceSegment>): List<SilenceSegment> {
        if (segments.isEmpty()) return emptyList()
        
        val merged = mutableListOf<SilenceSegment>()
        var current = segments.first()
        
        for (i in 1 until segments.size) {
            val next = segments[i]
            
            if (next.startTimeMs - current.endTimeMs <= config.minimumNonSilenceDurationMs) {
                current = SilenceSegment(
                    startTimeMs = current.startTimeMs,
                    endTimeMs = next.endTimeMs,
                    averageVolumeDb = (current.averageVolumeDb + next.averageVolumeDb) / 2,
                    confidenceScore = (current.confidenceScore + next.confidenceScore) / 2
                )
            } else {
                merged.add(current)
                current = next
            }
        }
        
        merged.add(current)
        return merged
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