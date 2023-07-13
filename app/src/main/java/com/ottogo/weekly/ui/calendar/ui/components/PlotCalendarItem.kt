package com.ottogo.weekly.ui.calendar.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameDay
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.login.coloredShadow
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.viewmodels.UserViewModel
//import kotlinx.coroutines.NonCancellable.message
import org.java_websocket.client.WebSocketClient
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlotCalendarItem(plot: Plot, navController: NavController, userViewModel: UserViewModel, webSocket: WebSocketClient?, onClick: () -> Unit) {

    val time = if (plot.starttime != null){
        SimpleDateFormat("h:mma").format(plot.starttime) + if (plot.endtime != null) { " - " + SimpleDateFormat("h:mma").format(plot.endtime) } else {""}

    } else {
        "Date undecided"
    }


    Column(modifier = Modifier
        .clickable { onClick() }
        .fillMaxWidth()) {


        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

            EmojiCircle(emoji = plot.emoji)

            Spacer(modifier = Modifier.width(16.dp))

            Column() {
                Text(plot.name, style = MaterialTheme.typography.h3)
                Spacer(modifier = Modifier.height(8.dp))
                Text(time, style = MaterialTheme.typography.body2,
                    color = ExtendedTheme.colors.Black60)
            }
            Spacer(modifier = Modifier.weight(1F))

            if (plot.group_id != null) {

                IconButton(onClick = { navController.navigate("groupChatPage/${plot.group_id}") }, modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(ExtendedTheme.colors.LightGray)
                    .size(48.dp)) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_chat_3_line),
                        contentDescription = null,
                    )
                }
            } else if (plot.relationship_id != null){
                IconButton(onClick = {
                    val chat = userViewModel.friends.filterValues { it.relationship_id == plot.relationship_id }.keys.first()
                    Log.d("friends", chat.toString())
                    navController.navigate("privateChatPage/${chat}")
                }, modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(ExtendedTheme.colors.LightGray)
                    .size(48.dp)) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_chat_3_line),
                        contentDescription = null,
                    )
                }

            }
        }

        val calendar = Calendar.getInstance()

        // Get 30 minutes after time
        calendar.time = plot.starttime
        calendar.add(Calendar.MINUTE, 30)
        val after30Min = calendar.time

        // Get 30 minutes before time
        calendar.time = plot.starttime
        calendar.add(Calendar.MINUTE, -30)
        val before30Min = calendar.time

        val currentDate = Calendar.getInstance().time
        if (currentDate.after(before30Min) && currentDate.before(after30Min)) {
            Log.d("status", plot.relationship_id.toString())
            if (plot.relationship_id != null) {
                Log.d("status", "Finding user")
                var recipientId = -1
                for ((userId, user) in userViewModel.friends) {
                    if (user.relationship_id == plot.relationship_id) {
                        recipientId = userId
                        Log.d("status", "Found user: " + recipientId.toString())
                    }
                }



                if (recipientId != -1) {

                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        CustomButton(buttonText = "Running late", modifier = Modifier.weight(1f).padding(end=16.dp),
                            backgroundColor = LightGray,
                            textColor = Black,
                            onClick = {
                                webSocket?.send("{\"recipient\": $recipientId, \"message\": \"Running late\"}")
                                userViewModel.addPrivateMessage(
                                    message = ChatMessage(
                                        user_id = userViewModel.profile!!.user_id,
                                        message = "Running late",
                                        recipient = recipientId,
                                        seen = true
                                    )
                                )
                        })

                        CustomButton(buttonText = "I'm here", modifier = Modifier.weight(1f),
                            onClick = {
                                webSocket?.send("{\"recipient\": $recipientId, \"message\": \"I'm here\"}")
                                userViewModel.addPrivateMessage(
                                    message = ChatMessage(
                                        user_id = userViewModel.profile!!.user_id,
                                        message = "I'm here",
                                        recipient = recipientId,
                                        seen = true
                                    )
                                )
                        })
                    }

                }
            }
            else if (plot.group_id != null) {

                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    CustomButton(buttonText = "Running late", modifier = Modifier.weight(1f).padding(end=16.dp),
                        backgroundColor = LightGray,
                        textColor = Black,
                        onClick = {
                            webSocket?.send("{\"group\": ${plot.group_id}, \"message\": \"Running late\"}")
                            userViewModel.addGroupMessage(
                                message = ChatMessage(
                                    user_id = userViewModel.profile!!.user_id,
                                    message = "Running late",
                                    group = plot.group_id,
                                    seen = true
                                )
                            )
                    })

                    CustomButton(buttonText = "I'm here", modifier = Modifier.weight(1f),
                        onClick = {
                            webSocket?.send("{\"group\": ${plot.group_id}, \"message\": \"I'm here\"}")
                            userViewModel.addGroupMessage(
                                message = ChatMessage(
                                    user_id = userViewModel.profile!!.user_id,
                                    message = "I'm here",
                                    group = plot.group_id,
                                    seen = true
                                )
                            )
                    })
                }
            }

        }
    }
}