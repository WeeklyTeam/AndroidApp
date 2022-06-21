package com.ottogo.weekly.ui.components

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.ui.theme.Typography
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun CustomButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    foregroundColor: Color = MaterialTheme.colors.onPrimary,
    backgroundColor: Color = MaterialTheme.colors.primary,
    onClick: suspend () -> Unit
    ) {
    var buttonloading: String by remember { mutableStateOf(buttonText) }
    val scope = rememberCoroutineScope()

    Button(onClick = {

        scope.launch {
            onClick()
            buttonloading = buttonText

        }

        buttonloading = "loading"

    }, shape = CircleShape, modifier = modifier.fillMaxWidth().height(48.dp).clip(CircleShape), colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)) {
        Text(text = buttonloading, style = MaterialTheme.typography.h4, color = foregroundColor)

    }
}