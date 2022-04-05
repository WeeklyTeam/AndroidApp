package com.ottogo.weekly.ui.login

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.nunitoFamily
import kotlinx.coroutines.runBlocking
import java.time.format.TextStyle

@Composable
fun LandingPage(navController: NavController) {


    Column(modifier = Modifier.padding(24.dp)){

        displayTitle("Weekly")
        displayGreeting("Make your best memories")



            Spacer(modifier = Modifier.padding(187.dp))
            CustomButton(buttonText = "Sign up") {
                runBlocking {
                    navController.navigate("signupBirthdayPage")
                }
                Modifier.height(48.dp)
            }

            Spacer(modifier = Modifier.height(15.dp))
            CustomButton(buttonText = "Login") {
                runBlocking {
                    navController.navigate("loginPage")
                }
            }
        }
}

@Composable
fun displayTitle(appName: String) {
    Spacer(modifier = Modifier.height(65.dp))
    Text(text = appName,
        fontSize = 48.sp,
        fontWeight = FontWeight(700),
        fontFamily = nunitoFamily
    )
}
@Composable
fun displayGreeting(greeting: String) {
    Spacer(modifier = Modifier.height(17.dp))
    Text(text = greeting,
        fontSize = 24.sp,
        fontWeight = FontWeight(700),
        fontFamily = nunitoFamily)
}
