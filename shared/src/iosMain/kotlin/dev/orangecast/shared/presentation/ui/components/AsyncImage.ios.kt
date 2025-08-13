package dev.orangecast.shared.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.jetbrains.skia.Image

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

    LaunchedEffect(url) {
        try {
            val httpClient = HttpClient()
            val imageData = httpClient.get(url).readBytes()
            httpClient.close()
            
            val skiaImage = Image.makeFromEncoded(imageData)
            imageBitmap = skiaImage.toComposeImageBitmap()
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    when {
        isLoading -> PlaceholderImage(modifier = modifier)
        imageBitmap != null -> Image(
            bitmap = imageBitmap!!,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
        else -> PlaceholderImage(modifier = modifier)
    }
}