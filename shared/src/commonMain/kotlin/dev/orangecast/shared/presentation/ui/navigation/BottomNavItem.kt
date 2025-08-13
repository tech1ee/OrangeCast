package dev.orangecast.shared.presentation.ui.navigation

enum class BottomNavItem(val route: String, val title: String, val icon: String) {
    DISCOVER("discover", "Discover", "🏠"),
    NEW_EPISODES("episodes", "New Episodes", "📺"),
    LIBRARY("library", "Library", "❤️")
}