package dev.orangecast.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.orangecast.shared.domain.model.ThemeMode
import dev.orangecast.shared.presentation.viewmodel.ThemeViewModel
import dev.orangecast.shared.ui.accessibility.AccessibilityStrings
import dev.orangecast.shared.ui.accessibility.accessibleButton
import dev.orangecast.shared.ui.accessibility.accessibleHeading
import dev.orangecast.shared.ui.accessibility.decorativeElement

@Composable
fun ThemeSelector(
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by themeViewModel.themeMode.collectAsState()
    val isLoading by themeViewModel.isLoading.collectAsState()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.accessibleHeading(level = 2)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ThemeOption(
                title = "System",
                description = "Follow system setting",
                icon = Icons.Default.Smartphone,
                selected = currentTheme == ThemeMode.SYSTEM,
                enabled = !isLoading,
                onClick = { themeViewModel.setThemeMode(ThemeMode.SYSTEM) },
                accessibilityLabel = AccessibilityStrings.SYSTEM_THEME_OPTION
            )
            
            ThemeOption(
                title = "Light",
                description = "Light theme",
                icon = Icons.Default.LightMode,
                selected = currentTheme == ThemeMode.LIGHT,
                enabled = !isLoading,
                onClick = { themeViewModel.setThemeMode(ThemeMode.LIGHT) },
                accessibilityLabel = AccessibilityStrings.LIGHT_THEME_OPTION
            )
            
            ThemeOption(
                title = "Dark",
                description = "Dark theme",
                icon = Icons.Default.DarkMode,
                selected = currentTheme == ThemeMode.DARK,
                enabled = !isLoading,
                onClick = { themeViewModel.setThemeMode(ThemeMode.DARK) },
                accessibilityLabel = AccessibilityStrings.DARK_THEME_OPTION
            )
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    accessibilityLabel: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .accessibleButton(
                description = "$accessibilityLabel. $description",
                enabled = enabled,
                stateDescription = if (selected) "Selected" else "Not selected"
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary 
                  else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.decorativeElement()
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.onSurface 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        RadioButton(
            selected = selected,
            enabled = enabled,
            onClick = null,
            modifier = Modifier.decorativeElement()
        )
    }
}