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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
@Composable
fun AddAvailabilityPage (navController: NavController, userViewModel: UserViewModel, openEmoji: () -> Unit, closeSheet: () -> Unit, bottomSheetViewModel: BottomSheetViewModel) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var starttime by remember { mutableStateOf(Date()) }
    var endtime by remember { mutableStateOf(Date()) }



    var endtimeCalendar = Calendar.getInstance()
    val endtimeHour = endtimeCalendar[Calendar.HOUR_OF_DAY]
    val endtimeMinute = endtimeCalendar[Calendar.MINUTE]

    val endtimeDay = endtimeCalendar[Calendar.DAY_OF_MONTH]
    val endtimeYear = endtimeCalendar[Calendar.YEAR]
    val endtimeMonth = endtimeCalendar[Calendar.MONTH]


    var starttimeCalendar = Calendar.getInstance()
    val starttimeHour = starttimeCalendar[Calendar.HOUR_OF_DAY]
    val starttimeMinute = starttimeCalendar[Calendar.MINUTE]

    val starttimeDay = starttimeCalendar[Calendar.DAY_OF_MONTH]
    val starttimeYear = starttimeCalendar[Calendar.YEAR]
    val starttimeMonth = starttimeCalendar[Calendar.MONTH]

    val endtimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            endtimeCalendar.time = endtime
            endtimeCalendar.set(Calendar.HOUR_OF_DAY, mHour)
            endtimeCalendar.set(Calendar.MINUTE, mMinute)
            endtime = endtimeCalendar.time

        }, endtimeHour, endtimeMinute, false
    )

    val starttimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            starttimeCalendar.time = starttime
            starttimeCalendar.set(Calendar.HOUR_OF_DAY, mHour)
            starttimeCalendar.set(Calendar.MINUTE, mMinute)
            starttime = starttimeCalendar.time

            if (starttime > endtime){
                endtime = starttime
            }

        }, starttimeHour, starttimeMinute, false
    )

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            starttimeCalendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth)
            starttimeCalendar.set(Calendar.MONTH, mMonth)
            starttimeCalendar.set(Calendar.YEAR, mYear)
            starttime = starttimeCalendar.time

            if (starttime > endtime){
                endtime = starttime
            }


        }, starttimeYear, starttimeMonth, starttimeDay
    )

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            endtimeCalendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth)
            endtimeCalendar.set(Calendar.MONTH, mMonth)
            endtimeCalendar.set(Calendar.YEAR, mYear)
            endtime = endtimeCalendar.time

        }, starttimeYear, starttimeMonth, starttimeDay
    )


    var created by remember{
        mutableStateOf(false)
    }

    var emoji by remember{
        mutableStateOf("\uD83D\uDC40")
    }

    LaunchedEffect(key1 = title){
        if(title.contains("[^A-Za-z0-9 ]".toRegex())){
            emoji = title.replace("[A-Za-z0-9 ]".toRegex(), "\uD83D\uDC40")
        }
    }

    LaunchedEffect(key1 = bottomSheetViewModel.plotEmoji, block = {
        if (!bottomSheetViewModel.plotEmoji.isNullOrEmpty()){
            emoji = bottomSheetViewModel.plotEmoji ?: ""
            closeSheet()
        }
    })
    val localFocusManager = LocalFocusManager.current

        Column() {
        TitleBar(navController, "Add availability")
        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)
        Column(
            Modifier
                .verticalScroll(rememberScrollState())) {

            if (created){
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Successfully added!", color = ExtendedTheme.colors.Green, style = MaterialTheme.typography.h5, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            EmojiCircle(emoji = emoji, modifier = Modifier.padding(horizontal = 16.dp), onClick = {openEmoji()})

            Spacer(modifier = Modifier.height(24.dp))


            CustomTextField(helper = "What you up to?", hint = "i.e. \uD83D\uDCDA School, \uD83D\uDCBC Work, etc...", input = title.replace("[^A-Za-z0-9 ]".toRegex(), ""), onChange = { title = it }, modifier = Modifier.padding(horizontal = 16.dp), done = true, keyboardActions = KeyboardActions(onDone = { localFocusManager.clearFocus() }))

            Spacer(modifier = Modifier.height(24.dp))

            Text("From", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.h4)

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.padding(horizontal = 16.dp)){

                Text(SimpleDateFormat("EEE, MMM dd").format(starttime), style = MaterialTheme.typography.body1, modifier = Modifier
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(ExtendedTheme.colors.LightGray)
                    .clickable { startDatePickerDialog.show() }
                    .padding(horizontal = 12.dp, vertical = 8.dp))

                Spacer(modifier = Modifier.width(8.dp))

                Text(SimpleDateFormat("h:mma").format(starttime), style = MaterialTheme.typography.body1, modifier = Modifier
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(ExtendedTheme.colors.LightGray)
                    .clickable { starttimePickerDialog.show() }
                    .padding(horizontal = 12.dp, vertical = 8.dp))

                Spacer(modifier = Modifier.width(8.dp))

                Text("to", style = MaterialTheme.typography.body1, modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.padding(horizontal = 16.dp)){

                Text(SimpleDateFormat("EEE, MMM dd").format(endtime), style = MaterialTheme.typography.body1, modifier = Modifier
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(ExtendedTheme.colors.LightGray)
                    .clickable { endDatePickerDialog.show() }
                    .padding(horizontal = 12.dp, vertical = 8.dp))

                Spacer(modifier = Modifier.width(8.dp))

                Text(SimpleDateFormat("h:mma").format(endtime), style = MaterialTheme.typography.body1, modifier = Modifier
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(ExtendedTheme.colors.LightGray)
                    .clickable { endtimePickerDialog.show() }
                    .padding(horizontal = 12.dp, vertical = 8.dp))



            }

            Spacer(modifier = Modifier.height(36.dp))

            CustomButton(
                buttonText = "LOL",
                onClick = {
                    Log.d("alarmStatus", "hello")
                }
            )

            CustomButton(
                buttonText = "Add",
                onClick = {
                    // Code for Testing the Notifications
                    // Set starttime to 2 hours 5 seconds ahead
                    // var calendar = Calendar.getInstance()
                    // calendar.add(Calendar.HOUR_OF_DAY, +2)
                    // calendar.add(Calendar.SECOND, +5)
                    // Log.i("alarmStatus", "Calendar has time: ${calendar.time}")
                    // var testStartTime: Date = calendar.time

                    userViewModel.addPlot(
                            WeeklyApi.retrofitService.createPlot(
                                mapOf("Authorization" to "token ${userViewModel.token}"),
                                mapOf(
                                    "starttime" to starttime,
                                    "endtime" to endtime,
                                    "name" to title.replace("[^A-Za-z0-9 ]".toRegex(), ""),
                                    "emoji" to emoji,
                                    )
                            ),
                            context
                    )
                    created = true

                }, modifier = Modifier.padding(horizontal = 16.dp) )

            Spacer(modifier = Modifier.height(36.dp))


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

