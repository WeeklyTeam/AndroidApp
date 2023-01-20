package com.ottogo.weekly.ui.calendar.plot


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.ottogo.weekly.ui.theme.Black80
import com.ottogo.weekly.ui.theme.LightGray
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.calendar.ui.components.PlotMemberList
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import java.text.SimpleDateFormat


@Composable
fun PlotPage(navController: NavController, plotId: Int, userViewModel: UserViewModel, openSheet: (profile: Profile?) -> Unit) {
    val plot = if (userViewModel.plots.firstOrNull { it.id == plotId } != null) {userViewModel.plots.firstOrNull { it.id == plotId }} else {userViewModel.recommendations.firstOrNull { it.id == plotId }}


    print(userViewModel.plots.toString())

    Column () {
        TitleBar(navController = navController, title = "", iconButtons = {
            IconButton(onClick = { navController.navigate("plotEditPage/$plotId") }, modifier = Modifier.size(56.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_edit_2_line),
                    contentDescription = null,
                )
            }
        })

        if (plot != null) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
                PlotInfo(navController = navController, plot = plot)
                Spacer(modifier = Modifier.padding(bottom = 24.dp))
                Row() {
                    if (plot.group_id != null) {
                        CustomButton(
                            buttonText = "Chat",
                            modifier = Modifier.width(125.dp),
                            onClick = {navController.navigate("groupChatPage/${plot.group_id}")},
                        )
                        Spacer(Modifier.height(16.dp))
                    } else if (plot.relationship_id != null){
                        CustomButton(
                            buttonText = "Chat",
                            modifier = Modifier.width(125.dp),
                            onClick = {
                                val chat = userViewModel.friends.filterValues { it.relationship_id == plot.relationship_id }.keys.first()
                                Log.d("friends", chat.toString())
                                navController.navigate("privateChatPage/${chat}")},
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(onClick = {
                        navController.navigate("addPlotMembersPage/${plotId}")
                    }, Modifier.clip(CircleShape)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user_add_line),
                            contentDescription = "invite",
                            tint = MaterialTheme.colors.onBackground,
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .padding(10.dp)
                                .size(28.dp)
                        )

                    }
                }

                if (!plot.url.isNullOrEmpty()){
                    Spacer(Modifier.height(24.dp))
                    Text(text = plot.url.toString(),
                        style = TextStyle(textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp, color = Color(0xFF2D5BFF)
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth().clickable{ navController.navigate("webviewPage/Weekly?url=${plot.url}")}, textAlign = TextAlign.Start)
                }

                if (!plot.description.isNullOrEmpty()){
                    Spacer(Modifier.height(24.dp))
                    Text(text = plot.description,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(), textAlign = TextAlign.Start)
                }

                if (plot.going.count() > 0) {
                    PlotMemberList(title = "Going", members = plot.going, openSheet = openSheet)
                }
                if (plot.invited.count() > 0) {
                    PlotMemberList(title = "Invited", members = plot.invited, openSheet = openSheet)
                }
                if (plot.not_going.count() > 0) {
                    PlotMemberList(title = "Not going", members = plot.not_going, openSheet = openSheet)
                }
                if (plot.is_going) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Can't make it?",
                        style = MaterialTheme.typography.body2,
                        color = ExtendedTheme.colors.Black60,
                        modifier = Modifier
                            .padding(start = 32.dp)
                            .fillMaxWidth(), textAlign = TextAlign.Start)
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomButton(buttonText = "Rain Check",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textColor = ExtendedTheme.colors.Black60,
                        backgroundColor = LightGray,
                        onClick = {
                            userViewModel.rejectPlotInvite(plot = plot)
                            navController.navigateUp()
                        })
                }
                Spacer(modifier = Modifier.height(48.dp))
            }

        }
    }
}



@Composable
fun PlotInfo(navController: NavController, plot: Plot) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmojiCircle(emoji = plot.emoji, Modifier.size(72.dp), onClick = {navController.navigate("plotEditPage/${plot.id}")})
        Spacer(modifier = Modifier.padding(bottom = 16.dp))
        Text(text = plot.name, style = MaterialTheme.typography.h1, modifier = Modifier.clickable{navController.navigate("plotEditPage/${plot.id}")})
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        Text(text = if (plot.starttime != null) {SimpleDateFormat("EEEE, MMM d").format(plot.starttime) + " at " + SimpleDateFormat("h:mm a").format(plot.starttime)} else { "Date undecided" }, style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60, modifier = Modifier.clickable{navController.navigate("plotEditPage/${plot.id}")})




    }
}