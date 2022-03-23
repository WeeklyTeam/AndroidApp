package com.ottogo.weekly.ui.login

import android.app.DatePickerDialog
import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.CalendarView
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.ottogo.weekly.MyNonActivityClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
        Column(){

    }
}


