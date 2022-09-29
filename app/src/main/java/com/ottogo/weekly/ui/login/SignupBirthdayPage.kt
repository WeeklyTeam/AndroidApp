package com.ottogo.weekly.ui.login

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.Message
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import java.util.*


/*
*
* Built by: Linda
*
* Follow the guides of the figma file
* use a date picker dialog to pick the date, verify the age is over 13
* otherwise show a message that the user is too young
* pass the date to the signup view as a string that follows the format YYYY-MM-DD
*
* */


@Composable
fun SignupBirthdayPage(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    var error: String? by remember {mutableStateOf(value = null)}
    val context = LocalContext.current


    systemUiController.setSystemBarsColor(color = Color.White)

    val calendar = Calendar.getInstance()

    var age: Int by remember { mutableStateOf(0) }
    var year: Int by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var month: Int by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var day: Int by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    var maxDay: Int by remember { mutableStateOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            year = selectedYear
            month = selectedMonth
            day = selectedDay
            age = getAge(year, month, day)

        }, year, month, day
    )


    Column(modifier = Modifier
        .systemBarsPadding()
        .verticalScroll(rememberScrollState())) {
        LoginTitle(navController = navController, title = "Birthday")

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (error != null){
                Spacer(modifier = Modifier.height(32.dp))
                Message(error.toString())
            }

            Spacer(Modifier.height(32.dp))


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                AndroidView(factory = { context ->

                    val customView = DatePicker(context, null, R.style.DatePickerSpinnerStyle)
                    customView.spinnersShown = true //deprecated
                    customView.calendarViewShown = false //deprecated
                    customView.setOnDateChangedListener { view, newYear, newMonth, newDay ->
                        year = newYear
                        month = newMonth
                        day = newDay
                        age = getAge(year, month, day)

                    }


                    customView


                })
            } else   {

                Text(text = "Selected Birthday: $year-$month-$day")
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    datePickerDialog.show()
                }) {
                    Text(text = "Open Date Picker")
                }
            }

            Spacer(modifier = Modifier.padding(bottom = 32.dp))

            when(age){
                -1 -> Text("You're $age years old \uD83E\uDD2F")
                0 -> Text("You're $age years old \uD83D\uDC76")
                else -> Text("You're $age years old \uD83C\uDF82")
            }


            Spacer(modifier = Modifier.padding(bottom = 32.dp))
            CustomButton(buttonText = "Next") {
                val dob = Calendar.getInstance()
                val minimumDate = Calendar.getInstance()

                minimumDate.add(Calendar.YEAR, -13)

                if (age > 12){
                    navController.navigate("signupPage/$year-$month-$day")
                } else {
                    error = "You must be over 13 years old"
                }
            }
        }
            

    }

}

fun getAge(year: Int, month: Int, day: Int): Int {
    val dob = Calendar.getInstance()
    val today = Calendar.getInstance()
    dob[year, month] = day
    var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
    if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
        age--
    }
    return age
}