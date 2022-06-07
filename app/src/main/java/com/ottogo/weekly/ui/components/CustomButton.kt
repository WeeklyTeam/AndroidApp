package com.ottogo.weekly.ui.components

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onClick: suspend () -> Unit
    ) {
    var buttonloading: String by remember { mutableStateOf(buttonText) }
    val scope = rememberCoroutineScope()

    Button(onClick = {


        scope.launch {
            runBlocking {
                onClick()
                buttonloading = buttonText
            }

        }

        buttonloading = "loading"

    }, shape = CircleShape, modifier = modifier.fillMaxWidth().height(48.dp).clip(CircleShape)) {
        Text(text = buttonloading, style = MaterialTheme.typography.h4)

    }
}