package com.ottogo.weekly.ui.calendar

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Shape
import android.graphics.Color
import android.util.Log
import android.util.Range
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.calendar.availability.beginningOfDay
import com.ottogo.weekly.ui.calendar.ui.components.PlotItem
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.selects.select
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalendarPage(navController: NavController, userViewModel: UserViewModel) {

    var displayMonth by rememberSaveable{
        mutableStateOf(initialMonth())
    }

    var selectedCalendar by rememberSaveable {
        mutableStateOf(-1)
    }

    var selectedDate: Date? by rememberSaveable {
        mutableStateOf(null)
    }



    Column() {
        CalendarTitleBar(navController = navController, date = displayMonth, nextMonth = { displayMonth = it }, previousMonth = { displayMonth = it }, userViewModel = userViewModel)

        Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {


            CalendarComponent(displayMonth,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                selectedDate = selectedDate,
                selectDate = { selectedDate = it })


            if (userViewModel.friends.count() > 0 || userViewModel.groups.count() > 0) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {
                    Chip("Mine", isSelected = selectedCalendar == -1, modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCalendar = -1 })

                    Chip("Create", icon = R.drawable.ic_calendar_line, modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            navController.navigate("createCalendarPage")
                        })

                    userViewModel.calendars?.forEachIndexed { index, calendar ->
                        Chip(calendar.name,
                            isSelected = selectedCalendar == index,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCalendar = index })

                    }

                }
            }

            if (userViewModel.plots.count() ?: 0 > 0) {
                userViewModel.plots.filter {
                    if (it.starttime != null) {
                        it.starttime >= displayMonth && it.starttime < addMonth(displayMonth, 1)
                    } else {
                        true
                    }
                }
                    .filter {
                        if (selectedDate != null && it.starttime != null) {
                            it.starttime >= selectedDate && it.starttime < addDay(
                                selectedDate!!, 1
                            )
                        } else {
                            selectedDate == null
                        }
                    }
                    .filter {
                        if (selectedCalendar != -1) {
                            when {
                                it.relationship_id != null -> {
                                    it.relationship_id == userViewModel.calendars?.get(selectedCalendar)?.relationship_id
                                }
                                it.group_id != null -> {
                                    it.group_id == userViewModel.calendars?.get(selectedCalendar)?.group_id
                                }
                                else -> {
                                    false
                                }
                            }
                        } else {
                            true
                        }
                    }.forEach { plot ->
                        PlotItem(plot, onClick = {
                            navController.navigate("plotPage/${plot.id}")
                        }, userViewModel = userViewModel)

                    }
            } else {
                Text(text = "Make your first plan \uD83C\uDF89", style = MaterialTheme.typography.h2, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 48.dp, bottom = 24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tap ", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)
                    Icon(painter = painterResource(id = R.drawable.ic_add_line), contentDescription = "search", tint = ExtendedTheme.colors.Black60, modifier = Modifier.size(15.dp))
                    Text(" to begin", style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)

                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun CalendarComponent(month: Date, shortened: Boolean = false, modifier: Modifier = Modifier, selectedDate: Date?, selectDate: (Date?) -> Unit){

    val daysOfWeek = if (shortened) { listOf("S", "M", "T", "W", "T", "F", "S") } else { listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat") }
    val range = calendarRange(month = month).toList()

    Column(modifier = modifier) {
        Row() {
            daysOfWeek.forEach { weekday ->
                Text(text = weekday, style = MaterialTheme.typography.h4, textAlign = TextAlign.Center, modifier = Modifier
                    .weight(1F)
                    .padding(vertical = 8.dp))
            }
        }


        for (i in 0..(if (range.count() % 7 == 0){ range.count()/7-1 }else { (range.count()+7)/7-1 } )){
            Row {
                for (j in (i*7)..(i*7+6)) {
                    var blank = j >= range.count()
                    blank = !(!blank && range[j] > 0)
                    if (!blank) {
                        val date = setDay(month, range[j])

                        CalendarBox(value = range[j].toString(), date = date, isSelected = selectedDate == date, modifier = Modifier
                            .weight(1F)
                            .clip(CircleShape)
                            .clickable {
                                if (selectedDate != date) {
                                    selectDate(date)
                                } else {
                                    selectDate(null)
                                }
                            })
                    } else {
                        CalendarBox(value = "", modifier = Modifier.weight(1F))
                    }

                }
            }
        }
    }
}

@Composable
fun Chip(text: String, isSelected: Boolean = false, icon: Int? = null, modifier: Modifier = Modifier){
    Row(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colors.primary
                } else {
                    ExtendedTheme.colors.LightGray
                }
            )
            .height(40.dp)
            .padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {

        if (icon != null) {
            Icon(painter = painterResource(id = icon), contentDescription = null, tint = ExtendedTheme.colors.Black60, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.h4,
            color = if (isSelected) { MaterialTheme.colors.onPrimary } else { ExtendedTheme.colors.Black60 }
        )
    }


}

@Composable
fun CalendarBox(value: String, date: Date = Date(), isSelected: Boolean = false, isPlan: Boolean  = false, isUnavailable: Boolean = false, modifier: Modifier = Modifier){
    Box(modifier = modifier, contentAlignment = Alignment.Center){

        Surface(modifier = Modifier.size(32.dp),
            color = if (isSelected) { MaterialTheme.colors.primary } else { MaterialTheme.colors.onPrimary },
            shape = CircleShape
        ) {}

        Text(
            text = value,
            style = TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = if (isSelected) { FontWeight.Bold
                } else { FontWeight.Normal },
                fontSize = 16.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            color = if (!isSelected) { if (date >= beginningOfDay(Date())) { MaterialTheme.colors.onBackground } else { ExtendedTheme.colors.Black60 } } else { MaterialTheme.colors.onPrimary }
        )

    }


}

fun addMonth(date: Date, amount: Int): Date{
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.MONTH, amount)
    return calendar.time
}


fun addDay(date: Date, amount: Int): Date{
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.DATE, amount)
    return calendar.time
}

fun setDay(date: Date, day: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar[Calendar.DAY_OF_MONTH] = day

    return calendar.time
}

fun initialMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    // set day to minimum
    // set day to minimum
    calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}

