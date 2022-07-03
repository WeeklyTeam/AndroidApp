package com.ottogo.weekly.ui.chat.group

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun AddGroupMembersPage(navController: NavController, userViewModel: UserViewModel) {
    Column {
        TitleBar(
            navController = navController,
            title = "Add",
            modifier = Modifier.padding(start = 24.dp, top = 44.dp, bottom = 12.dp)
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = LightGray,
            thickness = 1.dp
        )
        for (profileID in userViewModel.friends!!) {
            val profile = userViewModel.friends!![profileID.key]
            profile?.let { MemberItem(profile = it) }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = LightGray,
            thickness = 1.dp
        )
        DoneBtn()
    }
}

@Composable
fun DoneBtn () {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 16.dp, end = 16.dp, bottom = 50.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        CustomButton(buttonText = "Done",
            // TODO: Implement Done btn functionality
            onClick = {})
    }
}

@Composable
fun MemberItem(profile: Profile?) {
    val checkedState = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (profile != null) {
            ProfilePicture(
                url = profile.profile_picture,
                modifier = Modifier
                    .size(48.dp)
            )
            Text(
                text = profile.name,
                style = MaterialTheme.typography.body1,
                modifier=Modifier.padding(horizontal = 16.dp))
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it},
                modifier = Modifier.size(20.dp)
            )
        }
    }
}