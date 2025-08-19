package dev.orangecast.shared.presentation.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Memory-efficient shared shimmer brush using CompositionLocal
val LocalShimmerBrush = compositionLocalOf<Brush> {
    error("ShimmerBrush not provided")
}

@Composable
fun ProvideShimmerBrush(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.0f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.0f)
    )

    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation - 400f, 0f),
        end = Offset(translateAnimation, 400f)
    )

    CompositionLocalProvider(LocalShimmerBrush provides shimmerBrush) {
        content()
    }
}

fun Modifier.shimmer(): Modifier = composed {
    val brush = LocalShimmerBrush.current
    
    drawWithContent {
        drawContent()
        drawRect(brush = brush)
    }
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0xFFE0E0E0))
            .shimmer()
    )
}

@Composable
fun ShimmerPodcastList(
    itemCount: Int = 3,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(itemCount) {
            PodcastCardShimmer(
                layoutStyle = PodcastCardLayoutStyle.Grid,
                modifier = Modifier.width(160.dp)
            )
        }
    }
}

@Composable
fun ShimmerEpisodeCard(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        ShimmerBox(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp)
                )
                
                ShimmerBox(
                    modifier = Modifier
                        .width(40.dp)
                        .height(12.dp)
                )
            }
            
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(12.dp)
            )
        }
    }
}

@Composable
fun ShimmerDiscoverScreen() {
    ProvideShimmerBrush {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            
            repeat(3) { _ ->
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ShimmerBox(
                            modifier = Modifier
                                .width(150.dp)
                                .height(24.dp)
                        )
                        
                        ShimmerPodcastList()
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerPodcastDetailScreen() {
    ProvideShimmerBrush {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )
                    
                    ShimmerBox(
                        modifier = Modifier
                            .width(120.dp)
                            .height(16.dp)
                    )
                }
            }
            
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
            
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            repeat(5) {
                ShimmerEpisodeCard(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ShimmerSearchResult(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
            )
            
            ShimmerBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(14.dp)
            )
        }
    }
}

@Composable
fun GenreItemShimmer() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            ShimmerBox(
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}