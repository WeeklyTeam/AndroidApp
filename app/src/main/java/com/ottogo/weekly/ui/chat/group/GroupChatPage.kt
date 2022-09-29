package com.ottogo.weekly.ui.chat.group

import android.os.Build
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import coil.size.Size
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameDay
import com.ottogo.weekly.ui.chat.GiphyBottomModalSheet
import com.ottogo.weekly.ui.chat.PrivateChatPageContent
import com.ottogo.weekly.ui.components.GroupPicture
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.*
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import java.text.SimpleDateFormat


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupChatPage(navController: NavController, userViewModel: UserViewModel, groupId: Int, webSocket: WebSocketClient?, openSheet: (profile: Profile?) -> Unit) {


    var sheetSwipeableState = rememberSwipeableState(initialValue = "none")
    val coroutineScope = rememberCoroutineScope()

    GiphyBottomModalSheet(sheetSwipeableState, webSocket, userViewModel, coroutineScope, groupId = groupId) {
        GroupChatPageContent(navController, userViewModel, groupId, webSocket, toggleSwipeState =  {
            coroutineScope.launch {
                sheetSwipeableState.animateTo("half")
            }
        }, openSheet = openSheet)
    }

}


@Composable
fun GroupChatPageContent(navController: NavController, userViewModel: UserViewModel, groupId: Int , webSocket: WebSocketClient?, toggleSwipeState: () ->Unit, openSheet: (profile: Profile?) -> Unit) {

    var message by remember {
        mutableStateOf("")
    }
    val group = userViewModel.groups[groupId]

    Log.d("groups", userViewModel.groups.toString())
    Log.d("groups", groupId.toString())

    LaunchedEffect(key1 = groupId, block = {
        if ((webSocket as WebSocketClient).isClosed) {
            (webSocket as WebSocketClient).connect()
            webSocket.send("{\"group\": $groupId}")
            userViewModel.seenChatMessages(groupId = groupId)
        } else {
            webSocket.send("{\"group\": $groupId}")
            userViewModel.seenChatMessages(groupId = groupId)
        }
        })


//    val focusRequester = FocusRequester()
//
//
//    DisposableEffect(Unit) {
//        focusRequester.requestFocus()
//        onDispose { }
//    }



    Column() {


            TitleBar(navController = navController, title = group?.name ?: "", onTitleTap = {
                navController.navigate("groupPage/$groupId")
            }, spot = {
                if (group != null) {
                    GroupPicture(
                        group = group, size = 36, onImageClick = { navController.navigate("groupPage/$groupId") }
                    )
                }
            })

            Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        if (group != null) {
            GroupChatMessages(userViewModel = userViewModel, group = group, messages = userViewModel.groups[groupId]?.messages ?: listOf(), modifier = Modifier.weight(1F))
        }


            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

//                IconButton(onClick = {
//                    webSocket?.send("{\"group\": $groupId, \"message\": \"$message\"}")
//                    userViewModel.addPrivateMessage(
//                        message = ChatMessage(
//                            user_id = userViewModel.profile!!.user_id,
//                            message = message,
//                            group = groupId,
//                            seen = true
//                        )
//                    )
//                    message = ""
//
//                }, Modifier.clip(CircleShape)) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_image_2_line),
//                        contentDescription = "image",
//                        tint = MaterialTheme.colors.onBackground,
//                        modifier = Modifier
//                            .background(MaterialTheme.colors.background)
//                            .padding(10.dp)
//                            .size(28.dp)
//                    )
//
//                }



                IconButton(onClick = {
                    toggleSwipeState.invoke()

                }, Modifier.clip(CircleShape)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_file_gif_line),
                        contentDescription = "gif",
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .padding(10.dp)
                            .size(28.dp)
                    )

                }

                Spacer(modifier = Modifier.width(8.dp))


                TextField(
                    value = message,
                    onValueChange = { message = it }, Modifier.weight(1F),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = ExtendedTheme.colors.LightGray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    visualTransformation = VisualTransformation.None,
                    //modifier = Modifier.focusRequester(focusRequester),
                    placeholder = { Text("Message...") },
                    shape = RoundedCornerShape(28.dp),
                    singleLine = false,
                    keyboardActions = KeyboardActions(onSend = {
                        webSocket?.send("{\"group\": $groupId, \"message\": \"$message\"}")
                        userViewModel.addGroupMessage(
                            message = ChatMessage(
                                user_id = userViewModel.profile!!.user_id,
                                message = message,
                                group = groupId,
                            )
                        )
                        message = ""
                    }),

                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = {
                    webSocket?.send("{\"group\": $groupId, \"message\": \"$message\"}")
                    userViewModel.addGroupMessage(
                        message = ChatMessage(
                            user_id = userViewModel.profile!!.user_id,
                            message = message,
                            group = groupId,
                            seen = true
                        )
                    )
                    message = ""

                }, Modifier.clip(CircleShape)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send_plane_fill),
                        contentDescription = "send",
                        tint = MaterialTheme.colors.onPrimary,
                        modifier = Modifier
                            .background(MaterialTheme.colors.primary)
                            .padding(12.dp)
                            .size(24.dp)
                    )

            }
        }





    }

}

