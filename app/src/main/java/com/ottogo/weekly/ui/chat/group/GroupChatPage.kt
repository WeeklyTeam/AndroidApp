package com.ottogo.weekly.ui.chat.group

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.*
import org.java_websocket.client.WebSocketClient


@Composable
fun GroupChatPage(navController: NavController, userViewModel: UserViewModel, groupId: Int , webSocket: WebSocketClient?) {

    var message by remember {
        mutableStateOf("")
    }
    val group = userViewModel.groups[groupId]

    Log.d("groups", userViewModel.groups.toString())
    Log.d("groups", groupId.toString())




    Column() {


            TitleBar(navController = navController, title = group?.name ?: "")

            Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

            GroupChatMessages(userViewModel = userViewModel, messages = userViewModel.groups[groupId]?.messages ?: listOf(), modifier = Modifier.weight(1F))


            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
                    webSocket?.send("{\"group\": $groupId, \"message\": \"$message\"}")
                    userViewModel.addGroupMessage(
                        message = ChatMessage(
                            user_id = userViewModel.profile!!.user_id,
                            message = message,
                            group = groupId,
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
fun GroupChatMessages (userViewModel: UserViewModel, messages: List<ChatMessage>, modifier: Modifier = Modifier){
    val currentUserId: Int = userViewModel.profile?.user_id ?: -1
    val lazyListState = rememberLazyListState()

    LazyColumn(state = lazyListState, reverseLayout = true, modifier = modifier) {

        for (index in messages.indices) {
            val previousMessage = messages.getOrNull(index + 1)
            val nextMessage = messages.getOrNull(index - 1)
            lateinit var shape: Shape
            Log.d("gc",messages[index].toString())


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
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {



                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier
                        .align(
                            if (messages[index].user_id == currentUserId) {
                                Alignment.CenterEnd
                            } else {
                                Alignment.CenterStart

                            }
                        )) {


                        if (previousMessage?.user_id ?:-1 != messages[index].user_id && messages[index].user_id != currentUserId){
                            ProfilePicture(url = userViewModel.friends[messages[index].user_id]?.profile_picture, size = 36)
                            Spacer(Modifier.width(12.dp))

                        } else {
                            Spacer(Modifier.width(48.dp))
                        }

                        Column() {

                            if (previousMessage?.user_id ?: -1 != messages[index].user_id && messages[index].user_id != currentUserId){
                                Text(userViewModel.friends[messages[index].user_id]?.name ?: "", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black40)

                            }


                            Text(text = messages[index].message?:"",
                                color = if (messages[index].user_id == currentUserId) { MaterialTheme.colors.onPrimary } else { MaterialTheme.colors.onBackground },
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .background(
                                        if (messages[index].user_id == currentUserId) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            ExtendedTheme.colors.LightGray
                                        },
                                        shape = shape
                                    )
                                    .padding(horizontal = 16.dp, 8.dp)

                                )
                        }

                    }
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
