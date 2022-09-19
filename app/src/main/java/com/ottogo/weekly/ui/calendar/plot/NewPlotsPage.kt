package com.ottogo.weekly.ui.calendar.plot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.calendar.ui.components.PlotItem
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun NewPlotsPage(navController: NavController, userViewModel: UserViewModel) {


    Column () {
        TitleBar(navController = navController, title = "Plans")

        Column(Modifier
            .verticalScroll(rememberScrollState())) {
            userViewModel.plots.filter { !it.is_going }.forEach{
                PlotItem(plot = it, userViewModel = userViewModel) {
                    navController.navigate("plotPage/${it.id}")
                }
            }
        }
    }
}
