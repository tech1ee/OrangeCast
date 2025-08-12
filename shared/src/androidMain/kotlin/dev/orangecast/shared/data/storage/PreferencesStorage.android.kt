package dev.orangecast.shared.data.storage

import android.content.Context
import android.content.SharedPreferences
import dev.orangecast.shared.domain.model.AudioEnhancement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

actual class PreferencesStorage(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    
    actual suspend fun saveCurrentEnhancement(enhancement: AudioEnhancement) {
        withContext(Dispatchers.IO) {
            val enhancementJson = json.encodeToString(enhancement)
            prefs.edit()
                .putString(KEY_CURRENT_ENHANCEMENT, enhancementJson)
                .apply()
        }
    }
    
    actual suspend fun getCurrentEnhancement(): AudioEnhancement? {
        return withContext(Dispatchers.IO) {
            try {
                val enhancementJson = prefs.getString(KEY_CURRENT_ENHANCEMENT, null)
                if (enhancementJson != null) {
                    json.decodeFromString<AudioEnhancement>(enhancementJson)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
    
    actual suspend fun saveUserPreset(name: String, enhancement: AudioEnhancement) {
        withContext(Dispatchers.IO) {
            val presetsJson = prefs.getString(KEY_USER_PRESETS, "{}")
            val presets = try {
                json.decodeFromString<Map<String, AudioEnhancement>>(presetsJson!!)
            } catch (e: Exception) {
                emptyMap()
            }
            
            val updatedPresets = presets + (name to enhancement)
            val updatedJson = json.encodeToString(updatedPresets)
            
            prefs.edit()
                .putString(KEY_USER_PRESETS, updatedJson)
                .apply()
        }
    }
    
    actual suspend fun getUserPresets(): Map<String, AudioEnhancement> {
        return withContext(Dispatchers.IO) {
            try {
                val presetsJson = prefs.getString(KEY_USER_PRESETS, "{}")
                json.decodeFromString<Map<String, AudioEnhancement>>(presetsJson!!)
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }
    
    actual suspend fun deleteUserPreset(name: String) {
        withContext(Dispatchers.IO) {
            val presets = getUserPresets()
            val updatedPresets = presets - name
            val updatedJson = json.encodeToString(updatedPresets)
            
            prefs.edit()
                .putString(KEY_USER_PRESETS, updatedJson)
                .apply()
        }
    }
    
    actual suspend fun clearAllPresets() {
        withContext(Dispatchers.IO) {
            prefs.edit()
                .remove(KEY_USER_PRESETS)
                .remove(KEY_CURRENT_ENHANCEMENT)
                .apply()
        }
    }
    
    companion object {
        private const val PREFS_NAME = "audio_enhancement_prefs"
        private const val KEY_CURRENT_ENHANCEMENT = "current_enhancement"
        private const val KEY_USER_PRESETS = "user_presets"
    }
}