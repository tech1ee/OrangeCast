package dev.orangepie.library.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.orangecast.library.ui.R
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle

@Composable
fun LibraryEmptyScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_passion),
                contentDescription = null
            )
            Text(
                text = stringResource(id = R.string.library_empty),
                style = TextStyle.B1,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun LibraryEmptyScreenPreview() {
    LibraryEmptyScreen()
}