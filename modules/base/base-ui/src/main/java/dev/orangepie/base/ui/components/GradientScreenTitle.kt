package dev.orangepie.base.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.orangecast.common.R
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle

@Composable
fun GradientScreenTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Background,
                        Color.BackgroundBlack,
                    )
                )
            )
    ) {
        GradientText(
            modifier = Modifier
                .padding(16.dp),
            text = title,
            gradient = listOf(
                Color.Yellow,
                Color.Orange,
            ),
            textSize = TextStyle.H2.fontSize,
        )
    }
    GradientDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    )
}