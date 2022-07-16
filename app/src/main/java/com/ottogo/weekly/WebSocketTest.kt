package com.ottogo.weekly

import androidx.compose.material.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ottogo.weekly.ui.chat.ChatPage
import com.ottogo.weekly.viewmodels.UserViewModel
import org.java_websocket.client.WebSocketClient

@Composable
fun WebSocketTest(navController: NavController,  userViewModel: UserViewModel, webSocket: WebSocketClient?) {
    Column() {
        Button(onClick = {
            webSocket?.send("{\"recipients\": ${listOf(userViewModel.profile?.user_id)}, \"group_invitation\": ${true}, \"name\": \"${"new_group"}\", \"profile_picture\": \"${"http://www.lunammk.com/wp-content/uploads/2021/02/tequila-choices2-2021-768x2918.jpg"}\"}")
        }) {
            Text("Send")
        }

        ChatPage(navController = navController, userViewModel = userViewModel)
    }
}