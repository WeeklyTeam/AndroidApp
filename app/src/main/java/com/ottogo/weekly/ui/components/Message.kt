package com.ottogo.weekly.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.ui.theme.DarkRed
import com.ottogo.weekly.ui.theme.LightRed

enum class MessageTheme{
    ERROR,
}

@Preview
@Composable
fun Message(message: String = "error", messageTheme: MessageTheme = MessageTheme.ERROR) {

    val (foregroundColor, backgroundColor) = when (messageTheme) {
        MessageTheme.ERROR -> (DarkRed to LightRed)
    }

    Card(elevation = 0.dp, backgroundColor = backgroundColor, shape = RoundedCornerShape (12.dp)) {
        Text(message, color = foregroundColor, style = MaterialTheme.typography.h4, modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth())
    }


}