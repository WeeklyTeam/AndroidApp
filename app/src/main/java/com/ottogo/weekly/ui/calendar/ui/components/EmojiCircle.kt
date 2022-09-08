package com.ottogo.weekly.ui.calendar.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily


@Composable
fun EmojiCircle(emoji: String, modifier: Modifier = Modifier){
    Box(modifier = modifier
        .clip(
            CircleShape
        )
        .size(72.dp)
        .background(ExtendedTheme.colors.LightGray)
        , contentAlignment = Alignment.Center){
        Text(emoji, style = TextStyle(
            fontFamily = nunitoFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp
        ),
            color = Color(red = 0, blue = 0, green = 0)
        )
    }

    Log.d("status", emoji)
}