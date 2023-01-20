package com.ottogo.weekly.ui.calendar.weekly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.giphy.sdk.core.models.User
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.login.Loader
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun WeeklyCompletePage(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    if (userViewModel.friends.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.height(48.dp))
            LottieLoader(modifier = Modifier.height(screenHeight * 1 / 3), res = R.raw.travelcar)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "All set, we'll keep you posted.\nUse your time wisely and have fun!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(24.dp))
            CustomButton(buttonText = "Yay!", onClick = {
                userViewModel.updateWeeklyCompletePref(context)
                navController.popBackStack("homePage", inclusive = false)
            }
            )


        }
    } else {
        Column(modifier = Modifier.padding(24.dp)
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {


            Spacer(modifier = Modifier.height(48.dp))
            LottieLoader(modifier = Modifier.height(screenHeight*1/3), res = R.raw.friendsfun)
            Spacer(modifier = Modifier.height(24.dp))
            Text("All done! Let's find some friends\nto get together with.", textAlign = TextAlign.Center, style = MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.height(24.dp))
            CustomButton(buttonText = "Yes!", onClick = {
                userViewModel.updateWeeklyCompletePref(context)
                navController.navigate("contactsPage")}
            )


        }
    }
}

@Composable
fun LottieLoader(modifier: Modifier = Modifier, res: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(res))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier

    )
}