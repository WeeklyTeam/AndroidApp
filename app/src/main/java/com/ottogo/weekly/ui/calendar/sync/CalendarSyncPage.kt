package com.ottogo.weekly.ui.calendar.sync

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.account.ui.components.AccountOption
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme

@Composable
fun CalendarSyncPage(navController: NavController) {

    Column {
        TitleBar(navController = navController, title = "Options")
        Divider(color = ExtendedTheme.colors.LightGray, thickness = 1.dp)

        AccountOption(R.drawable.ic_canvas_bug_color_rgb, "Canvas") {
            // Daniel
            // Navigate to CanvasWebviewPage to preform Canvas oauth request (you can add your navigation by going to MainActivity and finding mainnavigation)
            // follow there for more instructions
        }

        AccountOption(R.drawable.ic_google_calendar_icon__2020_, "Google Calendar") {
            // Justin
            // Preform oauth request
            // This https://developers.google.com/identity/sign-in/android/start-integrating
            // then this https://developers.google.com/identity/sign-in/android/sign-in
            // finally navigate to the AddPlotsPage.kt (you can add your navigation by going to MainActivity and finding mainnavigation)
            // follow there for more instructions
        }

        AccountOption(R.drawable.ic_calendar_line, "Device") {

        }


    }
}