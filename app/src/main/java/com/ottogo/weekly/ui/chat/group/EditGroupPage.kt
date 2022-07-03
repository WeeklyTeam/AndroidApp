package com.ottogo.weekly.ui.chat.group


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ottogo.weekly.ui.theme.LightGray
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun EditGroupPage(navController: NavController, groupId: Int, userViewModel: UserViewModel) {
    val group = userViewModel.groups?.get(133)
    var groupName by remember { mutableStateOf(group?.name) }

    Column {
        TitleBar(
            navController = navController,
            title = "Edit",
            modifier = Modifier.padding(start = 24.dp, top = 44.dp, bottom = 12.dp))
        Divider(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            color = LightGray,
            thickness = 1.dp
        )
        if (group != null) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ProfilePicture(
                    url = group.image,
                    modifier = Modifier.size(96.dp))
            }
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
            EditGroupName(text = groupName, onTextChange = { groupName = it })
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
            MembersAddSideScroll(group = group)
            SaveBtn()
        }
    }
}

@Composable
fun EditGroupName(text: String?, onTextChange: (String) -> Unit) {
    Column() {
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        CustomTextField(
            helper = "Group",
            hint = "Group",
            input = text!!,
            onChange = onTextChange,
            modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
fun MembersAddSideScroll(group: Group?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        LazyRow(
//            // TODO: Find a way to create a horizontal scrollable list
//        )
        CustomButton(
            buttonText = "Add",
            modifier = Modifier
                .size(width = 100.dp, 40.dp)
                .border(
                    width = 3.dp,
                    color = com.ottogo.weekly.ui.theme.LightGray,
                    shape = RoundedCornerShape(12.dp)
                ),
            foregroundColor = com.ottogo.weekly.ui.theme.Purple,
            backgroundColor = com.ottogo.weekly.ui.theme.White,
            onClick = {} )
    }
}

// TODO: Implement Save btn functionality
@Composable
fun SaveBtn () {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 122.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        CustomButton(buttonText = "Save",
            onClick = {})
    }
}