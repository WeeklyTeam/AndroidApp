package com.ottogo.weekly.ui.chat

import androidx.compose.animation.Animatable
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.Coil
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.Black40
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.theme.*
import kotlinx.coroutines.launch


@Composable
fun ChatPage(navController: NavController, userViewModel: UserViewModel) {
    Column {
        ChatTitleBar(navController = navController)


        // todo : pull url, name, message from database or internal db?
        val url = "https://www.pngfind.com/pngs/m/610-6104451_image-placeholder-png-user-profile-placeholder-image-png.png"
        val name = "Jessica Jones"
        val message = "Hey! What's up."

        chatTabRow(imageURL = url, message = message, name =name, navController= navController)
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun chatTabRow(imageURL: String,
               message: String ,
               name : String, navController: NavController){
    val tabItems = listOf("Friends", "Groups")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    // tab row structure
    Column(){
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = LightGray,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 12.dp,
                    end = 71.dp
                ) // offset row to the left
                .background(color = Color.Transparent) // transparent bc there is already a background color
                .clip(RoundedCornerShape(30.dp)),
            indicator = {
                tabPositions ->  TabRowDefaults.Indicator(
                Modifier
                    .pagerTabIndicatorOffset(pagerState, tabPositions)
                    .width(0.dp)
                    .height(0.dp)
                )
            }
            ) {
            tabItems.forEachIndexed { index, title ->
                val color = remember {
                    Animatable(Color.Transparent)
                }

                LaunchedEffect(key1 = pagerState.currentPage == index) {
                    // if tab is selected color is white, else LightGray
                    color.animateTo(
                        if (pagerState.currentPage == index) Color.White
                        else LightGray)
                }
                Tab(
                    // changes text style when tab is chosen
                    text = {
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
                    modifier = Modifier.background(
                        color = color.value,
                        shape = RoundedCornerShape(30.dp)
                    ),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index) // method has to be called in a coroutine
                        }
                    })
                }
            }
        }
        
        HorizontalPager(
            count = tabItems.size,
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
            ) { page ->
            if (page == 0){
                FriendListPreview(imageURL = imageURL, message = message, name =name)
            }
            else {
                Column() {
                    addGroup(navController= navController)
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    FriendListPreview(imageURL = imageURL, message = message, name =name)
                }
            }
        }
}


//todo: call this when state is group
@Composable
fun addGroup(navController: NavController){
    Row(
        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("createGroupPage", )}

    ) {
        // todo : replace user photo with icon here
        Spacer(modifier = Modifier.padding(all = 16.dp))
        Box(modifier = Modifier
            .size(48.dp)
            .border(2.dp, Color.LightGray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ){
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_add_line),
                contentDescription = null,
                tint = Purple
                )
        }
        Spacer(modifier = Modifier.padding(start=16.dp))
        Text(text = "Create Group",
            fontSize = 18.sp,
            fontWeight = FontWeight(400),
            fontFamily = nunitoFamily,
            textAlign = TextAlign.Center)
    }
}


// this could be used for friend or group
@Composable
fun FriendListPreview(
    imageURL: String,
    message: String ,
    name : String
    ) {
    LazyColumn(Modifier.fillMaxWidth()){
        items(50) {
            Row(Modifier.fillMaxWidth()){
                Spacer(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                UserPhoto(imageURL)

                Column(Modifier.padding(bottom = 10.dp, top = 8.dp, start = 16.dp)) {
                    Text(text = "$name",
                        fontSize = 18.sp,
                        fontWeight = FontWeight(400),
                        fontFamily = nunitoFamily
                    )
                    Text(text = "$message",
                        fontSize = 14.sp,
                        fontWeight = FontWeight(400),
                        fontFamily = nunitoFamily
                    )
                }
            }
        }
    }
}

@Composable
fun ChatTitleBar(navController: NavController){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Chat", Modifier.padding(top = 24.dp, bottom = 8.dp), style = MaterialTheme.typography.h1)


        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_add_circle_line),
                    contentDescription = null,
                )
            }
            IconButton(onClick = { navController.navigate("peopleSearch") }) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_search_line),
                    contentDescription = null,
                )
            }
        }
    }

}

@Composable
fun UserPhoto(imageURL: String){
    CoilImage(imageURL = imageURL)
}

@Composable
fun CoilImage(
    imageURL: String,
    modifier: Modifier = Modifier.clip(CircleShape)
    ){
    Box(
        modifier = Modifier
            .height(48.dp)
            .width(48.dp)
    )
    {
        val painter = rememberImagePainter(
            data = imageURL,
            builder = {}
        )
        Image(painter = painter, contentDescription="profile image")
    }
}