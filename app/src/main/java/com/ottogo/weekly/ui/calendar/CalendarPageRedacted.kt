package com.ottogo.weekly.ui.calendar2

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Availability
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.ui.calendar.availability.beginningOfDay
import com.ottogo.weekly.ui.calendar.ui.components.AvailabilityText
import com.ottogo.weekly.ui.calendar.ui.components.PlotItem
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*

//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun CalendarPage(navController: NavController, userViewModel: UserViewModel) {
//
//    var displayMonth by rememberSaveable{
//        mutableStateOf(initialMonth())
//    }
//
//    var selectedCalendar by rememberSaveable {
//        mutableStateOf(-1)
//    }
//
//    var selectedDate: Date? by rememberSaveable {
//        mutableStateOf(null)
//    }
//
//    val coroutine = rememberCoroutineScope()
//
//    LaunchedEffect(key1 = selectedCalendar, block = {
//
//        if (selectedCalendar != -1) {
//            val calendar = userViewModel.calendars[selectedCalendar]
//            if (calendar.availabilities.isNullOrEmpty()) {
//                var users = ""
//
//
//                if (calendar.group_id != null) {
//                    users = (userViewModel.groups[calendar.group_id]?.members?.filter { it.user_id != userViewModel.profile?.user_id }
//                        ?.map { it.user_id }
//                        ?: listOf()).joinToString(separator = ",")
//                } else if (calendar.relationship_id != null) {
//                    users = listOf(calendar.user_id).toString()
//                }
//                try {
//
//                    val newAvailability = WeeklyApi.retrofitService.getAvailabilities(
//                        mapOf("Authorization" to "token ${userViewModel.token}"),
//                        users
//                    ).toMutableList()
//                    userViewModel.addCalendarAvailabilities(
//                        selectedCalendar, newAvailability
//                    )
//                    Log.d("availabilities", users)
//                } catch (e: HttpException) {
//                    Log.d("Exception", e.toString())
//
//                }
//            }
//        }
//    })
//
//
//
//    Column() {
//        CalendarTitleBar(navController = navController, date = displayMonth, nextMonth = { displayMonth = it }, previousMonth = { displayMonth = it }, userViewModel = userViewModel)
//
//        Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
//
//
//            CalendarComponent(displayMonth,
//                modifier = Modifier
//                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
//                selectedDate = selectedDate,
//                selectDate = { selectedDate = it },
//                plots = userViewModel.plots.filter {
//                        if (it.starttime != null) {
//                            it.starttime >= displayMonth && it.starttime < addMonth(displayMonth, 1)
//                        } else {
//                            false
//                        }
//                }.filter {
//                         if (selectedCalendar == -1){
//                             true
//                         } else {
//                             if (userViewModel.calendars[selectedCalendar].group_id != null) {
//                                 userViewModel.calendars[selectedCalendar].group_id == it.group_id
//                             }
//                             else if (userViewModel.calendars[selectedCalendar].relationship_id != null) {
//                                 userViewModel.calendars[selectedCalendar].relationship_id == it.relationship_id
//                             } else {
//                                 false
//                             }
//                         }
//                }, availability = if (selectedCalendar == -1) {
//                    userViewModel.availability.filter {
//                        if (it.starttime != null) {
//                            it.starttime >= displayMonth && it.starttime < addMonth(displayMonth, 1)
//                        } else {
//                            false
//                        }
//                    }} else {
//                        userViewModel.calendars[selectedCalendar].availabilities?.plus(userViewModel.availability)
//                            ?: listOf()
//                    }
//            )
//
//
//            if (userViewModel.friends.count() > 0 || userViewModel.groups.count() > 0) {
//                Row(
//                    modifier = Modifier
//                        .horizontalScroll(rememberScrollState())
//                        .padding(8.dp)
//                ) {
//                    Chip("Mine", isSelected = selectedCalendar == -1, modifier = Modifier
//                        .padding(horizontal = 8.dp)
//                        .clip(
//                            RoundedCornerShape(20.dp)
//                        )
//                        .clickable { selectedCalendar = -1 })
//
//                    Chip("Create", icon = R.drawable.ic_calendar_line, modifier = Modifier
//                        .padding(horizontal = 8.dp)
//                        .clip(
//                            RoundedCornerShape(20.dp)
//                        )
//                        .clickable {
//                            navController.navigate("createCalendarPage")
//                        })
//
//                    userViewModel.calendars?.forEachIndexed { index, calendar ->
//                        Chip(calendar.name,
//                            isSelected = selectedCalendar == index,
//                            modifier = Modifier
//                                .padding(horizontal = 8.dp)
//                                .clip(
//                                    RoundedCornerShape(20.dp)
//                                )
//                                .clickable { selectedCalendar = index })
//
//                    }
//
//
//                }
//            }
//
//
//
//            if (userViewModel.plots.count() ?: 0 > 0) {
//
//
//
//                userViewModel.plots.filter {
//                    if (it.starttime != null) {
//                        it.starttime >= displayMonth && it.starttime < addMonth(displayMonth, 1)
//                    } else {
//                        true
//                    }
//                }
//                    .filter {
//                        if (selectedDate != null && it.starttime != null) {
//                            it.starttime >= selectedDate && it.starttime < addDay(
//                                selectedDate!!, 1
//                            )
//                        } else {
//                            selectedDate == null
//                        }
//                    }
//                    .filter {
//                        if (selectedCalendar != -1) {
//                            when {
//                                it.relationship_id != null -> {
//                                    it.relationship_id == userViewModel.calendars?.get(selectedCalendar)?.relationship_id
//                                }
//                                it.group_id != null -> {
//                                    it.group_id == userViewModel.calendars?.get(selectedCalendar)?.group_id
//                                }
//                                else -> {
//                                    false
//                                }
//                            }
//                        } else {
//                            true
//                        }
//                    }.forEach { plot ->
//                        PlotItem(plot, onClick = {
//                            navController.navigate("plotPage/${plot.id}")
//                        }, userViewModel = userViewModel)
//
//                    }
//            } else {
//                Text(text = "Make your first plan \uD83C\uDF89", style = MaterialTheme.typography.h2, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 48.dp, bottom = 24.dp))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text("Tap ", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)
//                    Icon(painter = painterResource(id = R.drawable.ic_add_line), contentDescription = "search", tint = ExtendedTheme.colors.Black60, modifier = Modifier.size(15.dp))
//                    Text(" to begin", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)
//
//                }
//            }
//
//            if (selectedDate != null) {
//
//                val busyToday = userViewModel.availability.firstOrNull {
//                    it.starttime == selectedDate && it.endtime == addDay(
//                        selectedDate!!,
//                        1
//                    )
//                }
//                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
//                    .clickable {
//                        runBlocking {
//                            if (busyToday == null) {
//                                userViewModel.addAvailability(
//                                    WeeklyApi.retrofitService.addAvailability(
//                                        mapOf("Authorization" to "token ${userViewModel.token}"),
//                                        mapOf(
//                                            "busy" to true,
//                                            "starttime" to selectedDate!!,
//                                            "endtime" to addDay(selectedDate!!, 1),
//                                            "title" to "",
//                                        )
//                                    )
//                                )
//                            } else {
//                                WeeklyApi.retrofitService.removeAvailability(
//                                    mapOf("Authorization" to "token ${userViewModel.token}"),
//                                    busyToday.id
//                                )
//                                userViewModel.removeAvailability(busyToday)
//                            }
//                        }
//                    }
//                    .padding(16.dp)
//                ) {
//
//
//                    Icon(
//                        painter = painterResource(
//                            id =
//                            if (busyToday != null) {
//                                R.drawable.ic_checkbox_circle_fill
//                            } else {
//                                R.drawable.ic_checkbox_blank_circle_line
//                            }
//                        ), contentDescription = "Checkbox",
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .height(24.dp)
//                            .width(24.dp),
//                        tint =
//                        if (busyToday != null) {
//                            MaterialTheme.colors.primary
//                        } else {
//                            ExtendedTheme.colors.Black60
//                        }
//                    )
//
//
//                    Text(text = "Busy today", style = MaterialTheme.typography.body1)
//
//
//                }
//
//                val calendar = Calendar.getInstance()
//                calendar.time = selectedDate
//
//
//                val yourAvailability = userViewModel.availability.filter {
//                    isSameDay(it.starttime, selectedDate) && it.endtime != addDay(it.starttime, 1) }
//
//
//                if (yourAvailability.count() > 0) {
//                    Text(text = "You have...",
//                        textAlign = TextAlign.Left,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 32.dp)
//                    )
//
//                    yourAvailability
//                        .forEach {
//                            AvailabilityText(availability = it, onDelete = {
//                                WeeklyApi.retrofitService.removeAvailability(
//                                    mapOf("Authorization" to "token ${userViewModel.token}"),
//                                    id = it.id
//                                )
//                                userViewModel.removeAvailability(it)
//                            })
//                        }
//                    Spacer(Modifier.height(16.dp))
//
//                }
//
//                if (selectedCalendar != -1) {
//                    val availabilities = userViewModel.calendars[selectedCalendar].availabilities?.filter {
//                        isSameDay(it.starttime, selectedDate) }
//
//                    if (availabilities?.count() ?: 0 > 0) {
//                        Text(
//                            text = "Your friend...",
//                            textAlign = TextAlign.Left,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 32.dp)
//                        )
//
//
//
//                        availabilities
//                            ?.forEach { availability ->
//                                val profile = if (userViewModel.calendars[selectedCalendar].group_id != null){
//                                    userViewModel.groups[userViewModel.calendars[selectedCalendar].group_id]!!.members.first { it.user_id == availability.user_id }
//                                } else {
//                                    userViewModel.friends[userViewModel.calendars[selectedCalendar].user_id]
//                                }
//                                AvailabilityText(availability = availability, profile = profile)
//                            }
//                        Spacer(Modifier.height(16.dp))
//
//                    }
//                }
//
//                CustomButton(buttonText = "Add availability", onClick = {
//                    navController.navigate("addAvailabilityPage")
//                }, textColor = ExtendedTheme.colors.Black80, backgroundColor = ExtendedTheme.colors.LightGray, modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp))
//
//
//
//            }
//
//            if (selectedCalendar != -1) {
//                val leaveMessage =
//                    if (userViewModel.calendars[selectedCalendar].group_id != null) {
//                        "Leave calendar and group"
//                    } else {
//                        "Leave calendar"
//                    }
//                Text(leaveMessage,
//                    textAlign = TextAlign.Center,
//                    style = MaterialTheme.typography.h5,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(32.dp)
//                        .clickable {
//                            if (userViewModel.calendars[selectedCalendar].group_id != null) {
//                                coroutine.launch {
//                                    userViewModel.calendars[selectedCalendar].group_id?.let {
//                                        WeeklyApi.retrofitService.leaveGroup(
//                                            mapOf("Authorization" to "token ${userViewModel.token}"),
//                                            it
//                                        )
//                                        userViewModel.removeGroup(it)
//                                        userViewModel.removeCalendar(selectedCalendar)
//                                    }
//                                }
//                            } else {
//                                coroutine.launch {
//                                    userViewModel.calendars[selectedCalendar].user_id?.let {
//                                        WeeklyApi.retrofitService.leaveCalendar(
//                                            mapOf("Authorization" to "token ${userViewModel.token}"),
//                                            it
//                                        )
//                                        userViewModel.removeFriend(it)
//                                        userViewModel.removeCalendar(selectedCalendar)
//                                    }
//                                }
//                            }
//                        })
//            }
//
//            Spacer(modifier = Modifier.height(96.dp))
//        }
//    }
//}
//



