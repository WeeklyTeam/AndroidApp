package com.ottogo.weekly.ui.plot

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.calendar.CalendarComponent
import com.ottogo.weekly.ui.calendar.addMonth
import com.ottogo.weekly.ui.calendar.initialMonth
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ScrollPicker
import com.ottogo.weekly.ui.components.TitleBar
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@Composable
fun PlotDatePage(navController: NavController) {

    var selectedDate: Date? by rememberSaveable {
        mutableStateOf(null)
    }

    var displayCalendar by rememberSaveable {
        mutableStateOf(true)
    }


    var displayMonth by rememberSaveable{
        mutableStateOf(initialMonth())
    }

    Column() {
        TitleBar(navController = navController, title = "When")

        Spacer(Modifier.weight(1F))

        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = { displayMonth = addMonth(displayMonth, -1) }, modifier = Modifier.size(56.dp)) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(SimpleDateFormat(if (displayCalendar) {"MMM yyyy"} else {"MMM dd"}).format(displayMonth),
                    modifier = Modifier.weight(1F), style = MaterialTheme.typography.h2, textAlign = TextAlign.Center)

            IconButton(onClick = { displayMonth = addMonth(displayMonth, 1) }, modifier = Modifier.size(56.dp)) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_right_s_line),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (displayCalendar) {
            CalendarComponent(month = displayMonth, modifier = Modifier
                .padding(16.dp), selectedDate = null, selectDate = {
                displayCalendar = false
                selectedDate = it
            })
        }
        else {
            ScrollPicker(options = listOf(List(12){ index -> index.toString()}, listOf("AM", "PM")))
        }

        Spacer(Modifier.weight(1F))

        CustomButton(buttonText = "Next", onClick = {}, modifier = Modifier.padding(16.dp))
    }
}