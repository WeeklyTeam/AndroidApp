package com.ottogo.weekly.ui.calendar.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Availability
import com.ottogo.weekly.api.models.Holiday
import com.ottogo.weekly.api.models.HolidayCategory
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.ui.calendar.CalendarBox
import com.ottogo.weekly.ui.calendar.DateFunctions.calendarRange
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameDay
import com.ottogo.weekly.ui.calendar.DateFunctions.setDay
import com.ottogo.weekly.viewmodels.UserViewModel
import java.util.*


// TODO: Is this where I should be placing the code for the calenar
@Composable
fun CalendarComponent(month: Date, shortened: Boolean = false, modifier: Modifier = Modifier, selectedDate: Date?, selectDate: (Date?) -> Unit, plots: List<Plot> = listOf(), availability: List<Availability> = listOf(), userViewModel: UserViewModel){

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
                        val calendar = Calendar.getInstance()
                        val date = setDay(month, range[j])
                        calendar.time = date
                        val busy = availability.firstOrNull{ isSameDay(it.starttime, date)  } != null
                        val plotToday = plots.firstOrNull{ isSameDay(it.starttime, date) } != null


                        CalendarBox(value = range[j].toString(), date = date, isSelected = selectedDate == date, modifier = Modifier
                            .weight(1F)
                            .clip(CircleShape)
                            .clickable {
                                if (selectedDate != date) {
                                    selectDate(date)
                                } else {
                                    selectDate(null)
                                }
                            }, isUnavailable = busy, isPlot = plotToday)
                    } else {
                        CalendarBox(value = "", modifier = Modifier.weight(1F))
                    }

                }
            }
        }
    }
}
