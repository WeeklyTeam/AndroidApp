package com.ottogo.weekly.ui.chat

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.GroupPicture
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChatSearchPage(navController: NavController, userViewModel: UserViewModel) {
    var searchText by remember { mutableStateOf("") }

    Column(){

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {

            SearchBar(searchText, Modifier.weight(1f)) { searchText = it.lowercase() }

            Spacer(modifier = Modifier.width(8.dp))

            CancelButton(navController)

        }

        Spacer(Modifier.height(16.dp))

        ChatResults(navController = navController, searchText = searchText, userViewModel = userViewModel)
    }
}

@Composable
fun addGroup(navController: NavController){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("createGroupPage") },
        verticalAlignment = Alignment.CenterVertically

    ) {
        // todo : replace user photo with icon here
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier
            .size(48.dp)
            .border(2.dp, LightGray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ){
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_add_line),
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        }
        Spacer(modifier = Modifier
            .width(16.dp)
            .height(72.dp))
        Text(text = "Create Group",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center)
    }
}





@Composable
fun ChatResults(navController: NavController, searchText: String, userViewModel: UserViewModel) {

    val searchResults = remember { mutableStateListOf<Profile>() }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        Card(shape = RoundedCornerShape(corner = CornerSize(12.dp)), elevation = 3.dp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)){
            addGroup(navController = navController)
        }

        Text("Recents", style = MaterialTheme.typography.h4, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = ExtendedTheme.colors.Black60)


        userViewModel.chats.filter {
            when (it) {
                is Profile ->
                    it.username.lowercase().contains(searchText) || it.name.lowercase().contains(searchText)
                is Group ->
                    it.name.lowercase().contains(searchText)

                else -> {
                    false
                }
            }
        }.forEach {
            when (it) {
                is Profile ->
                    ChatSearchResultsItem(name = it.name, userName = it.username, profilePicture = it.profile_picture, modifier = Modifier.clickable{
                        navController.navigate("privateChatPage/${it.user_id}")
                    })
                is Group ->
                    ChatSearchResultsItem(name = it.name, userName = "", profilePicture = null, modifier = Modifier.clickable{
                        navController.navigate("groupChatPage/${it.id}")
                    }, group = it)

            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatSearchResultsItem(name: String, userName: String, profilePicture: String?, group: Group? = null, modifier: Modifier = Modifier
)  {


    Row(modifier.padding(vertical = 8.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {

        if (group != null) {
            GroupPicture(group = group)
        } else {
            ProfilePicture(profilePicture)
        }

            Spacer(modifier = Modifier.width(16.dp))

            Column() {
                Text(text = name, style = MaterialTheme.typography.body1)
                Text(text = userName, style = MaterialTheme.typography.body2, color = Black40)
            }
        }
    }






