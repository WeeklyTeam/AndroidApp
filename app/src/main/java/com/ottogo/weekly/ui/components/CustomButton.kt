package com.ottogo.weekly.ui.components

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.Purple
import kotlinx.coroutines.launch

@Composable
fun CustomButton(
    buttonText: String,
    backgroundColor: Color = Purple,
    outlineColor: Color = Color.Transparent,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: suspend () -> Unit,
    ) {
    var buttonloading: String by remember { mutableStateOf(buttonText) }
    val scope = rememberCoroutineScope()

    Button(onClick = {

        scope.launch {
            onClick()
            buttonloading = buttonText
        }

        buttonloading = "loading"

    }, elevation = null, shape = RoundedCornerShape(24.dp),
        border = BorderStroke(3.dp, outlineColor), colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        modifier = modifier.fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = buttonloading, style = MaterialTheme.typography.h4, color = textColor)

    }
}