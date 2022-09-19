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
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun CreateCalendarPage(navController: NavController, userViewModel: UserViewModel) {
    var selected: Any? by remember{
        mutableStateOf("null")
    }
    var name by remember {
        mutableStateOf("")
    }

    var selectedGroupId by remember { mutableStateOf<Int?>(null) }
    val selectedProfileIds = remember {
        mutableStateListOf<Int>()
    }

    Column() {

        TitleBar(navController = navController, title = "Create")

        CustomTextField(helper = "Name", hint = "Name", input = name, onChange = {name = it}, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(
            Modifier
                .weight(1F)
                .verticalScroll(rememberScrollState())) {

            Text("Groups", style = MaterialTheme.typography.h4, modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp), color = ExtendedTheme.colors.Black60)

            userViewModel.groups.forEach { (index, group) ->
                userViewModel.groups[index]?.let { SelectGroupItem(group = group, selected = selectedGroupId == group.id, modifier = Modifier
                    .clickable {
                        if (selectedGroupId == it.id) {
                            selectedGroupId = null
                        } else {
                            selectedGroupId = it.id
                            selectedProfileIds.clear()
                        }
                    }
                    .padding(horizontal = 8.dp)) }
            }

            Text("Friends", style = MaterialTheme.typography.h4, modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp), color = ExtendedTheme.colors.Black60)

            userViewModel.friends.forEach { (index, friend) ->
                userViewModel.friends[index]?.let { SelectProfileItem(profile = friend, selected = selectedProfileIds.contains(friend.user_id), modifier = Modifier
                    .clickable {
                        if (selectedProfileIds.contains(friend.user_id)) {
                            selectedProfileIds.remove(friend.user_id)
                        } else {
                            selectedGroupId = null
                            selectedProfileIds.add(friend.user_id)
                        }
                    }
                    .padding(horizontal = 8.dp)) }
            }
        }

        Divider(color = ExtendedTheme.colors.LightGray, thickness = 1.dp)

        CustomButton(buttonText = "Create", onClick = {
            var body: MutableMap<String, Any?> = mutableMapOf()

            if (selectedGroupId != null) {
                body["group_id"] = selectedGroupId
                }
            else {
                body["user_ids"] = selectedProfileIds

            }
            if (name.isNotBlank()){
                body["name"] = name
            }
            userViewModel.addCalendar(WeeklyApi.retrofitService.createCalendar(mapOf("Authorization" to "token ${userViewModel.token}"), body))
            navController.navigateUp()

        }, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 12.dp))
    }
}