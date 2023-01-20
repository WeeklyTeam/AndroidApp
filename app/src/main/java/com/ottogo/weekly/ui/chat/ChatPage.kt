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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.ui.theme.Black40
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameDay
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameYear
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.GroupPicture
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ChatPage(navController: NavController, userViewModel: UserViewModel, openSheet: (profile: Profile?) -> Unit) {
    Column {
        ChatTitleBar(navController = navController, userViewModel = userViewModel)



        if (userViewModel.friends.count() > 0) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (userViewModel.requests.count() > 0) {
                    FriendRequests(requests = userViewModel.requests, openSheet)
                }

                chatTabRow(userViewModel = userViewModel, navController = navController, openSheet = openSheet)

                Spacer(Modifier.height(96.dp))
            }

        } else if (userViewModel.profile == null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.weight(1F))

                CircularProgressIndicator()

                Spacer(Modifier.weight(1F))
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1F))

                Text(text = "This app is way more fun\nwith friends", style = MaterialTheme.typography.h2, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))

                CustomButton(buttonText = "Sync contacts", onClick = {navController.navigate("contactsPage")}, modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Or tap ", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)
                    Icon(painter = painterResource(id = R.drawable.ic_search_line), contentDescription = "search", tint = ExtendedTheme.colors.Black60, modifier = Modifier.size(15.dp))
                    Text(" to search", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)

                }

                Spacer(modifier = Modifier.height(64.dp))

                FriendRequests(requests = userViewModel.requests, openSheet)

                Spacer(Modifier.weight(1F))
            }

        }
    }
}

