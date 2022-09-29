package com.ottogo.weekly.ui.login

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel


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
fun SignupNotificationPage(navController: NavController, token: String, username: String, userViewModel: UserViewModel) {
    var notificationComplete by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(modifier = Modifier
        .systemBarsPadding()
        .verticalScroll(rememberScrollState())) {
        LoginTitle(navController = navController, title = "Notifications")

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Loader(Modifier.height(screenHeight*1/3))
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Do you want the utmost best friend experience? \uD83E\uDDD1\u200D\uD83E\uDD1D\u200D\uD83E\uDDD1")
            Spacer(modifier = Modifier.height(32.dp))

            Row(Modifier.clickable {
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, "com.ottogo.weekly")
                notificationComplete = true
                startActivity(context, settingsIntent, null)
            }, verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    painter = painterResource(id = if (notificationComplete){ R.drawable.ic_checkbox_circle_fill } else { R.drawable.ic_checkbox_blank_circle_line }),
                    tint = if (notificationComplete){ MaterialTheme.colors.primary } else { ExtendedTheme.colors.Black60 },
                    contentDescription = "checkbox",
                    modifier = Modifier.padding(16.dp)
                )

                Column() {
                    Text(text = "Yes")

//                    Text(text = "Never miss an invite, message, or more.", style = MaterialTheme.typography.body2)


                }

            }

            Spacer(modifier = Modifier.height(32.dp))


            AnimatedVisibility(
                visible = notificationComplete,
                enter = fadeIn(
                    // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                    initialAlpha = 0.0f
                ),
            ) {
                CustomButton(buttonText = "Finish!") {
                    userViewModel.setToken(username, token, context)
                }
            }

        }
    }
}

@Composable
fun Loader(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.notificationpermission))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier

        )
}