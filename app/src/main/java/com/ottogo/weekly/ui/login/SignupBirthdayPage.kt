package com.ottogo.weekly.ui.login

import android.app.DatePickerDialog
import android.os.Build
import android.util.AttributeSet
import android.util.Xml
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import org.xmlpull.v1.XmlPullParser
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

    var year: Int by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var month: Int by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var day: Int by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            year = selectedYear
            month = selectedMonth
            day = selectedDay
            val dob = Calendar.getInstance()
            val minimumDate = Calendar.getInstance()
            dob.set(year, month, day)

            minimumDate.add(Calendar.YEAR, -13)

            if (minimumDate.compareTo(dob) < 0){
                error = "You must be over 13 years old"
            }



        }, year, month, day
    )


    Column(modifier = Modifier.systemBarsPadding()) {
        LoginTitle(navController = navController, title = "Birthday")

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {

            if (error != null){
                Spacer(modifier = Modifier.height(32.dp))
                Message(error.toString())
            }

            Spacer(Modifier.height(32.dp))


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val resources = context.resources
                val parser: XmlPullParser = resources.getXml(R.xml.datepicker)
                val attributes: AttributeSet = Xml.asAttributeSet(parser)

                AndroidView(factory = { context ->

                    val dp = DatePicker(context, null)
                    dp.setSpinnersShown(true)
                    dp.setOnDateChangedListener { view, newYear, newMonth, newDay ->
                        year = newYear
                        month = newMonth
                        day = newDay
                    }


                    dp


                })
            } else {

                Text(text = "Selected Birthday: $year-$month-$day")
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    datePickerDialog.show()
                }) {
                    Text(text = "Open Date Picker")
                }
            }


            Spacer(modifier = Modifier.padding(bottom = 32.dp))
            CustomButton(buttonText = "Next") {
                navController.navigate("signupPage/$year-$month-$day")
            }
        }
            

    }

}

