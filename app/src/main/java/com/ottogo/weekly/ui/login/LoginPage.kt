package com.ottogo.weekly.ui.login

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
* Built by: Linda
*
* create a login page following the design from the figma file
* note the spacing of 24dp between inputs and 32dp between buttons
* when a user presses login launch a web request and parse the response
* if the response is 500 show an error message
* "We are experiencing issues please try again later"
* if the response is 400 show the error message
* "Incorrect username or password"
* else set the users view model token to the one parsed from the response
* Try your best! Please don't hesitate to ask any questions
*
* to test this view you can use the username: testuser and password: TestUser
*
* */
@Composable
fun LoginPage(navController: NavController) {


    Column(){


    }
}