package dev.orangepie.base.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orangecast.base.ui.R
import dev.orangepie.base.ui.animation.clickAnimatedScale
import dev.orangepie.base.ui.theme.Color

@Composable
fun ButtonCircleWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit = {},
) {
    Card(
        modifier = modifier
            .size(40.dp)
            .clickAnimatedScale(onClick = onClick),
        backgroundColor = Color.BackgroundDark,
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

@Preview
@Composable
fun ButtonCircleWithIconPreview() {
    ButtonCircleWithIcon(
        onClick = {},
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = null
            )
        }
    )
}