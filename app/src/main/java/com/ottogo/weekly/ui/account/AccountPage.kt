package com.ottogo.weekly.ui.account

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.account.ui.components.AccountOption
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun AccountPage(navController: NavController, userViewModel: UserViewModel) {

    val context = LocalContext.current
    val manager = ReviewManagerFactory.create(context)


    Column(Modifier.verticalScroll(rememberScrollState())) {

        TitleBar(navController = navController, title = "Account")

        Spacer(modifier = Modifier.height(16.dp))
        
        Card(elevation = 0.dp, border = BorderStroke(1.dp, ExtendedTheme.colors.LightGray), shape = RoundedCornerShape(12.dp), modifier = Modifier
            .height(96.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { navController.navigate("editProfilePage") }
            ) {
            Row(Modifier.padding(16.dp)) {
                ProfilePicture(url = userViewModel.profile?.profile_picture, size = 64)
                Spacer(Modifier.width(16.dp))

                Column() {
                    Spacer(Modifier.height(8.dp))
                    userViewModel.profile?.let { Text(text = it.name, style = MaterialTheme.typography.body1) }
                    Spacer(Modifier.height(4.dp))
                    userViewModel.profile?.let { Text(text = it.username, style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60) }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AccountOption(R.drawable.ic_lightbulb_line, "Ideas") {
            navController.navigate("activitiesPage")
        }

        AccountOption(R.drawable.ic_star_half_line, "Review") {

            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = task.result
                    val flow = reviewInfo?.let { manager.launchReviewFlow(context as Activity, it) }
                    flow?.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                } else {
                    // There was some problem, log or handle the error code.
                }
            }


        }

        AccountOption(R.drawable.ic_contacts_book_line, "Contacts") {
            navController.navigate("contactsPage")
        }

        AccountOption(R.drawable.ic_calendar_check_line, "Sync") {
            navController.navigate("calendarSyncPage")
        }

        AccountOption(R.drawable.ic_edit_2_line, "Profile") {
            navController.navigate("editProfilePage")
        }

        AccountOption(R.drawable.ic_flag_line, "Report") {
            navController.navigate("reportPage")
        }

        AccountOption(R.drawable.ic_settings_3_line, "Settings") {
            navController.navigate("settingsPage")
        }

        AccountOption(R.drawable.ic_logout_box_line, "Logout") { userViewModel.logout(context) }


    }
}

