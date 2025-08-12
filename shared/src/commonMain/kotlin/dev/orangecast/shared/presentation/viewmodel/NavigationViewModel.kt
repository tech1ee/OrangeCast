package dev.orangecast.shared.presentation.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import dev.orangecast.shared.domain.model.Podcast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel {
    private val _selectedTab = MutableStateFlow(NavigationTab.DISCOVER)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()
    
    private val _showPodcastDetails = MutableStateFlow<Podcast?>(null)
    val showPodcastDetails: StateFlow<Podcast?> = _showPodcastDetails.asStateFlow()
    
    private val _showFullPlayer = MutableStateFlow(false)
    val showFullPlayer: StateFlow<Boolean> = _showFullPlayer.asStateFlow()
    
    
    fun selectTab(tab: NavigationTab) {
        try {
            
            _showPodcastDetails.value = null
            _showFullPlayer.value = false
            
            _selectedTab.value = tab
            
        } catch (e: Exception) {
            
        }
    }
    
    fun showPodcastDetails(podcast: Podcast) {
        try {
            _showPodcastDetails.value = podcast
        } catch (e: Exception) {
            
        }
    }
    
    fun showFullPlayer() {
        try {
            _showFullPlayer.value = true
        } catch (e: Exception) {
            
        }
    }
    
    fun hideFullPlayer() {
        try {
            _showFullPlayer.value = false
        } catch (e: Exception) {
            
        }
    }
    
    fun navigateBack() {
        try {
            when {
                _showFullPlayer.value -> _showFullPlayer.value = false
                _showPodcastDetails.value != null -> _showPodcastDetails.value = null
                else -> {}
            }
        } catch (e: Exception) {
            
        }
    }
    
    fun resetState() {
        try {
            _selectedTab.value = NavigationTab.DISCOVER
            _showPodcastDetails.value = null
            _showFullPlayer.value = false
        } catch (e: Exception) {
            
        }
    }
}

enum class NavigationTab(
    val title: String,
    val icon: ImageVector
) {
    DISCOVER("Discover", Icons.Default.Home),
    SEARCH("Search", Icons.Default.Search),
    LIBRARY("Library", Icons.Default.LibraryBooks)
}