package dev.orangecast.shared.data.repository

import dev.orangecast.shared.data.config.SecureStorage
import dev.orangecast.shared.domain.model.ThemeMode
import dev.orangecast.shared.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeRepositoryImpl(
    private val secureStorage: SecureStorage
) : ThemeRepository {
    
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    
    companion object {
        private const val THEME_MODE_KEY = "theme_mode"
    }
    
    init {
        loadThemeMode()
    }
    
    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> {
        return try {
            secureStorage.putString(THEME_MODE_KEY, mode.name)
            _themeMode.value = mode
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getThemeMode(): ThemeMode {
        return _themeMode.value
    }
    
    override fun observeThemeMode(): Flow<ThemeMode> {
        return _themeMode.asStateFlow()
    }
    
    private fun loadThemeMode() {
        try {
            kotlinx.coroutines.runBlocking {
                val storedMode = secureStorage.getString(THEME_MODE_KEY)
                if (storedMode != null) {
                    _themeMode.value = ThemeMode.valueOf(storedMode)
                }
            }
        } catch (e: Exception) {
            _themeMode.value = ThemeMode.SYSTEM
        }
    }
}