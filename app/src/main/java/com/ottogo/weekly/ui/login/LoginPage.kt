package com.ottogo.weekly.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.onesignal.OneSignal
import com.ottogo.weekly.api.Activity
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.Message
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    val context = LocalContext.current


    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(color = Color.White)

    Column(modifier = Modifier.systemBarsPadding()) {

        LoginTitle(navController = navController, title = "Login")

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (error != null){
                Spacer(modifier = Modifier.height(32.dp))
                Message(error.toString())
            }

            Spacer(modifier = Modifier.height(32.dp))

            CustomTextField(
                helper = "Login",
                hint = "Username, phone, or email",
                input = usernameValue,
                onChange = { usernameValue = it },
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
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
                    try {
                        Log.d("tokenSet", "token")

                        val responseMap = WeeklyApi.retrofitService.login(
                                mapOf(
                                    "username" to usernameValue.lowercase(),
                                    "password" to passwordValue
                                )
                            )

                        val pushTokenId = OneSignal.getDeviceState()?.userId ?: ""

                        val pushToken = pushTokenId.toRequestBody("text/plain".toMediaTypeOrNull())

                        val token = responseMap["token"]


                        WeeklyApi.retrofitService.patchProfile(
                            mapOf("Authorization" to "token $token"),
                            mapOf("token" to pushToken),
                        )

                        if (token != null) {
                            userViewModel.setToken(usernameValue, token, context)
                            Log.d("token", token)

                        }
                        else {
                            throw java.lang.Exception("token is null")
                        }


                    } catch (e: Exception) {
                        Log.d("AM", e.toString())

                        when (e) {
                            is HttpException -> {
                                val statuscode = e.code()
                                if (statuscode >= 400) {
                                    error = "Incorrect login or password"
                                }
                                if (statuscode >= 500) {
                                    error = "We are experiencing issues please try again later"
                                }
                            }
                            is IOException -> {
                                error = "Check your connection"
                            }
                            else -> {
                                "An unexpected error occurred"
                            }
                        }

                }

            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Forgot your password?" ,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.clickable {
                    navController.navigate("webviewPage/Weekly?url=https://www.theweeklyapp.com/account/password_reset/")
                }

            )
        }
    }
}
