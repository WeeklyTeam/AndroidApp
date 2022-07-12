package com.ottogo.weekly.ui.calendar.availability

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Availability
import com.ottogo.weekly.ui.calendar.*
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.SelectableOption
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.Typography
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
@Composable
fun AddAvailabilityPage (navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current

    var notBusy by remember { mutableStateOf(false) }
    var availabilityName by remember { mutableStateOf("") }
    var starttime by remember { mutableStateOf(Date()) }
    var endtime by remember { mutableStateOf(Date()) }



    var endtimeCalendar = Calendar.getInstance()
    val endtimeHour = endtimeCalendar[Calendar.HOUR_OF_DAY]
    val endtimeMinute = endtimeCalendar[Calendar.MINUTE]

    var starttimeCalendar = Calendar.getInstance()
    val starttimeHour = starttimeCalendar[Calendar.HOUR_OF_DAY]
    val starttimeMinute = starttimeCalendar[Calendar.MINUTE]

    val starttimeDay = starttimeCalendar[Calendar.DAY_OF_MONTH]
    val starttimeYear = starttimeCalendar[Calendar.YEAR]
    val starttimeMonth = starttimeCalendar[Calendar.MONTH]


    val endtimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            endtimeCalendar.set(Calendar.HOUR_OF_DAY, mHour)
            endtimeCalendar.set(Calendar.MINUTE, mMinute)
            endtime = endtimeCalendar.time
        }, endtimeHour, endtimeMinute, false
    )

    val starttimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            starttimeCalendar.set(Calendar.HOUR_OF_DAY, mHour)
            starttimeCalendar.set(Calendar.MINUTE, mMinute)
            starttime = starttimeCalendar.time


            endtimePickerDialog.show()
        }, starttimeHour, starttimeMinute, false
    )

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            starttimeCalendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth)
            starttimeCalendar.set(Calendar.MONTH, mMonth)
            starttimeCalendar.set(Calendar.YEAR, mYear)
            endtimeCalendar.set(Calendar.YEAR, mYear)
            endtimeCalendar.set(Calendar.MONTH, mMonth)
            endtimeCalendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth)

            starttimePickerDialog.show()
        }, starttimeYear, starttimeMonth, starttimeDay
    )


    val options = listOf("Busy", "Free")

    Column() {
        TitleBar(navController, "Add availability")
        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)
        Column(
            Modifier
                .verticalScroll(rememberScrollState())) {

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextField(helper = "Name (Optional)", hint = "i.e. School, Work, etc...", input = availabilityName, onChange = { availabilityName = it }, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)){

                Column() {
                    Text("From", style = MaterialTheme.typography.h4)
                    Spacer(Modifier.height(16.dp))
                    Text(SimpleDateFormat("EEE, MMM dd h:mma").format(starttime) + " to " + SimpleDateFormat("h:mma").format(endtime), style = MaterialTheme.typography.body1)
                }

                Spacer(Modifier.width(16.dp))



                CustomButton(buttonText = "Change", onClick = {datePickerDialog.show()}, outlineColor = ExtendedTheme.colors.LightGray, textColor = MaterialTheme.colors.primary, backgroundColor = MaterialTheme.colors.onPrimary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            options.forEachIndexed { index, option ->
                SelectableOption(option, index == notBusy.compareTo(false), modifier = Modifier.clickable{
                    notBusy = index != 0
                })
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                buttonText = "Add",
                onClick = {
                    userViewModel.addAvailability(
                            WeeklyApi.retrofitService.addAvailability(
                                mapOf("Authorization" to "token ${userViewModel.token}"),
                                mapOf(
                                    "busy" to !notBusy,
                                    "starttime" to starttime,
                                    "endtime" to endtime,
                                    "title" to availabilityName,
                                    "days_of_week" to listOf<Int>()
                                )
                            )
                    )

                }, modifier = Modifier.padding(horizontal = 16.dp) )

        }

    }
}


@Composable
fun timeRangeTextBox(): String {
    var timeRange by remember { mutableStateOf("") }

    Text(
        text = "From",
        style = MaterialTheme.typography.h3)
    TextField(
        value = timeRange,
        onValueChange = { timeRange = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 16.dp, start = 16.dp, end = 65.dp),
        singleLine = true)

    return timeRange
}

