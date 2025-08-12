# Audio Enhancement System

## Overview

The Audio Enhancement System provides real-time audio processing capabilities for podcast playback, including volume normalization, voice enhancement, frequency equalization, and noise reduction.

## Architecture

### Core Components

- **AudioEnhancement**: Data model containing all enhancement parameters
- **AudioProcessor**: Platform-specific audio processing implementation
- **AudioEnhancementRepository**: Data persistence and state management
- **AudioEnhancementUseCase**: Business logic for audio enhancement operations

### Key Features

#### 1. Volume Control
- **Volume Boost**: -20dB to +20dB range with automatic gain limiting
- **Normalization**: Dynamic volume leveling for consistent audio levels
- **Dynamic Range Compression**: Reduces volume differences for clearer speech

#### 2. Frequency Enhancement
- **Voice Boost**: 0-100% enhancement of vocal frequencies (250Hz-2kHz)
- **Bass Boost**: ±30dB adjustment of low frequencies (<250Hz)
- **Treble Boost**: ±30dB adjustment of high frequencies (>4kHz)

#### 3. Noise Reduction
- **Adaptive Noise Reduction**: 0-100% noise suppression
- **Real-time Processing**: Platform-optimized noise filtering

#### 4. Preset System
- **Built-in Presets**: Voice Enhanced, Music Optimized, Night Mode, Clarity Max
- **Custom Presets**: User-created enhancement profiles with persistence
- **Smart Recommendations**: AI-driven preset suggestions based on content analysis

## Usage

### Basic Enhancement

```kotlin
val useCase = AudioEnhancementUseCase(repository)

// Apply voice enhancement
val voiceEnhancement = AudioEnhancement.createForVoiceContent()
useCase.applyEnhancement(voiceEnhancement)

// Create custom enhancement
val customEnhancement = useCase.createCustomEnhancement(
    volumeBoost = 5f,
    voiceBoost = 30f,
    bassBoost = -10f,
    noiseReduction = 20f
)
```

### Preset Management

```kotlin
// Apply preset
useCase.applyPreset("Voice Enhanced")

// Save custom preset
useCase.saveCustomPreset("My Voice Settings", customEnhancement)

// Get all presets
val allPresets = useCase.getAllPresets()

// Find best preset for current audio
val bestPreset = useCase.findBestPresetForCurrentAudio()
```

### Real-time Adjustments

```kotlin
var enhancement = useCase.getCurrentEnhancement()

// Adjust volume
enhancement = useCase.adjustVolumeBoost(enhancement, 2f)

// Toggle features
enhancement = useCase.toggleNormalization(enhancement)
enhancement = useCase.toggleDynamicRangeCompression(enhancement)

// Apply changes
useCase.applyEnhancement(enhancement)
```

## Platform Implementation

### Android (ExoPlayer + AudioFX)
- Uses Android AudioFX framework (Equalizer, BassBoost, LoudnessEnhancer)
- Integrates with ExoPlayer's audio processing pipeline
- Supports hardware-accelerated effects when available

### iOS (AVAudioEngine + AVAudioUnit)
- Uses AVAudioEngine with AVAudioUnitEQ and AVAudioUnitReverb
- Real-time audio processing with low latency
- Optimized for iOS audio session management

## Performance Characteristics

### Latency
- **Normal Mode**: <50ms processing latency
- **Low Latency Mode**: <20ms processing latency (iOS), <10ms (Android)

### CPU Usage
- **Default Enhancement**: ~5-10% CPU usage
- **Full Enhancement**: ~15-25% CPU usage
- **Optimization**: Automatic scaling based on device capabilities

### Memory Usage
- **Static Memory**: ~2MB for enhancement engine
- **Dynamic Memory**: ~1MB per active enhancement profile
- **Preset Storage**: ~100KB for all presets

## Content Analysis

The system automatically analyzes audio content to provide intelligent enhancement recommendations:

### Audio Classification
- **Voice Only**: Optimizes for speech clarity and vocal frequency enhancement
- **Music Only**: Enhances musical elements with balanced frequency response
- **Mixed Content**: Provides balanced enhancement for varied content types

### Analysis Metrics
- **Voice Frequency Ratio**: Percentage of content in vocal frequencies
- **Music Frequency Ratio**: Percentage of content in musical frequencies  
- **Dynamic Range**: Difference between loudest and quietest parts
- **Noise Floor**: Background noise level measurement

## Integration Guidelines

### 1. Repository Setup
```kotlin
val audioProcessor = AudioProcessor()
val preferencesStorage = PreferencesStorage(context)
val repository = AudioEnhancementRepositoryImpl(audioProcessor, preferencesStorage)
```

### 2. Use Case Integration
```kotlin
val useCase = AudioEnhancementUseCase(repository)

// Initialize with media session
audioProcessor.setAudioSessionId(mediaPlayer.audioSessionId)
```

### 3. State Observation
```kotlin
useCase.observeProcessingState()
    .collect { processingState ->
        // Update UI with processing state
        updateProcessingIndicator(processingState.isProcessing)
        updateCpuUsage(processingState.cpuUsagePercent)
    }
```

## Best Practices

### 1. Performance Optimization
- Enable low-latency mode for real-time applications
- Use built-in presets when possible to reduce processing overhead
- Monitor CPU usage and adjust enhancement complexity accordingly

### 2. User Experience
- Provide visual feedback during processing operations
- Implement smooth transitions between enhancement states
- Allow users to compare original and enhanced audio

### 3. Content Adaptation
- Use content analysis for automatic preset recommendations
- Adjust enhancement aggressiveness based on content type
- Provide context-aware enhancement suggestions

## Validation and Constraints

All enhancement parameters are automatically validated:

- **Volume Boost**: Constrained to -20dB to +20dB
- **Voice/Bass/Treble Boost**: Safe ranges to prevent audio distortion
- **Percentage Values**: Clamped to 0-100% range
- **Boolean Flags**: Type-safe toggle operations

## Error Handling

The system gracefully handles various error conditions:

- **Platform Unavailability**: Falls back to software processing
- **Audio Session Errors**: Automatic retry with fallback configurations
- **Processing Failures**: Maintains previous enhancement state
- **Storage Errors**: Uses in-memory fallback for critical operations

## Testing

Comprehensive test coverage includes:

- **Unit Tests**: Individual component behavior validation
- **Integration Tests**: End-to-end workflow testing
- **Platform Tests**: Android and iOS specific functionality
- **Performance Tests**: Latency, CPU usage, and memory validation
- **Real Audio Tests**: Validation with actual podcast content