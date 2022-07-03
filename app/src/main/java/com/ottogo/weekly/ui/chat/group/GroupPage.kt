package com.ottogo.weekly.ui.chat.group


import android.util.Log
import androidx.compose.foundation.layout.*
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
    val group = userViewModel.groups?.get(133)
    // TODO: Fix groupid value being set to 0 for all calls
    Column(modifier = Modifier.padding(top=44.dp, bottom = 16.dp)) {
        TitleBar(navController = navController, title = "", iconButtons = {
            IconButton(onClick = { navController.navigate("editGroupPage") }, modifier = Modifier.size(56.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_edit_2_line),
                    contentDescription = null,
                )
            }
        })

        if (group != null) {
            GroupInfo(group)
            AddMember(navController, groupId)
            PlotMemberList(title = "Members", members = group.members.asIterable(), 25, 0)
            LeaveBtn()
        }
    }
}

@Composable
fun AddMember(navController: NavController, groupId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { navController.navigate("groupAddMembersPage/$groupId") },
            modifier = Modifier.size(56.dp)) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_add_circle_line),
                contentDescription = null,
            )
        }
        Text(
            text = "Add members",
            style = MaterialTheme.typography.body1
        )
    }
}

// TODO: Implement Leave btn functionality
@Composable
fun LeaveBtn () {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 122.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        CustomButton(buttonText = "Leave",
            foregroundColor = Black80,
            backgroundColor = LightGray,
            onClick = {})
    }
}

@Composable
fun GroupInfo(group: Group?) {
    Log.d("groups", group.toString())
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (group != null) {
            ProfilePicture(url = group.image, modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.padding(bottom = 16.dp))
            Text(text = group.name, style = MaterialTheme.typography.h1)
        }
    }
}