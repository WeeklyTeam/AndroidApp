package com.ottogo.weekly.ui.chat.group


import androidx.compose.foundation.clickable
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
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.calendar.ui.components.PlotMemberList
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.GroupPicture
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.ui.theme.Black80
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.LightGray

@Composable
fun GroupPage(navController: NavController, groupId: Int, userViewModel: UserViewModel, openSheet: (profile: Profile?) -> Unit){
    val group = userViewModel.groups[groupId]
    Column(modifier = Modifier.padding()) {
        TitleBar(navController = navController, title = "", iconButtons = {
            IconButton(onClick = { navController.navigate("editGroupPage/$groupId") }, modifier = Modifier.size(56.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_edit_2_line),
                    contentDescription = null,
                )
            }
        })

        if (group != null) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
                GroupInfo(navController, group)
                Spacer(modifier = Modifier.padding(bottom = 24.dp))
                AddMember(navController, groupId)
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
                if (group.members.count() > 0) {
                    PlotMemberList(title = "Members", members = group.members, openSheet = openSheet)
                }
                if (group.invited.count() > 0) {
                    PlotMemberList(title = "Invited", members = group.invited, openSheet = openSheet)
                }
                Spacer(modifier = Modifier.padding(bottom = 16.dp))

                if (!group.is_invited) {
                    CustomButton(buttonText = "Leave",
                        textColor = ExtendedTheme.colors.Black60,
                        backgroundColor = LightGray,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                        onClick = {
                            WeeklyApi.retrofitService.leaveGroup(
                                mapOf("Authorization" to "token ${userViewModel.token}"),
                                groupId
                            )
                            userViewModel.removeGroup(groupId = groupId)
                            navController.navigateUp()
                        })
                }

            }
            Spacer(modifier = Modifier.height(48.dp))


        }
    }
}

@Composable
fun AddMember(navController: NavController, groupId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("groupAddMembersPage/$groupId") }
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_user_add_line),
                contentDescription = null,
            )

        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = "Add members",
            style = MaterialTheme.typography.body1
        )
    }
}



@Composable
fun GroupInfo(navController: NavController, group: Group?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (group != null) {
            GroupPicture(group = group, size = 72, onImageClick = {
                navController.navigate("editGroupPage/${group.id}")
            })
            Spacer(modifier = Modifier.padding(bottom = 16.dp))
            Text(text = group.name, style = MaterialTheme.typography.h1, modifier = Modifier.clickable { navController.navigate("editGroupPage/${group.id}") })
        }
    }
}