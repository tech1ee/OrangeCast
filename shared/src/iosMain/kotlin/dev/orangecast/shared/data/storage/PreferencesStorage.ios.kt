package dev.orangecast.shared.data.storage

import dev.orangecast.shared.domain.model.AudioEnhancement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

actual class PreferencesStorage {
    
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val json = Json { ignoreUnknownKeys = true }
    
    actual suspend fun saveCurrentEnhancement(enhancement: AudioEnhancement) {
        withContext(Dispatchers.Default) {
            val enhancementJson = json.encodeToString(enhancement)
            userDefaults.setObject(enhancementJson, KEY_CURRENT_ENHANCEMENT)
            userDefaults.synchronize()
        }
    }
    
    actual suspend fun getCurrentEnhancement(): AudioEnhancement? {
        return withContext(Dispatchers.Default) {
            try {
                val enhancementJson = userDefaults.stringForKey(KEY_CURRENT_ENHANCEMENT)
                if (enhancementJson != null) {
                    json.decodeFromString<AudioEnhancement>(enhancementJson)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
    
    actual suspend fun saveUserPreset(name: String, enhancement: AudioEnhancement) {
        withContext(Dispatchers.Default) {
            val presetsJson = userDefaults.stringForKey(KEY_USER_PRESETS) ?: "{}"
            val presets = try {
                json.decodeFromString<Map<String, AudioEnhancement>>(presetsJson)
            } catch (e: Exception) {
                emptyMap()
            }
            
            val updatedPresets = presets + (name to enhancement)
            val updatedJson = json.encodeToString(updatedPresets)
            
            userDefaults.setObject(updatedJson, KEY_USER_PRESETS)
            userDefaults.synchronize()
        }
    }
    
    actual suspend fun getUserPresets(): Map<String, AudioEnhancement> {
        return withContext(Dispatchers.Default) {
            try {
                val presetsJson = userDefaults.stringForKey(KEY_USER_PRESETS) ?: "{}"
                json.decodeFromString<Map<String, AudioEnhancement>>(presetsJson)
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }
    
    actual suspend fun deleteUserPreset(name: String) {
        withContext(Dispatchers.Default) {
            val presets = getUserPresets()
            val updatedPresets = presets - name
            val updatedJson = json.encodeToString(updatedPresets)
            
            userDefaults.setObject(updatedJson, KEY_USER_PRESETS)
            userDefaults.synchronize()
        }
    }
    
    actual suspend fun clearAllPresets() {
        withContext(Dispatchers.Default) {
            userDefaults.removeObjectForKey(KEY_USER_PRESETS)
            userDefaults.removeObjectForKey(KEY_CURRENT_ENHANCEMENT)
            userDefaults.synchronize()
        }
    }
    
    companion object {
        private const val KEY_CURRENT_ENHANCEMENT = "current_enhancement"
        private const val KEY_USER_PRESETS = "user_presets"
    }
}