package com.ottogo.weekly.ui.chat

import android.os.Build
import android.util.Log
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.*
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.java_websocket.client.WebSocketClient

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PrivateChatPage(navController: NavController, userViewModel: UserViewModel, userId: Int, webSocket: WebSocketClient?) {

    Giphy.configure(LocalContext.current, "OGYiQs1RQKTbdR0jAGA0RyqkWD5GEY0z")

    var giphySheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    var test by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = giphySheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetPeekHeight = 0.dp,
        sheetContent = {

            Box(modifier= Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clickable {
                    coroutineScope.launch {
                        giphySheetState.bottomSheetState.collapse()
                    }
                })
            GiphyView() {
                coroutineScope.launch {
                    giphySheetState.bottomSheetState.collapse()
                }
            }
        },
        sheetGesturesEnabled = false) {

        PrivateChatPageContent(navController, userViewModel, userId, webSocket, giphySheetState, coroutineScope)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PrivateChatPageContent(navController: NavController, userViewModel: UserViewModel, userId: Int, webSocket: WebSocketClient?, giphySheetState: BottomSheetScaffoldState, coroutineScope: CoroutineScope) {
    var message by remember {
        mutableStateOf("")
    }
    val friend = userViewModel.friends[userId]

    Column() {
        Log.d("recomp", "recomp1")


        TitleBar(navController = navController, title = friend?.name ?: "")

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        ChatMessages(messages = userViewModel.friends[userId]?.messages ?: listOf(), userId = userId, currentUserId = userViewModel.profile!!.user_id, modifier = Modifier.weight(1F))

//        Text(userViewModel.friends[userId]?.messages.toString())

        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = {
                coroutineScope.launch {
                    if (giphySheetState.bottomSheetState.isCollapsed) {
                        giphySheetState.bottomSheetState.expand()
                    } else {
                        giphySheetState.bottomSheetState.collapse()
                    }
                }
            }){
                Icon(
                    painter = painterResource(id = R.drawable.ic_file_gif_line),
                    contentDescription = "gif",
                    modifier = Modifier.height(24.dp)
                )
            }

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
                placeholder = { Text("Message...") },
                shape = RoundedCornerShape(28.dp),
                singleLine = false,
                keyboardActions = KeyboardActions(onSend = {

                }),
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = {
                webSocket?.send("{\"recipient\": $userId, \"message\": \"$message\"}")
                userViewModel.addPrivateMessage(
                    message = ChatMessage(
                        user_id = userViewModel.profile!!.user_id,
                        message = message,
                        recipient = userId
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GiphyView(
    toggleSheet: () -> Unit
) {
        var close by remember { mutableStateOf(false) }
        if (close) {
            toggleSheet.invoke()
            close = false
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize().clickable { toggleSheet.invoke() },
            factory = { context ->
                val gridView = GiphyGridView(context)

                gridView.content = GPHContent.trendingGifs
                gridView.callback = object : GPHGridCallback {
                    override fun contentDidUpdate(resultCount: Int) {
                        Log.d("status", "test")
                    }

                    override fun didSelectMedia(media: Media) {
                        Log.d("status", "${media.bitlyGifUrl}")
                        close = true
                    }
                }

                gridView.apply {}
            }
        )

}

@Composable
fun ChatMessages (messages: List<ChatMessage>, userId: Int, currentUserId: Int, modifier: Modifier = Modifier){
    val lazyListState = rememberLazyListState()

    LazyColumn(state = lazyListState, reverseLayout = true, modifier = modifier) {
        Log.d("recomp", "recomp2")

        for (index in messages.indices) {
            val previousMessage = messages.getOrNull(index - 1)
            val nextMessage = messages.getOrNull(index + 1)
            lateinit var shape: Shape

            if (previousMessage?.user_id ?: -1 != messages[index].user_id){
                shape = if (messages[index].user_id == currentUserId) {
                    RoundedCornerShape(topEnd = 3.dp, topStart = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp)

                } else {
                    RoundedCornerShape(topEnd = 20.dp, topStart = 3.dp, bottomEnd = 20.dp, bottomStart = 20.dp)

                }
            } else if (nextMessage?.user_id ?: -1 != messages[index].user_id) {
                shape = if (messages[index].user_id == currentUserId) {
                    RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp, bottomEnd = 3.dp, bottomStart = 20.dp)

                } else {
                    RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp, bottomEnd = 20.dp, bottomStart = 3.dp)

                }
            } else {
                shape = if (messages[index].user_id == currentUserId) {
                    RoundedCornerShape(topEnd = 3.dp, topStart = 20.dp, bottomEnd = 3.dp, bottomStart = 20.dp)

                } else {
                    RoundedCornerShape(topEnd = 20.dp, topStart = 3.dp, bottomEnd = 20.dp, bottomStart = 3.dp)

                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = messages[index].message,
                        color = if (messages[index].user_id == currentUserId) {
                            MaterialTheme.colors.onPrimary
                        } else {
                            MaterialTheme.colors.onBackground
                        },
                        modifier = Modifier
                            .align(
                                if (messages[index].user_id == currentUserId) {
                                    Alignment.CenterEnd
                                } else {
                                    Alignment.CenterStart
                                }
                            )
                            .padding(vertical = 2.dp, horizontal = 16.dp)
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
