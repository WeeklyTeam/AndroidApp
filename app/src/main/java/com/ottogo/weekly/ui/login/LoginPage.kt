package com.ottogo.weekly.ui.login

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.ui.theme.whiteBackground
import kotlinx.coroutines.runBlocking


/**
 * Things I need help with
 * 1. Shriya's code for custom input fieldx`x
 * 2. using the correct font from res file
 * 3. back button
 * 4. Whats proper sizing
 * 5. connect to API
 */


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
fun LoginPage(navController: NavController) {

    val usernameValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(24.dp)
    ) {

        Text(
            text = "Login",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = nunitoFamily,
                fontSize = 49.sp
            ),
        )
        Spacer(modifier = Modifier.padding(24.dp))
            Text(
                text = "Username",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = nunitoFamily,
                    fontSize = 22.sp
                )
            )
            CustomTextField(helper = "Username",
                            hint = "Username",
                            input = usernameValue.value,
                            onChange = { usernameValue.value = it })

            Spacer(modifier = Modifier.padding(24.dp))
                Text(
                    text = "Password",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontFamily = nunitoFamily,
                        fontSize = 22.sp
                    )
                )
                CustomTextField(helper = "Password",
                                hint = "Password",
                                input = passwordValue.value,
                                onChange = { passwordValue.value = it })

                Spacer(modifier = Modifier.padding(24.dp))

                    CustomButton(buttonText = "Login"){
                        runBlocking {
                            var responseMap =
                            WeeklyApi.retrofitService.login(mapOf("username" to usernameValue.value, "password" to passwordValue.value))
                        }
                    }
    }
}





































//@Composable
//fun LoginPage(navController: NavController) {
//
//    val username = remember{ mutableStateOf("")}
//    val passwordValue = remember{ mutableStateOf("")}
//
//    // THIS CODE TO ADD AN IMAGE TO THE TOP
//        //Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter){
//          //  Box(modifier = Modifier.fillMaxSize().background(Color.WHITE), contentAlignment = Alignment.TopCenter){
//
//           // }
//        //}
//
//
////    Column(horizontalAlignment = Alignment.CenterHorizontally,
////           verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.60f).clip(
////            RoundedCornerShape(topLeft = 30.dp, topRight = 30.dp))){
////    }
//
//
//    Text(text = "Login", style = TextStyle(fontWeight = FontWeight.Bold, letterSpacing = TextUnit.Companion.Sp(2)),
//
//        fontSize = TextUnit.Companion.Sp(30)
//
//    )
//
//    Spacer(modifier = Modifier.padding(20.dp))
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        OutlinedTextField(value = username.value , onValueChange = {username.value = it},
//        label = {Text(text = "Username")},
//        placeholder = {Text(text = "Username")},
//        singleLine = true,
//        modifier = Modifier.fillMaxWidth(8.8f)
//        )
//
//        OutlinedTextField(
//            value = passwordValue,
//            onValueChange = { passwordValue.value = it},
//            label = {Text("Password")},
//            placeholder = {Text(text = "Password")}
//
//        )
//    }
//}