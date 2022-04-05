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
* Built by: Alondra
*
* Use the date passed from the birthday page and the TextField inputs to do a signup request
* if the response is 500 show an error message
* "We are experiencing issues please try again later"
* if the response is 400 show the error message
* "an unexpected error occurred"
* else parse the token from the response, send a new get request to verify, afterwards navigate and pass the token to the verify page
* Try your best! Please don't hesitate to ask any questions
*
* */
@Composable
fun SignupPage(navController: NavController, dob: String) {



    Column(){


    }
}