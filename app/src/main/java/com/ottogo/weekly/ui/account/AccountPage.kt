package com.ottogo.weekly.ui.account

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun AccountPage(navController: NavController, userViewModel: UserViewModel) {
    Column() {
        CustomButton(buttonText = "Logout", onClick = {userViewModel.token = null} )

    }
}