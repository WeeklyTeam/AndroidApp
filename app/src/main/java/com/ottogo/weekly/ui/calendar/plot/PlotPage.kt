package com.ottogo.weekly.ui.calendar.plot


import android.annotation.SuppressLint
import android.util.Log
import com.ottogo.weekly.ui.theme.LightGray
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.calendar.ui.components.PlotMemberList
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat


@Composable
fun PlotPage(navController: NavController, plotId: Int, userViewModel: UserViewModel) {
    val plot = userViewModel.plots.firstOrNull(){ it.id == plotId }

    if (plot != null) {
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            TitleBar(navController = navController, title = "", iconButtons = {
                IconButton(onClick = { navController.navigate("plotEditPage/$plotId") }, modifier = Modifier.size(56.dp)) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_edit_2_line),
                        contentDescription = null,
                    )
                }
            })

            PlotInfo(plot = plot)
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
            CustomButton(buttonText = "Chat",
                modifier = Modifier.padding(horizontal = 125.dp),
                onClick = {
                    navController.navigate("groupChatPage/${plot.group_id}")
                },)
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
            Column(modifier = Modifier
                .weight(1F)
                .verticalScroll(rememberScrollState())) {
                PlotMemberList(title = "Going", members = plot.going.asIterable())
            }

            Column(horizontalAlignment = Alignment.Start) {
                if ( plot.is_going) {
                    RainCheckBtn(plot, userViewModel, navController)
                }
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun PlotInfo(plot: Plot) {
    val date = plot.starttime?.let { SimpleDateFormat("EEEE, MMM dd").format(it) }
    val time = plot.starttime?.let { SimpleDateFormat("h:mm a").format(it) }

    EmojiCircle(emoji = plot.emoji, Modifier.size(72.dp))
    Spacer(modifier = Modifier.padding(bottom = 16.dp))
    Text(text = plot.name, style = MaterialTheme.typography.h1)
    Spacer(modifier = Modifier.padding(bottom = 8.dp))
    Text(text = "$date at $time", style = MaterialTheme.typography.h5)
}

@Composable
fun RainCheckBtn(plot: Plot, userViewModel: UserViewModel, navController: NavController) {
    Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)
    Text(text = "Can't make it?",
        style = MaterialTheme.typography.body1,
        modifier = Modifier.padding(start = 31.dp))
    Spacer(modifier = Modifier.padding(bottom = 12.dp))
    CustomButton(buttonText = "Rain Check",
        modifier = Modifier.padding(horizontal = 16.dp),
        backgroundColor = LightGray,
        textColor = MaterialTheme.colors.primary,
        onClick = {
            userViewModel.rejectPlotInvite(plot)
            navController.navigateUp()
        })
}