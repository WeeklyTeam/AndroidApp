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
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import kotlinx.coroutines.runBlocking

@Composable
fun LandingPage(navController: NavController) {



    Column(){

        CustomButton(buttonText = "Sign up"){
            runBlocking {
                navController.navigate("signupPage")

            }
        }
        CustomButton(buttonText = "Login"){
            runBlocking {
                navController.navigate("loginPage")

            }
        }
    }
}