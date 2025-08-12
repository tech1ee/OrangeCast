package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.BufferConfiguration
import dev.orangecast.shared.domain.repository.NetworkMonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdaptiveBufferUseCase(
    private val networkMonitorRepository: NetworkMonitorRepository
) {
    
    fun observeOptimalBufferConfiguration(): Flow<BufferConfiguration> {
        return networkMonitorRepository.observeNetworkType()
            .map { networkType ->
                BufferConfiguration.createOptimalConfiguration(networkType)
            }
    }

    suspend fun getCurrentOptimalConfiguration(): BufferConfiguration {
        val networkType = networkMonitorRepository.getCurrentNetworkType()
        return BufferConfiguration.createOptimalConfiguration(networkType)
    }

    suspend fun shouldRebuffer(currentBufferPercentage: Int, networkType: String?): Boolean {
        val isNetworkAvailable = networkMonitorRepository.isNetworkAvailable()
        
        if (!isNetworkAvailable) {
            return false
        }

        return when {
            currentBufferPercentage < 10 -> true
            currentBufferPercentage < 25 && networkType == "CELLULAR_SLOW" -> true
            else -> false
        }
    }
}