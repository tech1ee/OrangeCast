package dev.orangecast.shared.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * OrangeCast Design System Colors
 * Modern palette with soft, warm orange tones for 2024
 */
object OrangeCastColors {
    
    // Primary Brand Colors
    val PrimaryOrange = Color(0xFFFF8A65)      // Soft coral-orange
    val SecondaryOrange = Color(0xFFFFAB91)    // Light peach
    val AccentOrangeLight = Color(0xFFFF7043)  // Warm tangerine (light theme)
    val AccentOrangeDark = Color(0xFFFFB74D)   // Brighter orange (dark theme)
    
    // Light Theme
    object Light {
        val Primary = PrimaryOrange
        val Secondary = SecondaryOrange
        val Accent = AccentOrangeLight
        
        val Background = Color(0xFFFAFAFA)
        val Surface = Color(0xFFFFFFFF)
        val SurfaceVariant = Color(0xFFF5F5F5)
        
        val TextPrimary = Color(0xFF212121)
        val TextSecondary = Color(0xFF757575)
        val TextDisabled = Color(0xFFBDBDBD)
        
        val Divider = Color(0xFFE0E0E0)
        val Border = Color(0xFFEEEEEE)
        val Ripple = Color(0x20FF8A65)
    }
    
    // Dark Theme
    object Dark {
        val Primary = PrimaryOrange
        val Secondary = SecondaryOrange
        val Accent = AccentOrangeDark
        
        val Background = Color(0xFF121212)
        val Surface = Color(0xFF1E1E1E)
        val SurfaceVariant = Color(0xFF2C2C2C)
        
        val TextPrimary = Color(0xFFFFFFFF)
        val TextSecondary = Color(0xFFB3B3B3)
        val TextDisabled = Color(0xFF666666)
        
        val Divider = Color(0xFF373737)
        val Border = Color(0xFF424242)
        val Ripple = Color(0x30FF8A65)
    }
    
    // Semantic Colors (Universal)
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFFC107)
    val Error = Color(0xFFF44336)
    val Info = Color(0xFF2196F3)
    
    // Supporting Colors
    val SageGreen = Color(0xFFA5B4A7)
    val WarmBeige = Color(0xFFF5F1EB)
    val SoftBlue = Color(0xFFB8C5D6)
}