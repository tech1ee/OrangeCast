package dev.orangecast.shared.domain.repository

import dev.orangecast.shared.domain.model.NetworkType
import kotlinx.coroutines.flow.Flow

interface NetworkMonitorRepository {
    fun observeNetworkType(): Flow<NetworkType>
    suspend fun getCurrentNetworkType(): NetworkType
    suspend fun isNetworkAvailable(): Boolean
}