package com.ottogo.weekly.ui.login

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import kotlinx.coroutines.runBlocking


/*
*
* Built by: Sriya
*
* This is a 4 digit code input
* Do a post request to verify using the token in the header map as authorization and token in the body
* if the response is 500 show an error message
* "We are experiencing issues please try again later"
* if the response is 400 show the error message
* "an unexpected error occurred"
* else afterwards navigate and pass the token to the signup profile page
* Try your best! Please don't hesitate to ask any questions
*
* */

@Composable
fun SignupVerifyPage(navController: NavController) {
    var code by remember { mutableStateOf("") }
    LoginTitle(navController = navController, title = "verify")
            Column (
            horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding (horizontal = 24.dp)

            ){CustomTextField(helper = "code", hint = "code" ,input = code, onChange = {code = it})
                    Spacer (modifier = Modifier.padding (bottom = 32.dp))
                    CustomButton (buttonText = "Next", onClick =
                    {
                        runBlocking {
                            navController.navigate("SignupProfilePage/sdfsghjk")
                            WeeklyApi.retrofitService.verify(mapOf("Authorization" to "token b370bf6411b5089dc3d88b421537d755271067b7"),
                                mapOf("code" to "6052".toInt()))
                        }
                    })

}
}