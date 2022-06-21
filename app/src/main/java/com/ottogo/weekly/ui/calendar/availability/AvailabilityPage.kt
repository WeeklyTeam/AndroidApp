package com.ottogo.weekly.ui.calendar.availability

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Availability
import com.ottogo.weekly.ui.calendar.*
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import java.util.*


@Composable
fun AvailabilityPage(navController: NavController, userViewModel: UserViewModel){
    var selectedDate: Date by rememberSaveable {
        mutableStateOf(beginningOfDay(date = Date()))
    }

    var startOfWeek: Date by rememberSaveable {
        mutableStateOf(beginningOfWeek())
    }

    Column {
        TitleBar(navController = navController, title = "Availability", iconButtons = {
            IconButton(
                onClick = { navController.navigate("addAvailabilityPage") },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_add_line),
                    contentDescription = null,
                )
            }
        })

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        WeeklyCalendarComponent(startOfWeek = startOfWeek, selectedDate = selectedDate, modifier = Modifier.padding(16.dp), selectDate = { selectedDate =
            it!!
        })

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Box(Modifier.verticalScroll(rememberScrollState())){



            Column() {
                (7..12).forEach{ time ->
                    TimeRow(time = time, anteMeridiem = true)
                }

                (1..12).forEach{ time ->
                    TimeRow(time = time, anteMeridiem = false)

                }
            }

            userViewModel.availability?.filter { it.starttime > selectedDate && it.endtime < addDay(selectedDate, 1) }
                ?.forEach { availability ->
                    val diff: Long = availability.starttime.time - selectedDate.time
                    val seconds = diff / 1000
                    val minutes = seconds / 60
                    Log.d("availability", availability.starttime.toString())
                    Log.d("availability", minutes.toString())

                    AvailabilityCard(availability = availability, modifier = Modifier.padding(top = (minutes.toInt()-6*60-30).dp, start = 96.dp, end = 16.dp))
                }


        }
    }
}

fun beginningOfWeek(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    // set day to minimum
    // set day to minimum

    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return addDay(calendar.time, -(calendar.get(Calendar.DAY_OF_WEEK) - 1))
}


fun beginningOfDay(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    // set day to minimum
    // set day to minimum

    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}

@Composable
fun AvailabilityCard(availability: Availability, modifier: Modifier = Modifier){
    Row(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ExtendedTheme.colors.LightGreen)
            .padding(12.dp)
            .fillMaxWidth()
            ) {
        Column() {
            Text(availability.title.ifEmpty { "Busy" }, color = ExtendedTheme.colors.DarkGreen, style = MaterialTheme.typography.h5)
            Spacer(Modifier.height(2.dp))
            Text(availability.title.ifEmpty { "8:30pm-5:00pm" }, color = ExtendedTheme.colors.DarkGreen, style = MaterialTheme.typography.body2)
        }


    }
}

@Composable
fun TimeRow(time: Int, anteMeridiem: Boolean){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$time" + if (anteMeridiem) { "am" } else { "pm" },
            style = MaterialTheme.typography.body2,
            color = ExtendedTheme.colors.Black40,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(96.dp)
                .padding(horizontal = 16.dp)

        )

        Divider(
            thickness = 1.dp,
            color = ExtendedTheme.colors.LightGray,
            modifier = Modifier.padding(top = 29.dp, bottom = 30.dp, end = 16.dp)
        )
    }
}

@Composable
fun WeeklyCalendarComponent(startOfWeek: Date, modifier: Modifier = Modifier, selectedDate: Date, selectDate: (Date) -> Unit){

    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val calendar = Calendar.getInstance()


    Row(modifier = modifier) {

        for (i in (0..6)) {
            val date = addDay(startOfWeek, i)
            calendar.time = date


            Column(Modifier.width(IntrinsicSize.Min).weight(1F)) {
                Text(text = daysOfWeek[i], style = MaterialTheme.typography.h4, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth())

                CalendarBox(value = calendar.get(Calendar.DATE).toString(), date = date, isSelected = selectedDate == date, modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .clickable {
                        selectDate(date)
                    })
                
            }

        }
    }
        
    
}