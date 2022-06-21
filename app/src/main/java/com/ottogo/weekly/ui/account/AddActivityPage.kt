package com.ottogo.weekly.ui.account

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.Message
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import retrofit2.HttpException
import java.io.IOException

@Composable
fun AddActivityPage(navController: NavController, userViewModel: UserViewModel) {

    var activity by remember {
        mutableStateOf("")
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    var error: String? by remember {mutableStateOf(value = null)}

    Column(horizontalAlignment = Alignment.CenterHorizontally,) {
        TitleBar(navController = navController, title = "Add activity")

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        if (error != null){
            Spacer(modifier = Modifier.height(24.dp))
            Message(error.toString())
        }

        if (submitted){
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Submitted! Feel free to add another", color = ExtendedTheme.colors.Green, style = MaterialTheme.typography.h5)
        }

        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(helper = "Activity", hint = "", input = activity, onChange = { activity = it }, modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(buttonText = "Add", onClick = {

            try {
                val response = WeeklyApi.retrofitService.createActivity(
                    mapOf(
                        "Authorization" to "token ${userViewModel.token}"
                    ),
                    mapOf(
                        "activity" to activity
                    )
                )
                error = null
                submitted = true

            } catch (e: Exception) {
                Log.d("dbg", e.toString())

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


        }, modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "All activities must be verified", color = ExtendedTheme.colors.Black60, style = MaterialTheme.typography.body2)
    }
}