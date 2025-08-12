package dev.orangecast.shared.data.repository

import dev.orangecast.shared.domain.model.NetworkType
import dev.orangecast.shared.domain.repository.NetworkMonitorRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.CoreTelephony.CTTelephonyNetworkInfo
import platform.Network.*
import platform.SystemConfiguration.*
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
class IosNetworkMonitorRepository : NetworkMonitorRepository {

    private val reachability = SCNetworkReachabilityCreateWithName(null, "8.8.8.8")
    
    override fun observeNetworkType(): Flow<NetworkType> = callbackFlow {
        val callback: SCNetworkReachabilityCallBack = { _, flags, _ ->
            trySend(determineNetworkTypeFromFlags(flags))
        }
        
        var callbackRef: SCNetworkReachabilityCallBack? = callback
        
        SCNetworkReachabilitySetCallback(reachability, callbackRef, null)
        SCNetworkReachabilityScheduleWithRunLoop(reachability, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode)
        
        trySend(getCurrentNetworkType())
        
        awaitClose {
            SCNetworkReachabilityUnscheduleFromRunLoop(reachability, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode)
            callbackRef = null
        }
    }.distinctUntilChanged()

    override suspend fun getCurrentNetworkType(): NetworkType {
        return determineNetworkType()
    }

    override suspend fun isNetworkAvailable(): Boolean {
        return determineNetworkType() != NetworkType.UNKNOWN
    }
    
    private fun determineNetworkType(): NetworkType {
        var flags = SCNetworkReachabilityFlags.MIN_VALUE
        
        if (!SCNetworkReachabilityGetFlags(reachability, flags.ptr)) {
            return NetworkType.UNKNOWN
        }
        
        return determineNetworkTypeFromFlags(flags)
    }
    
    private fun determineNetworkTypeFromFlags(flags: SCNetworkReachabilityFlags): NetworkType {
        if ((flags and kSCNetworkReachabilityFlagsReachable) == 0u) {
            return NetworkType.UNKNOWN
        }
        
        return when {
            (flags and kSCNetworkReachabilityFlagsIsWWAN) != 0u -> {
                determineCellularNetworkType()
            }
            (flags and kSCNetworkReachabilityFlagsConnectionRequired) == 0u -> {
                NetworkType.WIFI
            }
            (flags and (kSCNetworkReachabilityFlagsConnectionOnDemand or kSCNetworkReachabilityFlagsConnectionOnTraffic)) != 0u &&
            (flags and kSCNetworkReachabilityFlagsInterventionRequired) == 0u -> {
                NetworkType.WIFI
            }
            else -> NetworkType.UNKNOWN
        }
    }
    
    private fun determineCellularNetworkType(): NetworkType {
        val telephonyInfo = CTTelephonyNetworkInfo()
        val currentRadioAccessTechnology = telephonyInfo.currentRadioAccessTechnology
        
        return when (currentRadioAccessTechnology) {
            CTRadioAccessTechnologyLTE -> NetworkType.CELLULAR_FAST
            CTRadioAccessTechnologyHSDPA,
            CTRadioAccessTechnologyHSUPA,
            CTRadioAccessTechnologyWCDMA -> NetworkType.CELLULAR_FAST
            CTRadioAccessTechnologyEdge,
            CTRadioAccessTechnologyGPRS -> NetworkType.CELLULAR_SLOW
            else -> NetworkType.CELLULAR_SLOW
        }
    }
}