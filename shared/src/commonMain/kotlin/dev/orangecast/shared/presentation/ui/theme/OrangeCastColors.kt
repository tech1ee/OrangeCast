package dev.orangecast.shared.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * OrangeCast Design System Colors
 * Minimal 2024 palette with soft orange accent and natural neutrals
 * Following 2024 trends: earthy, desaturated, warm and minimal
 */
object OrangeCastColors {
    
    // Primary Brand Colors - Soft terracotta-inspired orange
    val PrimaryOrange = Color(0xFFE07B56)      // Soft terracotta-orange (desaturated)
    val SecondaryOrange = Color(0xFFF0B193)    // Very light peach
    val AccentOrangeLight = Color(0xFFD96E47)  // Slightly deeper terracotta
    val AccentOrangeDark = Color(0xFFF2A373)   // Warm peachy glow for dark theme
    
    // Light Theme - Minimal and soft
    object Light {
        val Primary = PrimaryOrange
        val Secondary = SecondaryOrange
        val Accent = AccentOrangeLight
        
        val Background = Color(0xFFFCFCFC)      // Very soft off-white
        val Surface = Color(0xFFFFFFFE)         // Pure but warm white
        val SurfaceVariant = Color(0xFFF8F7F6)  // Warm neutral with beige hint
        
        val TextPrimary = Color(0xFF2A2926)     // Soft dark gray with warm undertone
        val TextSecondary = Color(0xFF6B6863)   // Muted warm gray
        val TextDisabled = Color(0xFFC4C1BC)    // Light warm gray
        
        val Divider = Color(0xFFEFEEED)         // Barely visible warm divider
        val Border = Color(0xFFF2F1F0)         // Subtle warm border
        val Ripple = Color(0x18E07B56)         // Very subtle orange ripple
    }
    
    // Dark Theme - Minimal and warm
    object Dark {
        val Primary = PrimaryOrange
        val Secondary = SecondaryOrange
        val Accent = AccentOrangeDark
        
        val Background = Color(0xFF1A1918)         // Warm dark gray with brown undertone
        val Surface = Color(0xFF232120)            // Slightly lighter warm dark
        val SurfaceVariant = Color(0xFF2F2D2C)     // Muted warm dark surface
        
        val TextPrimary = Color(0xFFF7F6F5)        // Warm off-white
        val TextSecondary = Color(0xFFB8B5B1)      // Muted warm gray
        val TextDisabled = Color(0xFF706D6A)       // Darker warm gray
        
        val Divider = Color(0xFF3A3836)           // Subtle warm divider
        val Border = Color(0xFF413E3C)           // Warm border
        val Ripple = Color(0x20E07B56)           // Subtle warm orange ripple
    }
    
    // Semantic Colors (Universal) - Muted and natural
    val Success = Color(0xFF7A9A7F)     // Soft sage green
    val Warning = Color(0xFFE6B85C)     // Muted golden yellow
    val Error = Color(0xFFD47B6A)       // Soft coral red
    val Info = Color(0xFF8FA0B3)        // Muted blue-gray
    
    // Supporting Colors - Natural earth tones
    val SageGreen = Color(0xFFA8B5A6)   // Soft sage
    val WarmBeige = Color(0xFFF6F3EF)   // Creamy beige
    val SoftBlue = Color(0xFFB8C5D1)    // Muted blue-gray
    
    // Convenient access properties
    val primary = PrimaryOrange
    val secondary = SecondaryOrange
}