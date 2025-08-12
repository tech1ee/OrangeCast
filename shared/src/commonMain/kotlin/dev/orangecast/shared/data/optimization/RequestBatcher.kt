package dev.orangecast.shared.data.optimization

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest

class RequestBatcher<T, R>(
    private val batchSize: Int = 10,
    private val windowMs: Long = 100,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val requestQueue = mutableListOf<BatchRequest<T, R>>()
    private val mutex = Mutex()
    private var batchingJob: Job? = null
    
    private val _batchReady = MutableSharedFlow<List<BatchRequest<T, R>>>()
    val batchReady = _batchReady.asSharedFlow()
    
    init {
        scope.launch {
            batchReady.collectLatest { batch ->
                processBatch(batch)
            }
        }
    }
    
    suspend fun addRequest(request: T, callback: (Result<R>) -> Unit) {
        val batchRequest = BatchRequest(request, callback)
        
        mutex.withLock {
            requestQueue.add(batchRequest)
            
            if (requestQueue.size >= batchSize) {
                flushBatch()
            } else {
                scheduleBatchFlush()
            }
        }
    }
    
    private fun scheduleBatchFlush() {
        batchingJob?.cancel()
        batchingJob = scope.launch {
            delay(windowMs)
            mutex.withLock {
                if (requestQueue.isNotEmpty()) {
                    flushBatch()
                }
            }
        }
    }
    
    private suspend fun flushBatch() {
        if (requestQueue.isEmpty()) return
        
        val currentBatch = requestQueue.toList()
        requestQueue.clear()
        _batchReady.emit(currentBatch)
    }
    
    private suspend fun processBatch(batch: List<BatchRequest<T, R>>) {
    }
    
    fun release() {
        batchingJob?.cancel()
        scope.launch {
            mutex.withLock {
                requestQueue.forEach { request ->
                    request.callback(Result.failure(Exception("Request batcher was released")))
                }
                requestQueue.clear()
            }
        }
    }
}

data class BatchRequest<T, R>(
    val request: T,
    val callback: (Result<R>) -> Unit
)