@Composable
fun GroupChatMessages (userViewModel: UserViewModel, group: Group, messages: List<ChatMessage>, modifier: Modifier = Modifier){
    val currentUserId: Int = userViewModel.profile?.user_id ?: -1
    val lazyListState = rememberLazyListState()


    val context = LocalContext.current

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    val size: Dp = (screenWidth * context.resources.displayMetrics.density*3/5)

    LazyColumn(state = lazyListState, reverseLayout = true, modifier = modifier) {

        for (index in messages.indices) {
            val previousMessage = messages.getOrNull(index + 1)
            val nextMessage = messages.getOrNull(index - 1)
            lateinit var shape: Shape


            when {
                previousMessage?.user_id ?: -1 != messages[index].user_id -> {
                    shape = if (messages[index].user_id == currentUserId) {
                        RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp, bottomEnd = 3.dp, bottomStart = 20.dp)

                    } else {
                        RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp, bottomEnd = 20.dp, bottomStart = 3.dp)

                    }
                }
                nextMessage?.user_id ?: -1 != messages[index].user_id -> {
                    shape = if (messages[index].user_id == currentUserId) {
                        RoundedCornerShape(topEnd = 3.dp, topStart = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp)

                    } else {
                        RoundedCornerShape(topEnd = 20.dp, topStart = 3.dp, bottomEnd = 20.dp, bottomStart = 20.dp)

                    }
                }
                else -> {
                    shape = if (messages[index].user_id == currentUserId) {
                        RoundedCornerShape(topEnd = 3.dp, topStart = 20.dp, bottomEnd = 3.dp, bottomStart = 20.dp)

                    } else {
                        RoundedCornerShape(topEnd = 20.dp, topStart = 3.dp, bottomEnd = 20.dp, bottomStart = 3.dp)

                    }
                }
            }

            item {



                Box(modifier = Modifier
                    .fillMaxWidth()) {


                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier
                        .padding(horizontal = 16.dp).align(
                            if (messages[index].user_id == currentUserId) {
                                Alignment.CenterEnd
                            } else {
                                Alignment.CenterStart

                            }
                        )) {


                        if (previousMessage?.user_id ?: -1 != messages[index].user_id && messages[index].user_id != currentUserId) {
                            ProfilePicture(
                                url = group.members.firstOrNull{ it.user_id == messages[index].user_id}?.profile_picture, size = 36
                            )
                            Spacer(Modifier.width(12.dp))

                        } else {
                            Spacer(Modifier.width(48.dp))

                        }

                        Column() {

                            if (previousMessage?.user_id ?: -1 != messages[index].user_id && messages[index].user_id != currentUserId) {
                                Text(
                                    group.members.firstOrNull { it.user_id == messages[index].user_id }?.name
                                        ?: "",
                                    style = MaterialTheme.typography.body2,
                                    color = ExtendedTheme.colors.Black60
                                )

                            }

                            messages[index].gif?.let {
                                val imageLoader = ImageLoader.Builder(context)
                                    .components {
                                        if (Build.VERSION.SDK_INT >= 28) {
                                            add(ImageDecoderDecoder.Factory())
                                        } else {
                                            add(GifDecoder.Factory())
                                        }
                                    }
                                    .build()
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current).data(data = it)
                                            .apply(block = fun ImageRequest.Builder.() {
                                                size(size.value.toInt())
                                                scale(Scale.FIT)
                                                precision(Precision.EXACT)
                                            }).build(), imageLoader = imageLoader
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(vertical = 2.dp)
                                        .width(screenWidth*3/5)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                            }

                            messages[index].image?.let {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(it)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = stringResource(R.string.content_profile_icon),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(vertical = 2.dp)
                                        .width(screenWidth*3/5)
                                        .clip(RoundedCornerShape(3.dp)))
                            }


                            messages[index].message?.let {

                                Text(
                                    text = it,
                                    color = if (messages[index].user_id == currentUserId) {
                                        MaterialTheme.colors.onPrimary
                                    } else {
                                        MaterialTheme.colors.onBackground
                                    },
                                    modifier = Modifier
                                        .padding(vertical = 2.dp)
                                        .widthIn(min = 40.dp, max = screenWidth*3/5)
                                        .background(
                                            if (messages[index].user_id == currentUserId) {
                                                MaterialTheme.colors.primary
                                            } else {
                                                ExtendedTheme.colors.LightGray
                                            },
                                            shape = shape
                                        )
                                        .padding(horizontal = 16.dp, vertical = 8.dp),

                                    )

                            }
                        }
                    }


                    }
                if (!isSameDay(nextMessage?.timestamp, messages[index].timestamp)) {
                    Text(
                        SimpleDateFormat("MMMM d").format(messages[index].timestamp),
                        color = ExtendedTheme.colors.Black60,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                }

            }
        }
    }


@Composable
fun LazyListState.OnBottomReached(
    loadMore : () -> Unit
){
    // ...
}

//LazyColumn(modifier = Modifier, state = lazyListState) {
//    userViewModel.friends?.get(userId)?.messages?.forEach{ message ->
////                    if (index != 0 && index == userViewModel.friends?.get(userId)?.messages?.count()) {
////                        var previousMessage = friend?.messages?[index-1]
////                        var nextMessage = friend?.messages?[index+1]
////                    }
//        Text(text = message.message)
////                    TextStyle(color = , fontSize = 16, fontFamily = nunitoFamily)
//    }
//}
