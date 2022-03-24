package com.ottogo.weekly.ui.login.ui.components

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/*
*
* Built by: Alondra
*
* Follow the figma file, note the spacing but keep in mind that the button will have a large tappable area
* there should be a clickable back button with 24dp padding all around
* you can find the icon on remixicons.com
* if you have any questions please feel free to reach out, try your best!
*
* */
@Composable
fun LoginTitle(navController: NavController, title: String) {



    Column(){
        Text(text = title)

    }
}