package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun setThemeMode(mode: ThemeMode): Result<Unit>
    suspend fun getThemeMode(): ThemeMode
    fun observeThemeMode(): Flow<ThemeMode>
}