package com.ottogo.weekly.ui.login

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Arrangement.Absolute.aligned
import androidx.compose.material.*
import androidx.compose.material.icons.materialIcon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.login.ui.components.Message
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


// todo : implement Shriya's custom text fields
// question : is alignment and padding correct?
// question : Are we using Scaffold/ floating action button ?
// what am I recieving from birthday?
// getting string value
// todo: add response errors

@Composable
fun SignupPage(navController: NavController, dob: String = "2001-07-10") {
    val phoneNumber = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var error: String? by remember {mutableStateOf(value = null)}

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ){
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            //todo : add navigation back button
            Spacer(modifier = Modifier.height(64.dp))
            displaySignupTitle(title = "Sign Up")
        }

        if (error != null){
            Spacer(modifier = Modifier.height(32.dp))
            Message(error.toString())
        }
        // phone text field
        Spacer(modifier = Modifier.height(32.dp))
        CustomTextField(
            helper = "Phone",
            hint = "9517596842",
            input = phoneNumber.value,
            onChange = { phoneNumber.value = it })

        // username text field
        Spacer(modifier = Modifier.height(24.dp))
        CustomTextField(
            helper = "Username",
            hint = "Username",
            input = username.value,
            onChange = { username.value = it })

        // Password text field
        Spacer(modifier = Modifier.height(24.dp))
        CustomTextField(
            helper = "Password",
            hint = "Password",
            input = password.value,
            onChange = { password.value = it })

        // todo : navigate to verify page and pass token to verify page
        Spacer(modifier = Modifier.height(25.dp))
        CustomButton(buttonText = "Sign Up") {
            runBlocking {
                navController.navigate("signupVerifyPage/{token}")

                try {
                    WeeklyApi.retrofitService.signup(
                        mapOf(
                            "phone" to phoneNumber.toString(),
                            "username" to username.toString(),
                            "password" to password.toString(),
                            "dob" to dob
                        )
                    )
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            val statuscode = e.code()
                            if (statuscode == 400) {
                                error = "400 error"
                            }
                            if (statuscode == 500) {
                                error = "500 error"
                            }
                        }
                        is IOException -> {
                            error = "Check your connection"
                        }
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            //modifier = Modifier.size(250.dp),
            text = "By tapping \"Sign Up\", I agree to",
            fontSize = 14.sp,
            fontFamily = nunitoFamily,
            color = Color.Gray
        )
        // todo : policy and eula in row
        Row() {
            Text(
                text = "Privacy Policy" ,
                fontFamily = nunitoFamily,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "EULA",
                fontFamily = nunitoFamily,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        }
    }

@Composable
fun displaySignupTitle(title: String) {
    Spacer(modifier = Modifier.height(44.dp))
    Text(text = title,
        fontSize = 42.sp,
        fontWeight = FontWeight(700),
        fontFamily = nunitoFamily
    )
}

@Composable
fun displayTextFieldTitle(title: String){
    Text(text = title,
        textAlign = TextAlign.Left,
        fontSize = 16.sp,
        fontWeight = FontWeight(700),
        fontFamily = nunitoFamily
    )
}

// todo: make red box
@Composable
fun ErrorMessage500(){
    Text(text = "We are experiencing issues please try again later",
    fontFamily = nunitoFamily,
    color = Color.Red)
}
@Composable
fun ErrorMessage400(){
    Text(text = "an unexpected error occurred",
        fontFamily = nunitoFamily,
        color = Color.Red)
}

