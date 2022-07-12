package com.ottogo.weekly.ui.chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import com.ottogo.weekly.ui.theme.Black40
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.GroupPicture
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ChatPage(navController: NavController, userViewModel: UserViewModel) {
    Column {
        ChatTitleBar(navController = navController, userViewModel = userViewModel)



        if (userViewModel.friends.count() > 0) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (userViewModel.requests.count() > 0) {
                    FriendRequests(requests = userViewModel.requests)
                }

                chatTabRow(userViewModel = userViewModel, navController = navController)

                Spacer(Modifier.height(60.dp))
            }

        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1F))

                Text(text = "This app is way more fun\nwith friends", style = MaterialTheme.typography.h2, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))

                CustomButton(buttonText = "Check contacts", onClick = {navController.navigate("contactsPage")}, modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Or tap ", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)
                    Icon(painter = painterResource(id = R.drawable.ic_search_line), contentDescription = "search", tint = ExtendedTheme.colors.Black60, modifier = Modifier.size(15.dp))
                    Text(" to search", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)

                }
                
                Spacer(modifier = Modifier.height(64.dp))

                FriendRequests(requests = userViewModel.requests)

                Spacer(Modifier.weight(1F))
            }

        }
    }
}

@Composable
fun FriendRequests(requests: List<Profile>){
    Column(Modifier.padding(top = 4.dp)) {
        requests.forEach{ profile ->
            Row(Modifier.clickable{}) {
                ProfilePicture(url = profile.profile_picture, modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

                Column() {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(profile.name, style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Wants to be your friend", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)
                }

                Spacer(modifier = Modifier.weight(1F))

                Icon(painter = painterResource(id = R.drawable.ic_more_fill), tint = ExtendedTheme.colors.Black60, contentDescription = "more", modifier = Modifier
                    .padding(vertical = 14.dp, horizontal = 16.dp)
                    .size(36.dp)
                    .background(color = ExtendedTheme.colors.LightGray, shape = CircleShape)
                    .padding(7.dp))
            }

        }
    }

}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun chatTabRow(userViewModel: UserViewModel, navController: NavController){
    val pagerState = rememberPagerState()
    val tabItems = listOf("Friends", "Groups")


    val url = "https://www.pngfind.com/pngs/m/610-6104451_image-placeholder-png-user-profile-placeholder-image-png.png"
    val name = "Jessica Jones"
    val message = "Hey! What's up."

    // tab row structure
    Column() {

        Spacer(Modifier.height(12.dp))

        userViewModel.chats.forEach {
            when (it) {
                is Profile ->
                    ChatListItem(name = it.name, description = it.messages.firstOrNull()?.message
                        ?: "Say Hi!", profilePicture = it.profile_picture, modifier = Modifier.clickable{
                        navController.navigate("privateChatPage/${it.user_id}")
                    }, timestamp = it.messages.firstOrNull()?.timestamp, notSeen = it.messages.firstOrNull()?.seen == false)
                is Group ->
                    GroupChatListItem(group=it,
                            onImageClick = {navController.navigate("groupPage/${it.id}")},
                            modifier = Modifier.clickable{
                            navController.navigate("groupChatPage/${it.id}")
                        })

            }

        }


    }



}



@SuppressLint("SimpleDateFormat")
@Composable
fun ChatListItem(
    name: String,
    description: String,
    profilePicture: String?,
    timestamp: Date?,
    notSeen: Boolean,
    onImageClick: () -> Unit = {},
    modifier: Modifier = Modifier
){

    var dateText = ""
    Log.d("timestamp", timestamp.toString())


    if (timestamp != null){
        val diff: Long = Date().time - timestamp.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30
        val years = days / 12

        Log.d("Timestampp", SimpleDateFormat("yyyy").format(timestamp))
        Log.d("Timestampp", SimpleDateFormat("MMM dd").format(timestamp))

        if (years > 0) {
            Log.d("timestamp", "years")
            dateText = SimpleDateFormat("yyyy").format(timestamp)
        } else if (days > 1) {
            dateText = SimpleDateFormat("MMM dd").format(timestamp)
            Log.d("Timestampp", SimpleDateFormat("MMM D").format(timestamp))

        } else if (days > 0) {
            dateText = "Yesterday"
        } else {
            dateText = SimpleDateFormat("h:mm a").format(timestamp)
            Log.d("timestamp", dateText)
            Log.d("timestamp", "hey")

        }
    }

    Row(
        modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min), horizontalArrangement = Arrangement.SpaceBetween){
        Row() {
            ProfilePicture(
                profilePicture,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(CircleShape)
                    .clickable {
                        onImageClick()
                    }
            )

            Column() {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = name, style = MaterialTheme.typography.body1, maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description, style = MaterialTheme.typography.body2, color = Black40, maxLines = 1
                )
            }
        }

        Column(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = dateText, style = MaterialTheme.typography.body2, color = Black60, maxLines = 1
            )

            if (notSeen) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                ) {}
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun GroupChatListItem(
    group: Group,
    onImageClick: () -> Unit = {},
    modifier: Modifier = Modifier
){

    var dateText = ""
    var timestamp = group.messages.firstOrNull()?.timestamp


    if (timestamp != null){
        val diff: Long = Date().time - timestamp.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30
        val years = days / 12

        Log.d("Timestampp", SimpleDateFormat("yyyy").format(timestamp))
        Log.d("Timestampp", SimpleDateFormat("MMM dd").format(timestamp))

        if (years > 0) {
            Log.d("timestamp", "years")
            dateText = SimpleDateFormat("yyyy").format(timestamp)
        } else if (days > 1) {
            dateText = SimpleDateFormat("MMM dd").format(timestamp)
            Log.d("Timestampp", SimpleDateFormat("MMM D").format(timestamp))

        } else if (days > 0) {
            dateText = "Yesterday"
        } else {
            dateText = SimpleDateFormat("h:mm a").format(timestamp)
            Log.d("timestamp", dateText)
            Log.d("timestamp", "hey")

        }
    }

    Row(
        modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min), horizontalArrangement = Arrangement.SpaceBetween){
        Row() {
            GroupPicture(
                group,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable {
                        onImageClick()
                    }
            )

            Column() {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = group.name, style = MaterialTheme.typography.body1, maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = group.messages.firstOrNull()?.message
                        ?: "Say Hi!", style = MaterialTheme.typography.body2, color = Black40, maxLines = 1
                )
            }
        }

        Column(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = dateText, style = MaterialTheme.typography.body2, color = Black60, maxLines = 1
            )

            if (group.messages.firstOrNull()?.seen == false) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                ) {}
            }

            Spacer(Modifier.height(8.dp))
        }

    }
}

@Composable
fun ChatTitleBar(navController: NavController, userViewModel: UserViewModel){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

        ProfilePicture(url = userViewModel.profile?.profile_picture, modifier = Modifier
            .padding(vertical = 12.dp)
            .clip(CircleShape)
            .clickable {
                navController.navigate("accountPage")
            })

        Spacer(Modifier.width(16.dp))

        Text(text = "Chat", Modifier.weight(1F), style = MaterialTheme.typography.h1)

        IconButton(onClick = { navController.navigate("chatSearchPage") }, modifier = Modifier.size(58.dp)) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(id = R.drawable.ic_chat_new_line),
                contentDescription = null,
            )
        }

        IconButton(onClick = { navController.navigate("searchPage") }, modifier = Modifier.size(58.dp)) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(id = R.drawable.ic_search_line),
                contentDescription = null,
            )
        }
    }



}