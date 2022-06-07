package com.ottogo.weekly.ui.login

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.format.TextStyle

@Composable
fun LandingPage(navController: NavController) {

    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(color = Color.Transparent)


    Box() {
        
        Image(painter = painterResource(id = R.mipmap.landing_page_background),
            contentDescription = "background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight
        )

        Column(modifier = Modifier.fillMaxHeight().systemBarsPadding().padding(24.dp), verticalArrangement = Arrangement.SpaceBetween) {

            Column() {
                Text(text = "Weekly",
                    fontSize = 48.sp,
                    fontWeight = FontWeight(700),
                    fontFamily = nunitoFamily,
                    color = MaterialTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Make your best memories",
                    fontSize = 24.sp,
                    fontWeight = FontWeight(700),
                    fontFamily = nunitoFamily,
                    color = MaterialTheme.colors.onPrimary)
            }



            Column() {
                CustomButton(buttonText = "Sign up") {
                    runBlocking {
                        navController.navigate("signupBirthdayPage")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = {

                    navController.navigate("loginPage")

                }, shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent),
                    border = BorderStroke(3.dp, MaterialTheme.colors.onPrimary),
                    modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text(text = "Login", style = MaterialTheme.typography.h4, color = MaterialTheme.colors.onPrimary)

                }
            }

        }
    }
}