//@Composable
//fun Chip(text: String, isSelected: Boolean = false, icon: Int? = null, modifier: Modifier = Modifier){
//    Row(
//        modifier
//            .clip(RoundedCornerShape(20.dp))
//            .background(
//                if (isSelected) {
//                    MaterialTheme.colors.primary
//                } else {
//                    ExtendedTheme.colors.LightGray
//                }
//            )
//            .height(40.dp)
//            .padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
//
//        if (icon != null) {
//            Icon(painter = painterResource(id = icon), contentDescription = null, tint = ExtendedTheme.colors.Black60, modifier = Modifier.size(20.dp))
//            Spacer(Modifier.width(8.dp))
//        }
//
//        Text(
//            text = text,
//            style = MaterialTheme.typography.h4,
//            color = if (isSelected) { MaterialTheme.colors.onPrimary } else { ExtendedTheme.colors.Black60 }
//        )
//    }
//
//
//}
//



//@SuppressLint("SimpleDateFormat")
//@Composable
//fun CalendarTitleBar(navController: NavController, date: Date, nextMonth: (Date) -> Unit, previousMonth: (Date) -> Unit, userViewModel: UserViewModel){
//
//    Row(
//        Modifier
//            .fillMaxWidth()
//            .height(IntrinsicSize.Min)
//            .padding(horizontal = 16.dp)
//            .padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
//
//        ProfilePicture(url = userViewModel.profile?.profile_picture, modifier = Modifier
//            .padding(vertical = 12.dp)
//            .clip(CircleShape)
//            .clickable {
//                navController.navigate("accountPage")
//            })
//
//        Spacer(Modifier.width(16.dp))
//
//        Text(text = SimpleDateFormat("MMM yyyy").format(date), style = MaterialTheme.typography.h1)
//
//        Spacer(Modifier.width(6.dp))
//
//        Icon(
//            modifier = Modifier
//                .fillMaxHeight()
//                .size(36.dp)
//                .clip(CircleShape)
//                .clickable {
//                    previousMonth(addMonth(date, -1))
//                }
//                .padding(horizontal = 6.dp),
//            painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
//            contentDescription = "previous month",
//        )
//        Icon(
//            modifier = Modifier
//                .fillMaxHeight()
//                .size(36.dp)
//                .clip(CircleShape)
//                .clickable {
//                    nextMonth(addMonth(date, 1))
//
//                }
//                .padding(horizontal = 6.dp),
//            painter = painterResource(id = R.drawable.ic_arrow_right_s_line),
//            contentDescription = "next month",
//        )
//
//        Spacer(modifier = Modifier.weight(1F))
//
//
//
////        IconButton(onClick = { navController.navigate("availabilityPage") }, modifier = Modifier.size(58.dp)) {
////            Icon(
////                modifier = Modifier.size(26.dp),
////                painter = painterResource(id = R.drawable.ic_calendar_check_line),
////                contentDescription = "availability",
////            )
////        }
//
//
//    }
//}