@Composable
fun FriendRequests(requests: List<Profile>, openSheet: (profile: Profile) -> Unit){
    Column(Modifier.padding(top = 4.dp)) {
        requests.forEach{ profile ->
            Row(Modifier.clickable{openSheet(profile)}) {
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


@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun chatTabRow(userViewModel: UserViewModel, navController: NavController, openSheet: (profile: Profile) -> Unit){
    val pagerState = rememberPagerState()
    val tabItems = listOf("Friends", "Groups")


    val url = "https://www.pngfind.com/pngs/m/610-6104451_image-placeholder-png-user-profile-placeholder-image-png.png"
    val name = "Jessica Jones"
    val message = "Hey! What's up."

    // tab row structure
    Column() {

        Spacer(Modifier.height(12.dp))

        userViewModel.chats.forEach { it ->
            when (it) {
                is Profile -> {
                    var description = "Say Hi!"
                    val firstMessage = it.messages.firstOrNull()
                    Log.d("chatStatus", "${it.name}: $firstMessage")
                    if (firstMessage?.message != null){
                        description = firstMessage.message
                    } else if (firstMessage?.gif != null) {
                        description = "[GIF]"
                    }
                    else if (firstMessage?.image != null) {
                        description = "[Image]"

                    }
                    ChatListItem(name = it.name,
                        description = description,
                        profilePicture = it.profile_picture,
                        modifier = Modifier.combinedClickable(
                            onClick = { navController.navigate("privateChatPage/${it.user_id}") },
                            onLongClick = { openSheet(it) },
                        ),
                        timestamp = it.messages.firstOrNull()?.timestamp,
                        notSeen = it.messages.firstOrNull()?.seen == false && it.messages.firstOrNull()?.user_id != userViewModel.profile?.user_id,
                        onImageClick = { openSheet(it) })
                }is Group ->
                    if (!it.is_invited) {
                        GroupChatListItem(group = it,
                            onImageClick = { navController.navigate("groupPage/${it.id}") },
                            modifier = Modifier.combinedClickable(
                                onClick = { navController.navigate("groupChatPage/${it.id}") },
                                onLongClick = { navController.navigate("groupPage/${it.id}") },
                            ))
                    } else {
                        GroupChatInviteListItem(
                            navController = navController,
                            group = it,
                            onAcceptInvite = {
                                try {
                                    WeeklyApi.retrofitService.acceptGroupInvite(mapOf("Authorization" to "token ${userViewModel.token}"), it.id)
                                    userViewModel.removeGroup(it.id)
                                    val newInvited = it.invited.toMutableList()
                                    newInvited.removeAll { it.user_id == userViewModel.profile?.user_id ?: -1 }
                                    val newMembers = it.members.toMutableList()
                                    newMembers.removeAll { it.user_id == userViewModel.profile?.user_id ?: -1 }

                                    userViewModel.addGroup(it.copy(
                                        invited = newInvited,
                                        members = newMembers,
                                        is_invited = false

                                    ))

                                } catch (e: Exception) {
                                    
                                }
                                             },
                            modifier = Modifier.clickable {
                                navController.navigate("groupChatPage/${it.id}")
                            },
                        onDenyInvite = {
                            try {
                                WeeklyApi.retrofitService.leaveGroup(mapOf("Authorization" to "token ${userViewModel.token}"), it.id)
                                userViewModel.removeGroup(it.id)

                            } catch (e: Exception) {

                            }
                        })
                    }

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
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)


        if (isSameDay(timestamp, Date())) {
            dateText = SimpleDateFormat("h:mm a").format(timestamp)

        } else if (isSameDay(timestamp, yesterday.time)) {
            dateText = "Yesterday"

        } else if (isSameYear(timestamp, Date())) {
            dateText = SimpleDateFormat("MMM d").format(timestamp)
        } else {
            dateText = SimpleDateFormat("YYYY").format(timestamp)

        }
    }


    Row(
        modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top){
        Row(modifier = Modifier.weight(1f)) {
            ProfilePicture(
                profilePicture,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(CircleShape)
                    .clickable {
                        onImageClick()
                    }
            )

            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = name, style = MaterialTheme.typography.body1, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description, modifier = Modifier.weight(1f), style = MaterialTheme.typography.body2, color = Black40, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }


        Column(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween) {
            Spacer(Modifier.height(8.dp))

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
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)


        if (isSameDay(timestamp, Date())) {
            dateText = SimpleDateFormat("h:mm a").format(timestamp)

        } else if (isSameDay(timestamp, yesterday.time)) {
            dateText = "Yesterday"

        } else if (isSameYear(timestamp, Date())) {
            dateText = SimpleDateFormat("MMM d").format(timestamp)
        } else {
            dateText = SimpleDateFormat("YYYY").format(timestamp)

        }
    }

    Row(
        modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min), horizontalArrangement = Arrangement.SpaceBetween){
        Row(modifier = Modifier.weight(1f)) {
            GroupPicture(
                group,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onImageClick = onImageClick

            )

            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = group.name, style = MaterialTheme.typography.body1, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = group.messages.firstOrNull()?.message
                        ?: "Say Hi!", style = MaterialTheme.typography.body2, color = Black40, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween) {

            Spacer(Modifier.height(8.dp))
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
fun GroupChatInviteListItem(
    navController: NavController,
    group: Group,
    modifier: Modifier = Modifier,
    onAcceptInvite: suspend () -> Unit,
    onDenyInvite: suspend () -> Unit,
){
   val coroutine = rememberCoroutineScope()

    Column() {

        Row(
            modifier
                .clickable {
                    navController.navigate("groupPage/${group.id}")
                }
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row() {
                GroupPicture(
                    group,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Column() {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = group.name, style = MaterialTheme.typography.body1, maxLines = 1
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "You've been invited!",
                        style = MaterialTheme.typography.body2,
                        color = Black40,
                        maxLines = 1
                    )
                }
            }

        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp, top = 8.dp)) {
            CustomButton(buttonText = "Deny", textColor = ExtendedTheme.colors.Black60, backgroundColor = ExtendedTheme.colors.LightGray, onClick = { coroutine.launch { onDenyInvite() } }, modifier = Modifier.weight(1F))
            Spacer(modifier = Modifier.width(16.dp))
            CustomButton(buttonText = "Accept", onClick = { coroutine.launch { onAcceptInvite() } }, modifier = Modifier.weight(1F))

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