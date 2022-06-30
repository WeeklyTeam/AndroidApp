package com.ottogo.weekly.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.Black40
import com.ottogo.weekly.viewmodels.UserViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.theme.*

@Composable
fun ChatPage(navController: NavController, userViewModel: UserViewModel) {
    Column() {
        ChatTitleBar(navController = navController)
    }
}

@Composable
fun ChatTitleBar(navController: NavController){
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Chat", Modifier.padding(top = 24.dp, bottom = 8.dp), style = MaterialTheme.typography.h1)


        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_add_circle_line),
                    contentDescription = null,
                )
            }
            IconButton(onClick = { navController.navigate("searchPage") }) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_search_line),
                    contentDescription = null,
                )
            }
        }


    }
}