package com.ottogo.weekly.ui.calendar.weekly

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.login.Loader
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun WeeklyFailurePage(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(modifier = Modifier.padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {


        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(56.dp)) {
            Icon(painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                contentDescription = "back",
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        LottieLoader(modifier = Modifier.height(screenHeight*1/3), res = R.raw.busyperson)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Good luck, in all that you're up to.\nHope you have fun, come back soon!", textAlign = TextAlign.Center, style = MaterialTheme.typography.h3)
        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(buttonText = "Yes!", onClick = {
            userViewModel.updateWeeklyCompletePref(context)
            navController.popBackStack("homePage", inclusive = false)}
        )


    }

}

