package dev.orangepie.player.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orangecast.player.ui.R
import dev.orangepie.base.ui.theme.Color

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val cardScale = remember { Animatable(1f) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape),
            strokeWidth = 4.dp,
            strokeCap = StrokeCap.Round,
            color = Color.Orange,
        )
        Card(
            modifier = Modifier
                .scale(cardScale.value)
                .clip(CircleShape),
            border = BorderStroke(width = 1.dp, color = Color.Orange.copy(alpha = .2f)),
            onClick = onClick,
            shape = CircleShape,
            elevation = 8.dp,
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Orange,
                                Color.Yellow
                            )
                        )
                    )
            ) {
                Crossfade(
                    modifier = Modifier.padding(16.dp),
                    targetState = isPlaying,
                    animationSpec = tween(300),
                    label = ""
                ) { playing ->
                    if (playing) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pause),
                            contentDescription = null,
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(isLoading) {
        cardScale.animateTo(
            targetValue = if (isLoading) .8f else 1f,
            animationSpec = tween(300)
        )
    }
}

@Preview
@Composable
fun PlayButtonPreview() {
    PlayButton(
        isLoading = true,
        isPlaying = true,
        onClick = {},
    )
}