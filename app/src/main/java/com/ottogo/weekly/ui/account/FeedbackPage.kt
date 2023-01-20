package com.ottogo.weekly.ui.account

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.SelectableOption
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun FeedbackPage(navController: NavController, userViewModel: UserViewModel) {
    var details by remember{ mutableStateOf("") }
    var response by remember{ mutableStateOf(true) }
    var submitted by remember{ mutableStateOf(false) }



    Column {
        TitleBar(navController = navController, title = "Feedback")

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
            .weight(1F)) {


            Text("Feedback", style = MaterialTheme.typography.h4, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.height(16.dp))

            CustomBigTextField(hint = "", input = details, onChange = { details = it }, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    response = !response
                }) {
                Icon(painter = painterResource(id =
                if(response){
                    R.drawable.ic_checkbox_circle_fill
                } else {
                    R.drawable.ic_checkbox_blank_circle_line
                }), contentDescription = "Checkbox",
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .height(24.dp)
                        .width(24.dp),
                    tint =
                    if(response){
                        MaterialTheme.colors.primary
                    } else {
                        ExtendedTheme.colors.Black60
                    }
                )

                Text(text = "I would like to hear back", style = MaterialTheme.typography.body1)


            }

            Spacer(Modifier.height(24.dp))


            if (submitted) {
                CustomButton(
                    buttonText = "Sent!",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp, bottom = 16.dp),
                    textColor = ExtendedTheme.colors.Green,
                    backgroundColor = MaterialTheme.colors.onPrimary
                ) {}
            } else {
                CustomButton(
                    buttonText = "Send",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp, bottom = 16.dp)
                ) {
                    WeeklyApi.retrofitService.report(
                        mapOf(
                            "Authorization" to "token ${userViewModel.token}"
                        ),
                        mapOf(
                            "details" to details,
                            "response" to response,
                            "category" to "F"
                        )
                    )
                    submitted = true


                }
            }

        }


    }
}

