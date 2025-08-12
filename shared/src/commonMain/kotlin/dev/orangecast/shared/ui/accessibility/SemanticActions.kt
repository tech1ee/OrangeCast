package dev.orangecast.shared.ui.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics

object SemanticActions {
    fun Modifier.podcastItemActions(
        onPlay: (() -> Unit)? = null,
        onSubscribe: (() -> Unit)? = null,
        onUnsubscribe: (() -> Unit)? = null,
        onDownload: (() -> Unit)? = null,
        onShare: (() -> Unit)? = null,
        isSubscribed: Boolean = false
    ): Modifier = this.semantics {
        customActions = buildList {
            onPlay?.let { action ->
                add(CustomAccessibilityAction("Play") { action(); true })
            }
            
            if (isSubscribed) {
                onUnsubscribe?.let { action ->
                    add(CustomAccessibilityAction("Unsubscribe") { action(); true })
                }
            } else {
                onSubscribe?.let { action ->
                    add(CustomAccessibilityAction("Subscribe") { action(); true })
                }
            }
            
            onDownload?.let { action ->
                add(CustomAccessibilityAction("Download") { action(); true })
            }
            
            onShare?.let { action ->
                add(CustomAccessibilityAction("Share") { action(); true })
            }
        }
    }
    
    fun Modifier.episodeItemActions(
        onPlay: (() -> Unit)? = null,
        onPause: (() -> Unit)? = null,
        onDownload: (() -> Unit)? = null,
        onRemoveDownload: (() -> Unit)? = null,
        onAddToQueue: (() -> Unit)? = null,
        onRemoveFromQueue: (() -> Unit)? = null,
        onShare: (() -> Unit)? = null,
        onMarkAsPlayed: (() -> Unit)? = null,
        onMarkAsUnplayed: (() -> Unit)? = null,
        isPlaying: Boolean = false,
        isDownloaded: Boolean = false,
        isInQueue: Boolean = false,
        isPlayed: Boolean = false
    ): Modifier = this.semantics {
        customActions = buildList {
            if (isPlaying) {
                onPause?.let { action ->
                    add(CustomAccessibilityAction("Pause") { action(); true })
                }
            } else {
                onPlay?.let { action ->
                    add(CustomAccessibilityAction("Play") { action(); true })
                }
            }
            
            if (isDownloaded) {
                onRemoveDownload?.let { action ->
                    add(CustomAccessibilityAction("Remove download") { action(); true })
                }
            } else {
                onDownload?.let { action ->
                    add(CustomAccessibilityAction("Download") { action(); true })
                }
            }
            
            if (isInQueue) {
                onRemoveFromQueue?.let { action ->
                    add(CustomAccessibilityAction("Remove from queue") { action(); true })
                }
            } else {
                onAddToQueue?.let { action ->
                    add(CustomAccessibilityAction("Add to queue") { action(); true })
                }
            }
            
            if (isPlayed) {
                onMarkAsUnplayed?.let { action ->
                    add(CustomAccessibilityAction("Mark as unplayed") { action(); true })
                }
            } else {
                onMarkAsPlayed?.let { action ->
                    add(CustomAccessibilityAction("Mark as played") { action(); true })
                }
            }
            
            onShare?.let { action ->
                add(CustomAccessibilityAction("Share") { action(); true })
            }
        }
    }
    
    fun Modifier.audioPlayerActions(
        onPlay: (() -> Unit)? = null,
        onPause: (() -> Unit)? = null,
        onNext: (() -> Unit)? = null,
        onPrevious: (() -> Unit)? = null,
        onSeekForward: (() -> Unit)? = null,
        onSeekBackward: (() -> Unit)? = null,
        onToggleShuffle: (() -> Unit)? = null,
        onToggleRepeat: (() -> Unit)? = null,
        onShowQueue: (() -> Unit)? = null,
        onSpeedControl: (() -> Unit)? = null,
        isPlaying: Boolean = false,
        hasNext: Boolean = true,
        hasPrevious: Boolean = true,
        shuffleEnabled: Boolean = false,
        repeatEnabled: Boolean = false
    ): Modifier = this.semantics {
        customActions = buildList {
            if (isPlaying) {
                onPause?.let { action ->
                    add(CustomAccessibilityAction("Pause") { action(); true })
                }
            } else {
                onPlay?.let { action ->
                    add(CustomAccessibilityAction("Play") { action(); true })
                }
            }
            
            if (hasPrevious) {
                onPrevious?.let { action ->
                    add(CustomAccessibilityAction("Previous track") { action(); true })
                }
            }
            
            if (hasNext) {
                onNext?.let { action ->
                    add(CustomAccessibilityAction("Next track") { action(); true })
                }
            }
            
            onSeekBackward?.let { action ->
                add(CustomAccessibilityAction("Skip backward 30 seconds") { action(); true })
            }
            
            onSeekForward?.let { action ->
                add(CustomAccessibilityAction("Skip forward 30 seconds") { action(); true })
            }
            
            onToggleShuffle?.let { action ->
                val label = if (shuffleEnabled) "Turn off shuffle" else "Turn on shuffle"
                add(CustomAccessibilityAction(label) { action(); true })
            }
            
            onToggleRepeat?.let { action ->
                val label = if (repeatEnabled) "Turn off repeat" else "Turn on repeat"
                add(CustomAccessibilityAction(label) { action(); true })
            }
            
            onShowQueue?.let { action ->
                add(CustomAccessibilityAction("Show queue") { action(); true })
            }
            
            onSpeedControl?.let { action ->
                add(CustomAccessibilityAction("Change playback speed") { action(); true })
            }
        }
    }
    
    fun Modifier.searchActions(
        onClearSearch: (() -> Unit)? = null,
        onVoiceSearch: (() -> Unit)? = null,
        onFilterResults: (() -> Unit)? = null,
        hasQuery: Boolean = false
    ): Modifier = this.semantics {
        customActions = buildList {
            if (hasQuery) {
                onClearSearch?.let { action ->
                    add(CustomAccessibilityAction("Clear search") { action(); true })
                }
            }
            
            onVoiceSearch?.let { action ->
                add(CustomAccessibilityAction("Voice search") { action(); true })
            }
            
            onFilterResults?.let { action ->
                add(CustomAccessibilityAction("Filter results") { action(); true })
            }
        }
    }
    
    fun Modifier.navigationActions(
        onGoBack: (() -> Unit)? = null,
        onGoHome: (() -> Unit)? = null,
        onOpenMenu: (() -> Unit)? = null,
        onOpenSettings: (() -> Unit)? = null
    ): Modifier = this.semantics {
        customActions = buildList {
            onGoBack?.let { action ->
                add(CustomAccessibilityAction("Go back") { action(); true })
            }
            
            onGoHome?.let { action ->
                add(CustomAccessibilityAction("Go to home") { action(); true })
            }
            
            onOpenMenu?.let { action ->
                add(CustomAccessibilityAction("Open menu") { action(); true })
            }
            
            onOpenSettings?.let { action ->
                add(CustomAccessibilityAction("Open settings") { action(); true })
            }
        }
    }
}