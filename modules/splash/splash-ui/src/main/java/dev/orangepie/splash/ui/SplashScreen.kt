package dev.orangepie.splash.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orangecast.splash.ui.R
import dev.orangepie.base.ui.theme.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {

    val visibile = remember { mutableStateOf(true) }
    val animatableAngle = remember {
        Animatable(0f)
    }

    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize(),
        visible = visibile.value,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Background,
                            Color.BackgroundBlack,
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 145.dp, height = 176.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.BottomCenter),
                        elevation = 8.dp,
                    ) {
                        Image(
                            modifier = Modifier
                                .size(145.dp),
                            painter = painterResource(id = R.drawable.img_splash_background),
                            contentDescription = null
                        )
                    }
                    Image(
                        modifier = Modifier
                            .size(width = 103.dp, height = 121.dp)
                            .rotate(animatableAngle.value),
                        painter = painterResource(id = R.drawable.img_splash_leaf),
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(42.dp),
                        painter = painterResource(id = R.drawable.img_splash_play),
                        contentDescription = null
                    )
                }
            }
        }
    }

    LaunchedEffect(true) {
        launch {
            animatableAngle.animateTo(
                targetValue = 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 500,
                        easing = LinearOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
        delay(1000)
        visibile.value = false
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}