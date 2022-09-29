package com.ottogo.weekly.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.onesignal.OneSignal
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.Message
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.ui.theme.nunitoFamily
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException

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

//username=[Ensure this field has no more than 16 characters.], phone=[Ensure this field has no more than 10 characters.]


@Composable
fun SignupPage(navController: NavController, dob: String) {
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error: String? by remember {mutableStateOf(value = null)}

    val focusManager = LocalFocusManager.current


    Column(modifier = Modifier.systemBarsPadding().verticalScroll(rememberScrollState())){
        LoginTitle(navController = navController, title = "Sign Up")

        Column(modifier = Modifier
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            if (error != null){
                Spacer(modifier = Modifier.height(32.dp))
                Message(error.toString())
            }
            // phone text field
            Spacer(modifier = Modifier.height(32.dp))
            CustomTextField(
                helper = "Phone",
                hint = "9517596842",
                input = phoneNumber,
                onChange = { phoneNumber = it },
                isNumberInput = true,
                keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Down)})
            )

            // username text field
            Spacer(modifier = Modifier.height(24.dp))
            CustomTextField(
                helper = "Username",
                hint = "Username",
                input = username,
                onChange = { username = it },
                keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Down)})
            )

            // Password text field
            Spacer(modifier = Modifier.height(24.dp))
            CustomTextField(
                helper = "Password",
                hint = "Password",
                input = password,
                onChange = { password = it },
                isPasswordInput = true,
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
                done = true
            )

            // todo : navigate to verify page and pass token to verify page
            Spacer(modifier = Modifier.height(32.dp))
            CustomButton(buttonText = "Sign Up") {
                runBlocking {

                    try {
                        val response = WeeklyApi.retrofitService.signup(
                            mapOf(
                                "phone" to phoneNumber,
                                "username" to username.trim().lowercase(),
                                "password" to password,
                                "dob" to dob,

                            )
                        )
//                        WeeklyApi.retrofitService.createCode(
//                            mapOf(
//                                "Authorization" to "token ${response["token"]}"
//                            )
//                        )
                        navController.navigate("signupVerifyPage/${response["token"]}/${response["username"]}")
                    } catch (e: Exception) {

                        when (e) {
                            is HttpException -> {
                                val statuscode = e.code()
                                if (statuscode >= 400) {
                                    error = "an unexpected error occurred"
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

            //test
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                //modifier = Modifier.size(250.dp),
                text = "By tapping \"Sign Up\", I agree to",
                fontSize = 14.sp,
                fontFamily = nunitoFamily,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row() {
                Text(
                    text = "Privacy Policy" ,
                    fontFamily = nunitoFamily,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        navController.navigate("webviewPage/Privacy Policy?url=https://www.privacypolicies.com/live/879e93a8-0691-459f-85fa-1d1a4c56bf12")
                    }

                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "EULA",
                    fontFamily = nunitoFamily,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        navController.navigate("webviewPage/EULA?url=https://www.privacypolicies.com/live/eed0418f-1191-4c07-8354-ffd904340564")
                    }
                )
            }

        }
    }
}


