package com.ottogo.weekly.ui.calendar.plot


import com.ottogo.weekly.ui.theme.Black80
import com.ottogo.weekly.ui.theme.LightGray
import androidx.compose.foundation.layout.*
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


@Composable
fun PlotPage(navController: NavController, plotId: Int, userViewModel: UserViewModel) {
    val plot = userViewModel.plots?.first { it.id == plotId }

    Column (modifier = Modifier.padding(top=44.dp, bottom = 16.dp)) {
        TitleBar(navController = navController, title = "", iconButtons = {
            IconButton(onClick = { navController.navigate("plotEditPage") }, modifier = Modifier.size(56.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_edit_2_line),
                    contentDescription = null,
                )
            }
        })

        if (plot != null) {
            PlotInfo(plot = plot)
            Spacer(modifier = Modifier.padding(bottom = 24.dp))
            CustomButton(buttonText = "Chat",
                    modifier = Modifier.padding(horizontal = 125.dp),
                    onClick = {},)
            PlotMemberList(title = "Going", members = plot.going.asIterable(), 24, 32)
            if (plot.is_going) {
                RainCheckBtn()
            }
        }
    }
}


@Composable
fun RainCheckBtn() {
    Column(
        modifier = Modifier.fillMaxHeight().padding(bottom = 33.dp, top = 33.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)
        Text(text = "Can't make it?",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(start = 31.dp))
        Spacer(modifier = Modifier.padding(bottom = 12.dp))
        CustomButton(buttonText = "Rain Check",
            modifier = Modifier.padding(horizontal = 16.dp),
            foregroundColor = Black80,
            backgroundColor = LightGray,
            onClick = {})
    }
}


@Composable
fun PlotInfo(plot: Plot) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmojiCircle(emoji = plot.emoji, Modifier.size(72.dp))
        Spacer(modifier = Modifier.padding(bottom = 16.dp))
        Text(text = plot.name, style = MaterialTheme.typography.h1)
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        Text(text = plot.starttime.toString(), style = MaterialTheme.typography.body2)
    }
}