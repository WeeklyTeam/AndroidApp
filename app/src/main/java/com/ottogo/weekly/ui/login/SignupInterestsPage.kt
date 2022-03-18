package com.ottogo.weekly.ui.login

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.nunitoFamily
import kotlinx.coroutines.runBlocking

/*
*
* Built by: Napoleon
*
* Send a get request to the interests url
* when the user taps on an interest send a request to like the interest
* when the response is successful add a red heart to the item
* keep track of how many interests the user likes
* set the user view model token to the token recieved
* Try your best! Please don't hesitate to ask any questions
*
* */

@Composable
fun SignupInterestsPage(navController: NavController, token: String) {

    Column(modifier = Modifier
        .background(color = Color.White)
        .padding(24.dp)) {
        Text(
            text = "Interests",
            fontFamily = nunitoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 42.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(54.dp)
                .width(328.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color(0xFFF0EAFF))
        ) {
            Text(
                text = "What do you like to do? (Minimum 3)",
                color = Color(0xFF4C21C2),
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Activity Title",
            fontFamily = nunitoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        runBlocking {
            WeeklyApi.retrofitService.activity(mapOf("Authorization" to "token 265245769906872d88b40205147f5cbf63538b83"))
            Log.d("status", "Retrieved Activity List")
        }

        Spacer(modifier = Modifier.height(32.dp))
        CustomButton(buttonText = "Finish", onClick = {})
    }
}