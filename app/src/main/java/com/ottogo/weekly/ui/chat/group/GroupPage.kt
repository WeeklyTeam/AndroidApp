package com.ottogo.weekly.ui.chat.group


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.ui.calendar.ui.components.PlotMemberList
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.ui.theme.Black80
import com.ottogo.weekly.ui.theme.LightGray

@Composable
fun GroupPage(navController: NavController, groupId: Int, userViewModel: UserViewModel){
    val group = userViewModel.groups?.get(groupId)

    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        TitleBar(navController = navController, title = "", iconButtons = {
            IconButton(onClick = { navController.navigate("editGroupPage/$groupId") }, modifier = Modifier.size(56.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_edit_2_line),
                    contentDescription = null,
                )
            }
        })

        Spacer(modifier = Modifier.padding(top = 44.dp))

        if (group != null) {
            GroupInfo(group)
            AddMember(navController, groupId)
            Column(modifier = Modifier
                .weight(1F)
                .verticalScroll(rememberScrollState())) {
                PlotMemberList(title = "Members", members = group.members.asIterable())
            }
        }

        CustomButton(buttonText = "Leave",
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            foregroundColor = Black80,
            backgroundColor = LightGray,
            onClick = {})
    }
}

@Composable
fun AddMember(navController: NavController, groupId: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 30.dp),
    ) {
        IconButton(
            onClick = { navController.navigate("groupAddMembersPage/$groupId") },
            modifier = Modifier.size(56.dp)) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_user_add_fill),
                contentDescription = null,
            )
        }
        Text(
            text = "Add members",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun GroupInfo(group: Group?) {
    if (group != null) {
        ProfilePicture(url = group.image, modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.padding(bottom = 16.dp))
        Text(text = group.name, style = MaterialTheme.typography.h1)
    }
}