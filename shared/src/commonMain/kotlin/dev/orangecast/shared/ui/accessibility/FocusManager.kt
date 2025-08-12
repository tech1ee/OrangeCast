package dev.orangecast.shared.ui.accessibility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import kotlinx.coroutines.delay

object AccessibilityFocusManager {
    
    @Composable
    fun rememberAccessibleFocusRequester(): FocusRequester {
        return remember { FocusRequester() }
    }
    
    @Composable
    fun AutoFocusOnAppear(
        focusRequester: FocusRequester,
        delayMs: Long = 100
    ) {
        LaunchedEffect(focusRequester) {
            delay(delayMs)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
            }
        }
    }
    
    fun Modifier.accessibleFocus(
        focusRequester: FocusRequester,
        traversalIndex: Float = 0f
    ): Modifier {
        return this
            .focusRequester(focusRequester)
            .semantics {
                this.traversalIndex = traversalIndex
            }
    }
    
    fun createFocusOrderedModifier(order: Int): Modifier {
        return Modifier.semantics {
            traversalIndex = order.toFloat()
        }
    }
}

enum class AccessibilityAction {
    ACTIVATE,
    DISMISS,
    EXPAND,
    COLLAPSE,
    SCROLL_FORWARD,
    SCROLL_BACKWARD
}

class AccessibilityAnnouncer {
    private val announcements = mutableStateOf<String?>(null)
    
    @Composable
    fun ObserveAnnouncements() {
        val currentAnnouncement = announcements.value
        
        LaunchedEffect(currentAnnouncement) {
            if (currentAnnouncement != null) {
                delay(100)
                announcements.value = null
            }
        }
    }
    
    fun announce(message: String) {
        announcements.value = message
    }
}

@Composable
fun rememberAccessibilityAnnouncer(): AccessibilityAnnouncer {
    return remember { AccessibilityAnnouncer() }
}