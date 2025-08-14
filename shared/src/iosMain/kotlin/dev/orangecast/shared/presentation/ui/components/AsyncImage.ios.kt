package dev.orangecast.shared.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import dev.orangecast.shared.presentation.ui.components.ShimmerBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import dev.orangecast.shared.data.cache.ImageCacheManager
import dev.orangecast.shared.data.cache.InMemoryImageCacheManager
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.jetbrains.skia.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

@Composable
actual fun AsyncImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    if (url.isEmpty()) {
        PlaceholderImage(modifier = modifier)
        return
    }

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val imageCache = remember { InMemoryImageCacheManager() }

    LaunchedEffect(url) {
        if (url.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        
        try {
            // Check cache first
            val cachedImage = imageCache.getImage(url)
            if (cachedImage != null) {
                imageBitmap = cachedImage
                isLoading = false
                return@LaunchedEffect
            }
            
            // Load from network if not cached
            val httpClient = HttpClient()
            val imageData = httpClient.get(url).readBytes()
            httpClient.close()
            
            if (imageData.isNotEmpty()) {
                val skiaImage = Image.makeFromEncoded(imageData)
                val bitmap = skiaImage.toComposeImageBitmap()
                
                // Cache the image
                imageCache.putImage(url, bitmap)
                
                imageBitmap = bitmap
            }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    when {
        isLoading -> ShimmerBox(
            modifier = modifier,
            shape = RoundedCornerShape(0.dp)
        )
        imageBitmap != null -> Image(
            bitmap = imageBitmap!!,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
        else -> PlaceholderImage(modifier = modifier)
    }
}