package com.ottogo.weekly.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.Message
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException

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
fun LoginPage(navController: NavController, userViewModel: UserViewModel) {


    var usernameValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var error: String? by remember {mutableStateOf(value = null)}
    val focusManager = LocalFocusManager.current



    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(color = Color.White)

    Column(modifier = Modifier.systemBarsPadding()) {

        LoginTitle(navController = navController, title = "Login")

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {

            if (error != null){
                Spacer(modifier = Modifier.height(32.dp))
                Message(error.toString())
            }

            Spacer(modifier = Modifier.height(32.dp))

            CustomTextField(helper = "Username",
                hint = "Username",
                input = usernameValue,
                onChange = { usernameValue = it },
                keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Next)}),
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextField(
                helper = "Password",
                hint = "Password",
                input = passwordValue,
                onChange = { passwordValue = it },
                isPasswordInput = true,
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
                done = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomButton(buttonText = "Login") {
                runBlocking {
                    try {
                        val responseMap = WeeklyApi.retrofitService.login(
                                mapOf(
                                    "username" to usernameValue,
                                    "password" to passwordValue
                                )
                            )
                        userViewModel.token = responseMap["token"]

                    } catch (e: Exception) {

                        when (e) {
                            is HttpException -> {
                                val statuscode = e.code()
                                if (statuscode >= 400) {
                                    error = "Incorrect username or password"
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
