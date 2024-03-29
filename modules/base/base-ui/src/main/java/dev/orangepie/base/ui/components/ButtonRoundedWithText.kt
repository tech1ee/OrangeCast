package dev.orangepie.base.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle

@Composable
fun ButtonRoundedWithText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false,
) {
    Button(
        modifier = modifier
            .height(44.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (selected) Color.BackgroundBlack else Color.Orange,
            contentColor = Color.White,
        ),
        shape = RoundedCornerShape(50),
        onClick = onClick,
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp),
            text = text,
            style = TextStyle.Button,
            color = Color.White
        )
    }
}

@Preview
@Composable
fun ButtonRoundedWithTextPreview() {
    ButtonRoundedWithText(
        text = "Unsubscribe",
        onClick = {},
    )
}