package com.ottogo.weekly.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.Message
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException

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
fun SignupVerifyPage(navController: NavController, token: String) {
    var code by remember { mutableStateOf("") }
    var error: String? by remember {mutableStateOf(value = null)}

    Column(modifier = Modifier.systemBarsPadding()) {
        LoginTitle(navController = navController, title = "Verify")

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            if (error != null){
                Spacer(modifier = Modifier.height(32.dp))
                Message(error.toString())
            }

            Spacer(modifier = Modifier.height(32.dp))
            CustomTextField(helper = "Code", hint = "", input = code, onChange = { code = it }, isNumberInput = true)
            Spacer(modifier = Modifier.height(32.dp))
            CustomButton(buttonText = "Next") {
                runBlocking {
                    try {
                        WeeklyApi.retrofitService.verify(
                            mapOf("Authorization" to "token $token"),
                            mapOf("code" to code.toInt())
                        )
                        navController.navigate("SignupProfilePage/$token")
                    } catch (e: Exception) {
                        when (e) {
                            is HttpException -> {
                                val statuscode = e.code()
                                if (statuscode >= 400) {
                                    error = "Incorrect code"
                                }
                                if (statuscode >= 500) {
                                    error = "We are experiencing issues please try again later"
                                }
                            }
                            is IOException -> {
                                error = "Check your connection"
                            }
                        }
                    }

                }
            }

        }
    }
}