fun calendarRange(month: Date): IntRange {
    val calendar = Calendar.getInstance()
    calendar.time = month
    return (calendar.get(Calendar.DAY_OF_WEEK)*-1+2)..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

@SuppressLint("SimpleDateFormat")
@Composable
fun CalendarTitleBar(navController: NavController, date: Date, nextMonth: (Date) -> Unit, previousMonth: (Date) -> Unit, userViewModel: UserViewModel){

    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {

        ProfilePicture(url = userViewModel.profile?.profile_picture, modifier = Modifier
            .padding(vertical = 12.dp)
            .clip(CircleShape)
            .clickable {
                navController.navigate("accountPage")
            })

        Spacer(Modifier.width(16.dp))

        Text(text = SimpleDateFormat("MMM yyyy").format(date), style = MaterialTheme.typography.h1)

        Spacer(Modifier.width(6.dp))

        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .size(36.dp)
                .clip(CircleShape)
                .clickable {
                    previousMonth(addMonth(date, -1))
                }
                .padding(horizontal = 6.dp),
            painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
            contentDescription = "previous month",
        )
        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .size(36.dp)
                .clip(CircleShape)
                .clickable {
                    nextMonth(addMonth(date, 1))

                }
                .padding(horizontal = 6.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right_s_line),
            contentDescription = "next month",
        )
        
        Spacer(modifier = Modifier.weight(1F))



        IconButton(onClick = { navController.navigate("availabilityPage") }, modifier = Modifier.size(58.dp)) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(id = R.drawable.ic_calendar_check_line),
                contentDescription = "availability",
            )
        }


    }
}