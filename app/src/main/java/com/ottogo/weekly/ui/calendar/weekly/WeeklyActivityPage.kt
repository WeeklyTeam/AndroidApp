package com.ottogo.weekly.ui.calendar.weekly

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.ottogo.weekly.R
import com.ottogo.weekly.api.Activity
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun WeeklyActivityPage(navController: NavController, availability: String, userViewModel: UserViewModel) {
    val selected = remember{
        mutableStateListOf<Int>()
    }

    var usePreferences by remember{
        mutableStateOf(true)
    }

    var showCustomActivity by remember{
        mutableStateOf(false)
    }

    var customActivityEmoji by remember{
        mutableStateOf("\uD83C\uDF42")
    }

    var customActivity by remember{
        mutableStateOf("")
    }

    var activities = remember{ mutableStateListOf<Activity>() }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = 1, block = {
        activities.addAll(WeeklyApi.retrofitService.getWeeklyActivities(mapOf("Authorization" to "token ${userViewModel.token}")))
        Log.d("activities", activities.toString())
    })

    LaunchedEffect(key1 = customActivity){
        if(customActivity.contains("[^A-Za-z0-9 .?!()\"]".toRegex())){
            customActivityEmoji = customActivity.replace("[A-Za-z0-9 .?!()\"]".toRegex(), "")
        }
    }

    var selectedList = remember { mutableStateListOf<Int>() }
    Column {

        if(showCustomActivity){
            Dialog(
                onDismissRequest = {
                    showCustomActivity = false

                },
                content = {
                    Column(modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colors.background)
                        .padding(24.dp)) {
                        EmojiCircle(emoji = customActivityEmoji, onClick = {
                            showCustomActivity = false
                        })
                        Spacer(modifier = Modifier.height(24.dp))


                        CustomTextField(
                            helper = "Activity",
                            hint = "What do you want to do?",
                            input = customActivity.replace("[^A-Za-z0-9 .?!()\"]".toRegex(), ""),
                            onChange = {
                                customActivity = it
                            },
                            modifier = Modifier.focusRequester(focusRequester),
                            keyboardActions = KeyboardActions(onNext = {
                                showCustomActivity = false
                            }),

                            )
                        Spacer(modifier = Modifier.height(24.dp))



                        CustomButton(buttonText = "Add") {
                            activities.add(Activity(id = -1, emojis=customActivityEmoji, activity = customActivity))
                            selected.add(activities.size-1)
                            showCustomActivity = false
                        }




                    }
                },

                )
        }

        TitleBar(navController = navController, title = "Weekly")
        Divider(color = ExtendedTheme.colors.LightGray, )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {


            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "What do you want to do?",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h3,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))


            val numRows = if ((activities.size+1) % 2 == 0) {
                (activities.size+1) / 2
            } else {
                (activities.size+1) / 2 + 1
            }

            for (row in 0 until numRows) {
                Log.d("activities", numRows.toString())

                val firstIndex = row * 2
                var secondIndex = firstIndex + 1

                if (secondIndex >= activities.size) {
                    secondIndex = -1
                }
                Row() {
                    if (firstIndex < activities.size) {
                        WeeklyActivityItem(
                            activity = activities[firstIndex],
                            selected = selected.contains(firstIndex),
                            onClick = { if(selected.contains(firstIndex)) { selected.remove(firstIndex) } else { selected.add(firstIndex) } },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                        )
                    } else {
                        WeeklyActivityItem(
                            activity = Activity(-1, "+", "Custom"),
                            selected = false,
                            onClick = { showCustomActivity = true },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }

                    if (secondIndex != -1) {
                        WeeklyActivityItem(activity = activities[secondIndex], selected = selected.contains(secondIndex), onClick =  {if(selected.contains(secondIndex)) { selected.remove(secondIndex) } else { selected.add(secondIndex) } }, modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp))
                    }
                    else if (firstIndex == activities.size-1) {
                        WeeklyActivityItem(
                            activity = Activity(-1, "+", "Custom"),
                            selected = false,
                            onClick = { showCustomActivity = true },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }


            }
            Spacer(modifier = Modifier.height(24.dp))


            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { usePreferences = !usePreferences }) {
                Icon(
                    painter = painterResource(id = if (usePreferences){ R.drawable.ic_checkbox_circle_fill } else { R.drawable.ic_checkbox_blank_circle_line }),
                    tint = if (usePreferences){ MaterialTheme.colors.primary } else { ExtendedTheme.colors.Black60 },
                    contentDescription = "checkbox",
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    "Use my favorites",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Update my favorites",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("activitiesPage")
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))
            CustomButton(
                buttonText = "Next",
                onClick = {
                    WeeklyApi.retrofitService.postWeekly(header = mapOf("Authorization" to "token ${userViewModel.token}"),
                    body = mapOf<String, Any>("user" to userViewModel.profile?.user_id!!, "availability" to availability, "activities" to activities.mapIndexed { index, it ->
                            if (selected.contains(index)){
                                "Y" + it.activity.lowercase()
                            } else {
                                "N" + it.activity.lowercase()
                            }
                        }.joinToString(separator = ",") + if (usePreferences) { ",Ypreferences" } else { "" }))
                    navController.navigate("weeklyCompletePage")
                          },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))


        }
    }

}

@Composable
fun WeeklyActivityItem(activity: Activity, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier){
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier =
        modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            }
            .background(
                if (selected) {
                    ExtendedTheme.colors.LightGreen
                } else {
                    MaterialTheme.colors.background
                }
            )
            .border(
                width = 1.dp,
                color = if (selected) {
                    ExtendedTheme.colors.DarkGreen
                } else {
                    ExtendedTheme.colors.LightGray
                },
                shape = RoundedCornerShape(12.dp)

            )
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {

        Text(
            text = activity.emojis, style = MaterialTheme.typography.body1, textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = activity.activity, style = MaterialTheme.typography.body1, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}


