package com.ottogo.weekly.ui.login

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.ui.components.CustomButton
import kotlinx.coroutines.runBlocking

/*
*
* Built by: Linda
*
* Follow the guides of the figma file
* use a date picker dialog to pick the date, verify the age is over 13
* otherwise show a message that the user is too young
* pass the date to the signup view as a string that follows the format YYYY-MM-DD
*
* */
@Composable
fun SignupBirthdayPage(navController: NavController) {



    Column(){
    Text(text ="birthday page")
        CustomButton(buttonText = "Sign up") {
            runBlocking {
                navController.navigate("signupPage/{dob}")
            }
            Modifier.width(27.dp)
        }

    }
}