package com.ottogo.weekly.ui.chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
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
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ChatPage(navController: NavController, userViewModel: UserViewModel) {
    Column {
        ChatTitleBar(navController = navController, userViewModel = userViewModel)



        if (userViewModel.friends?.count() ?:0 > 0) {
            if (userViewModel.requests.count() > 0) {
                FriendRequests(requests = userViewModel.requests)
            }

            chatTabRow(userViewModel = userViewModel, navController = navController)
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1F))

                Text(text = "Search your contacts\nFind your friends", style = MaterialTheme.typography.h2, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))

                CustomButton(buttonText = "Check now", onClick = {navController.navigate("contactsPage")}, modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp))
                
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

        TabBar(pagerState = pagerState, tabItems = tabItems, modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
            .border(3.dp, LightGray, RoundedCornerShape(22.dp)))

        HorizontalPager(
            count = tabItems.size,
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            if (page == 0){
                Column() {
                    userViewModel.friendsOrder!!.forEach { friend ->
                        Log.d("friends", friend.toString())
                        ChatListItem(name = userViewModel.friends!![friend]!!.name, description = userViewModel.friends!![friend]!!.messages.firstOrNull()?.message
                            ?: "Say Hi!", profilePicture = userViewModel.friends!![friend]!!.profile_picture, modifier = Modifier.clickable{
                            navController.navigate("privateChatPage/$friend")
                        }, timestamp = userViewModel.friends!![friend]!!.messages.firstOrNull()?.timestamp)
                    }
                    Spacer(Modifier.weight(1F))
                }

            }
            else {
                Column() {
                    addGroup(navController= navController)
                    userViewModel.groupsOrder!!.forEach { group ->
                        ChatListItem(name = userViewModel.groups!![group]!!.name, description = userViewModel.groups!![group]!!.messages.firstOrNull()?.message
                            ?: "Say Hi!", profilePicture = null,
                            onImageClick = {navController.navigate("groupPage/$group")},
                            modifier = Modifier.clickable{
                            navController.navigate("groupChatPage/$group")
                        }, timestamp = null)
                    }
                    Spacer(Modifier.weight(1F))

                }
            }
        }
    }



}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabBar(pagerState: PagerState, tabItems: List<String>, modifier: Modifier = Modifier){

    val coroutineScope = rememberCoroutineScope()

    Row(modifier) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = LightGray,
            modifier = Modifier

                .height(44.dp)
                .width(288.dp)
                .clip(RoundedCornerShape(30.dp)),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .pagerTabIndicatorOffset(pagerState, tabPositions)
                        .width(0.dp)
                        .height(0.dp)
                )
            }
        ) {
            tabItems.forEachIndexed { index, title ->
//                val color = remember {
//                    Animatable(Color.Transparent)
//                }
//
//                LaunchedEffect(key1 = pagerState.currentPage == index) {
//                    // if tab is selected color is white, else LightGray
//                    color.animateTo(
//                        if (pagerState.currentPage == index) Color.White
//                        else LightGray
//                    )
//                }
                Tab(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index) // method has to be called in a coroutine
                        }
                    },
                    content = {
                        Text(
                            title, style = if (pagerState.currentPage == index)
                                TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = nunitoFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Black80
                                )
                            else TextStyle(
                                fontSize = 16.sp,
                                fontFamily = nunitoFamily,
                                fontWeight = FontWeight.Bold,
                                color = Black60
                            )
                        )
                    },
                    selected = pagerState.currentPage == index,
                    modifier = Modifier
                        .background(
                            color = if (pagerState.currentPage == index){ MaterialTheme.colors.onPrimary } else { Color.Transparent },
                            shape = RoundedCornerShape(22.dp)
                        )
                    )
            }

        }
    }
}

// changes text style when tab is chosen




//todo: call this when state is group
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
                tint = Purple
            )
        }
        Spacer(modifier = Modifier
            .width(16.dp)
            .height(64.dp))
        Text(text = "Create Group",
            fontSize = 18.sp,
            fontWeight = FontWeight(400),
            fontFamily = nunitoFamily,
            textAlign = TextAlign.Center)
    }
}



@SuppressLint("SimpleDateFormat")
@Composable
fun ChatListItem(
    name: String,
    description: String,
    profilePicture: String?,
    timestamp: Date?,
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

    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
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

        Text(
            text = dateText, style = MaterialTheme.typography.body2, color = Black60, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), maxLines = 1
        )
    }
}

@Composable
fun ChatTitleBar(navController: NavController, userViewModel: UserViewModel){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp).padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

        ProfilePicture(url = userViewModel.profile?.profile_picture, modifier = Modifier.padding(vertical = 12.dp).clip(CircleShape)
            .clickable {
                navController.navigate("accountPage")
            })

        Spacer(Modifier.width(16.dp))

        Text(text = "Chat", Modifier.weight(1F), style = MaterialTheme.typography.h1)



        IconButton(onClick = { navController.navigate("searchPage") }, modifier = Modifier.size(58.dp)) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(id = R.drawable.ic_search_line),
                contentDescription = null,
            )
        }
    }



}