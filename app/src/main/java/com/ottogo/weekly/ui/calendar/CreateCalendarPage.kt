package com.ottogo.weekly.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.WeeklyApiService
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.chat.SelectGroupItem
import com.ottogo.weekly.ui.chat.SelectProfileItem
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun CreateCalendarPage(navController: NavController, userViewModel: UserViewModel) {
    var selected: Any? by remember{
        mutableStateOf("null")
    }


    Column() {

        TitleBar(navController = navController, title = "Create")

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(Modifier.weight(1F).verticalScroll(rememberScrollState())) {

            Text("Groups", style = MaterialTheme.typography.h4, modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp), color = ExtendedTheme.colors.Black60)

            userViewModel.groupsOrder?.forEach { index ->
                userViewModel.groups!![index]?.let { SelectGroupItem(group = it, selected = if (selected is Group) { (selected as Group).id == userViewModel.groups!![index]!!.id} else { false }, modifier = Modifier
                    .clickable {selected = userViewModel.groups!![index]}) }
            }

            Text("Friends", style = MaterialTheme.typography.h4, modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp), color = ExtendedTheme.colors.Black60)

            userViewModel.friendsOrder?.forEach { index ->
                userViewModel.friends!![index]?.let { SelectProfileItem(profile = it, selected = if (selected is Profile) { (selected as Profile).user_id == userViewModel.friends!![index]!!.user_id} else { false }, modifier = Modifier
                    .clickable {selected = userViewModel.friends!![index]}) }
            }
        }

        Divider(color = ExtendedTheme.colors.LightGray, thickness = 1.dp)

        CustomButton(buttonText = "Create", onClick = {
            lateinit var body: Map<String, Any>

            when (selected) {
                is Group -> {
                    body = mapOf("group_id" to (selected as Group).id)
                }
                is Profile -> {
                    body = mapOf("user_id" to (selected as Profile).user_id)
                }
            }
            WeeklyApi.retrofitService.createCalendar(mapOf("Authorization" to "token ${userViewModel.token}"), body)
            navController.navigateUp()

        }, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 12.dp))
    }
}