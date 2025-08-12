package dev.orangecast.shared.data.storage

import dev.orangecast.shared.domain.model.AudioEnhancement

expect class PreferencesStorage {
    suspend fun saveCurrentEnhancement(enhancement: AudioEnhancement)
    suspend fun getCurrentEnhancement(): AudioEnhancement?
    suspend fun saveUserPreset(name: String, enhancement: AudioEnhancement)
    suspend fun getUserPresets(): Map<String, AudioEnhancement>
    suspend fun deleteUserPreset(name: String)
    suspend fun clearAllPresets()
}