package dev.orangecast.shared.ui.accessibility

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

object AccessibilityValidator {
    
    const val MIN_CONTRAST_RATIO_NORMAL = 4.5
    const val MIN_CONTRAST_RATIO_LARGE = 3.0
    
    const val MIN_TOUCH_TARGET_DP = 44
    
    fun calculateContrastRatio(color1: Color, color2: Color): Double {
        val luminance1 = color1.luminance()
        val luminance2 = color2.luminance()
        
        val lighter = max(luminance1, luminance2)
        val darker = min(luminance1, luminance2)
        
        return (lighter + 0.05) / (darker + 0.05)
    }
    
    fun validateContrastRatio(
        foreground: Color,
        background: Color,
        isLargeText: Boolean = false
    ): ContrastValidationResult {
        val ratio = calculateContrastRatio(foreground, background)
        val minRatio = if (isLargeText) MIN_CONTRAST_RATIO_LARGE else MIN_CONTRAST_RATIO_NORMAL
        
        return ContrastValidationResult(
            ratio = ratio,
            isValid = ratio >= minRatio,
            requiredRatio = minRatio,
            level = when {
                ratio >= 7.0 -> ComplianceLevel.AAA
                ratio >= minRatio -> ComplianceLevel.AA
                else -> ComplianceLevel.FAIL
            }
        )
    }
    
    fun validateTouchTargetSize(
        widthDp: Int,
        heightDp: Int
    ): TouchTargetValidationResult {
        val isValidWidth = widthDp >= MIN_TOUCH_TARGET_DP
        val isValidHeight = heightDp >= MIN_TOUCH_TARGET_DP
        val isValid = isValidWidth && isValidHeight
        
        return TouchTargetValidationResult(
            isValid = isValid,
            widthDp = widthDp,
            heightDp = heightDp,
            minRequiredDp = MIN_TOUCH_TARGET_DP,
            recommendations = buildList {
                if (!isValidWidth) add("Increase width to at least ${MIN_TOUCH_TARGET_DP}dp")
                if (!isValidHeight) add("Increase height to at least ${MIN_TOUCH_TARGET_DP}dp")
            }
        )
    }
    
    fun validateContentDescription(
        contentDescription: String?,
        isInteractive: Boolean = false
    ): ContentDescriptionValidationResult {
        val isValid = when {
            contentDescription.isNullOrBlank() -> !isInteractive
            contentDescription.length < 3 -> false
            contentDescription.length > 200 -> false
            else -> true
        }
        
        val recommendations = buildList {
            when {
                contentDescription.isNullOrBlank() && isInteractive -> {
                    add("Interactive elements must have meaningful content descriptions")
                }
                contentDescription?.length ?: 0 < 3 -> {
                    add("Content description should be at least 3 characters long")
                }
                contentDescription?.length ?: 0 > 200 -> {
                    add("Content description should be concise (under 200 characters)")
                }
                contentDescription?.contains("button", ignoreCase = true) == true -> {
                    add("Avoid including element type in description (e.g., 'button')")
                }
                contentDescription?.endsWith(".") == false && contentDescription?.length ?: 0 > 10 -> {
                    add("Consider ending longer descriptions with a period")
                }
            }
        }
        
        return ContentDescriptionValidationResult(
            isValid = isValid,
            contentDescription = contentDescription,
            length = contentDescription?.length ?: 0,
            recommendations = recommendations
        )
    }
    
    fun auditComponent(
        componentName: String,
        foregroundColor: Color? = null,
        backgroundColor: Color? = null,
        isLargeText: Boolean = false,
        touchTargetWidthDp: Int? = null,
        touchTargetHeightDp: Int? = null,
        contentDescription: String? = null,
        isInteractive: Boolean = false
    ): ComponentAccessibilityAudit {
        val contrastResult = if (foregroundColor != null && backgroundColor != null) {
            validateContrastRatio(foregroundColor, backgroundColor, isLargeText)
        } else null
        
        val touchTargetResult = if (touchTargetWidthDp != null && touchTargetHeightDp != null) {
            validateTouchTargetSize(touchTargetWidthDp, touchTargetHeightDp)
        } else null
        
        val contentDescriptionResult = validateContentDescription(contentDescription, isInteractive)
        
        val overallValid = listOfNotNull(
            contrastResult?.isValid,
            touchTargetResult?.isValid,
            contentDescriptionResult.isValid
        ).all { it }
        
        return ComponentAccessibilityAudit(
            componentName = componentName,
            isValid = overallValid,
            contrastValidation = contrastResult,
            touchTargetValidation = touchTargetResult,
            contentDescriptionValidation = contentDescriptionResult
        )
    }
}

data class ContrastValidationResult(
    val ratio: Double,
    val isValid: Boolean,
    val requiredRatio: Double,
    val level: ComplianceLevel
)

data class TouchTargetValidationResult(
    val isValid: Boolean,
    val widthDp: Int,
    val heightDp: Int,
    val minRequiredDp: Int,
    val recommendations: List<String>
)

data class ContentDescriptionValidationResult(
    val isValid: Boolean,
    val contentDescription: String?,
    val length: Int,
    val recommendations: List<String>
)

data class ComponentAccessibilityAudit(
    val componentName: String,
    val isValid: Boolean,
    val contrastValidation: ContrastValidationResult?,
    val touchTargetValidation: TouchTargetValidationResult?,
    val contentDescriptionValidation: ContentDescriptionValidationResult
)

enum class ComplianceLevel {
    AAA,
    AA,
    FAIL
}