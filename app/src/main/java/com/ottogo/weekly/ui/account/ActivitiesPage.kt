package com.ottogo.weekly.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.login.ActivityCategoriesViewModel
import com.ottogo.weekly.ui.login.ActivityList
import com.ottogo.weekly.ui.login.SignupInterestsScreen
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun ActivitiesPage(navController: NavController, userViewModel: UserViewModel) {
    userViewModel.token?.let { ActivityCategoriesViewModel(it) }?.let {
        ActivitiesScreen(
            navController, userViewModel,
            it
        )
    }
}

@Composable
fun ActivitiesScreen(navController: NavController, userViewModel: UserViewModel, activityCategoriesViewModel:ActivityCategoriesViewModel) {

    Column {
        TitleBar(navController = navController, title = "Ideas", iconButtons = {
            IconButton(onClick = { navController.navigate("addActivityPage") }, modifier = Modifier.size(56.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_add_line),
                    contentDescription = null,
                )
            }
        })

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {





            for (activityCategoryIndex in 0 until activityCategoriesViewModel.activityCategories.size) {
                ActivityList(activityCategoryIndex, activityCategoriesViewModel)
            }



        }


    }
}