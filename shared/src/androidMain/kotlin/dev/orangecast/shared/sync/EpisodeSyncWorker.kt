package dev.orangecast.shared.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.orangecast.shared.domain.repository.PodcastRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class EpisodeSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val podcastRepository: PodcastRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Napier.i("Starting episode sync worker", tag = "EpisodeSyncWorker")
            
            // Sync episodes for all subscribed podcasts
            val syncResult = podcastRepository.syncEpisodesForSubscribedPodcasts()
            
            if (syncResult.isSuccess) {
                Napier.i("Episode sync completed successfully", tag = "EpisodeSyncWorker")
                Result.success()
            } else {
                val error = syncResult.exceptionOrNull()
                Napier.e("Episode sync failed: ${error?.message}", error, tag = "EpisodeSyncWorker")
                Result.retry()
            }
        } catch (e: Exception) {
            Napier.e("Episode sync worker failed with exception", e, tag = "EpisodeSyncWorker")
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "episode_sync_work"
        
        fun startPeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<EpisodeSyncWorker>(
                repeatInterval = 6, // 6 hours
                repeatIntervalTimeUnit = TimeUnit.HOURS,
                flexTimeInterval = 1, // 1 hour flex
                flexTimeIntervalUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
            
            Napier.i("Periodic episode sync scheduled", tag = "EpisodeSyncWorker")
        }
        
        fun stopPeriodicSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Napier.i("Periodic episode sync cancelled", tag = "EpisodeSyncWorker")
        }
    }
}