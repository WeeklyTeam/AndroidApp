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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameDay
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.login.coloredShadow
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlotCalendarItem(plot: Plot, navController: NavController, userViewModel: UserViewModel, onClick: () -> Unit) {

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

                IconButton(onClick = { navController.navigate("groupChatPage/${plot.group_id}") }, modifier = Modifier.clip(
                    CircleShape).background(ExtendedTheme.colors.LightGray).size(48.dp)) {
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
                }, modifier = Modifier.clip(
                    CircleShape).background(ExtendedTheme.colors.LightGray).size(48.dp)) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_chat_3_line),
                        contentDescription = null,
                    )
                }

            }




        }

    }
}