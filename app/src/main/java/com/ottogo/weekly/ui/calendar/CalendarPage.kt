package com.ottogo.weekly.ui.calendar

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Availability
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.api.models.Status
import com.ottogo.weekly.ui.calendar.DateFunctions.addDay
import com.ottogo.weekly.ui.calendar.DateFunctions.addMonth
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameDay
import com.ottogo.weekly.ui.calendar.availability.beginningOfDay
import com.ottogo.weekly.ui.calendar.availability.beginningOfWeek
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.calendar.ui.components.PlotCalendarItem
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.ScrollPicker
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalendarPage(navController: NavController, userViewModel: UserViewModel) {

    var displayWeek by rememberSaveable{
        mutableStateOf(beginningOfWeek())
    }



    var selectedDate: Date by rememberSaveable {
        mutableStateOf(beginningOfDay(Date()))
    }

    val coroutine = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    val foodEmojis = listOf<String>("\uD83C\uDF54", "\uD83E\uDD59", "\uD83E\uDDC6", "\uD83E\uDD63", "\uD83C\uDF5B", "\uD83C\uDF63")

    val statuses = listOf(
        Status(emoji = "\uD83D\uDCBC", title = "Busy"),
        Status(emoji = "\uD83E\uDD17", title = "Free"),
        Status(emoji = "\uD83E\uDD73", title = "Out"),
        Status(emoji = "\uD83C\uDFEB", title = "Class"),
        Status(emoji = "${foodEmojis[nextInt(foodEmojis.size)]}", title = "Hungry"),
        Status(emoji = "\uD83C\uDF79", title = "Parched"),
        Status(emoji = "\uD83E\uDD71", title = "Bored"),
        Status(emoji = "+", title = "Custom"))

    var showStatusDuration: Boolean by remember {
        mutableStateOf(false)
    }

    var showCustomStatus: Boolean by remember {
        mutableStateOf(false)
    }

    var status by remember{
        mutableStateOf("")
    }
    var statusEmoji by remember{
        mutableStateOf("")
    }

    var statusMinutes by remember { mutableStateOf(0) }
    var statusHours by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = selectedDate, block = {


//                try {
//
//                    val SA = WeeklyApi.retrofitService.getStatusesAndAdventures(
//                        mapOf("Authorization" to "token ${userViewModel.token}"),
//                        "2022-09-17",
//                        "23%3A50%3A00"
//                    )
//                    Log.d("S+A", SA.body.toString())
//
//
//
//                } catch (e: HttpException) {
//                    Log.d("S+A", e.toString())
//
//                }


    })

    if(showStatusDuration){
        Dialog(
            onDismissRequest = {
                showStatusDuration = false
                status = ""
                statusEmoji = ""
            },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colors.background)
                    .padding(24.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(){
                        Text("Hours", style=MaterialTheme.typography.h5, modifier = Modifier.width(72.dp), textAlign = TextAlign.Center)
                        Text("Minutes", style=MaterialTheme.typography.h5, modifier = Modifier.width(72.dp), textAlign = TextAlign.Center)

                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    ScrollPicker(options = listOf(List(24){ index -> String.format("%02d", (index))}, List(60){ index -> String.format("%02d", (index))}), selectItem = listOf(
                        {   it ->
                            statusHours = it

                        },
                        {   it ->
                            statusMinutes = it

                        }
                    ))

                    Spacer(Modifier.height(24.dp))
                        CustomButton(
                            buttonText = "Set",

                        ) {
                            val endtime = Calendar.getInstance()
                            endtime.add(Calendar.MINUTE, statusMinutes+statusHours*60)
                            userViewModel.addPlot(
                                WeeklyApi.retrofitService.createPlot(
                                    mapOf("Authorization" to "token ${userViewModel.token}"),
                                    mapOf("emoji" to statusEmoji, "name" to status, "starttime" to Date(), "endtime" to endtime.time)
                                )
                            )
                            showStatusDuration = false
                        }


                }


            }
        )
    }


    LaunchedEffect(key1 = status){
        if(status.contains("[^A-Za-z0-9 ]".toRegex())){
            statusEmoji = status.replace("[A-Za-z0-9 ]".toRegex(), "")
        }
    }
    val focusRequester = remember { FocusRequester() }

    if(showCustomStatus){
        statusEmoji = "\uD83D\uDE46"
        Dialog(
            onDismissRequest = {
                showCustomStatus = false

            },
            content = {
                Column(modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colors.background)
                    .padding(24.dp)) {
                    EmojiCircle(emoji = statusEmoji)
                    Spacer(modifier = Modifier.height(24.dp))


                    CustomTextField(
                        helper = "Status",
                        hint = "What you up to?",
                        input = status.replace("[^A-Za-z0-9 ]".toRegex(), ""),
                        onChange = {
                            status = it
                        },
                        modifier = Modifier.focusRequester(focusRequester),
                        keyboardActions = KeyboardActions(onNext = {
                            showCustomStatus = false
                            showStatusDuration = true
                        }),

                    )
                    Spacer(modifier = Modifier.height(24.dp))



                        CustomButton(buttonText = "Next",) {
                            showStatusDuration = true
                        }




                }
            },

        )
    }

    Column() {

        
        CalendarTitleBar2(navController = navController, date = displayWeek, selectedDate = selectedDate, nextMonth = { displayWeek = it }, previousMonth = { displayWeek = it }, userViewModel = userViewModel)

        Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.Start) {


            WeeklyCalendarComponent(displayWeek,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 12.dp),
                selectedDate = selectedDate,
                selectDate = { selectedDate = it },
                plots = userViewModel.plots.filter {
                        if (it.starttime != null) {
                            it.starttime >= displayWeek && it.starttime < addMonth(displayWeek, 1)
                        } else {
                            false
                        }
                }, availability = listOf()
            )

            if (userViewModel.plots.firstOrNull { !it.is_going } != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate("newPlotsPage") }
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(android.graphics.Color.parseColor("#fad0c4")),
                                    Color(android.graphics.Color.parseColor("#ffd1ff"))
                                ),
                                start = Offset(0f, Float.POSITIVE_INFINITY),
                                end = Offset(Float.POSITIVE_INFINITY, 0f)
                            )
                        )
                        .padding(16.dp)

                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            modifier = Modifier.size(36.dp),
                            painter = painterResource(id = R.drawable.ic_inbox_line),
                            contentDescription = null,
                            tint = Black

                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("You're invited!", style = MaterialTheme.typography.h4)

                    }
                }
            }

            if (userViewModel.plots.firstOrNull { isSameDay(it.starttime ?: Date(), selectedDate) && it.starttime != null } != null)
                Spacer(Modifier.height(24.dp))
                userViewModel.plots.filter { isSameDay(it.starttime ?: Date(), selectedDate) && it.starttime != null }.forEach{
                    PlotCalendarItem(plot = it, userViewModel = userViewModel) {
                        navController.navigate("plotPage/${it.id}")
                    }

                    Spacer(modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(ExtendedTheme.colors.LightGray)
                        .height(64.dp)
                        .width(1.dp))

                }


            if (isSameDay(Date(), selectedDate)) {

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Status",
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {


                    for (row in 1..(statuses.count() / 4))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                                .padding(horizontal = 16.dp)
                        ) {
                            for (col in 0..3) {
                                val statusItem = statuses[4 * (row - 1) + col]
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally, modifier =
                                    Modifier
                                        .width((screenWidth - 56.dp) / 4)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (statusItem.equals(
                                                    Status(
                                                        title = "Custom",
                                                        emoji = "+"
                                                    )
                                                )
                                            ) {
                                                showCustomStatus = true
                                            } else {
                                                status = statusItem.title
                                                statusEmoji = statusItem.emoji
                                                showStatusDuration = true
                                            }
                                        }
                                        .border(
                                            width = 1.dp,
                                            color = ExtendedTheme.colors.LightGray,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(vertical = 16.dp)
                                ) {

                                    Text(
                                        text = statusItem.emoji, style = TextStyle(
                                            fontFamily = nunitoFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 24.sp
                                        ), textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = statusItem.title,
                                        style = MaterialTheme.typography.body2,
                                        textAlign = TextAlign.Center
                                    )
                                }

                            }


                        }
                }

                Spacer(modifier = Modifier.height(24.dp))


                Text(text = "Friends", style = MaterialTheme.typography.h4, textAlign = TextAlign.Center, modifier = Modifier
                    .padding(horizontal = 16.dp))


            } else {
                val busyToday = userViewModel.availability.firstOrNull {
                    it.starttime == selectedDate && it.endtime == addDay(
                        selectedDate!!,
                        1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        runBlocking {
                            if (busyToday == null) {
                                userViewModel.addAvailability(
                                    WeeklyApi.retrofitService.addAvailability(
                                        mapOf("Authorization" to "token ${userViewModel.token}"),
                                        mapOf(
                                            "busy" to true,
                                            "starttime" to selectedDate!!,
                                            "endtime" to addDay(selectedDate!!, 1),
                                            "title" to "",
                                        )
                                    )
                                )
                            } else {
                                WeeklyApi.retrofitService.removeAvailability(
                                    mapOf("Authorization" to "token ${userViewModel.token}"),
                                    busyToday.id
                                )
                                userViewModel.removeAvailability(busyToday)
                            }
                        }
                    }
                    .padding(16.dp)
                ) {


                    Icon(
                        painter = painterResource(
                            id =
                            if (busyToday != null) {
                                R.drawable.ic_checkbox_circle_fill
                            } else {
                                R.drawable.ic_checkbox_blank_circle_line
                            }
                        ), contentDescription = "Checkbox",
                        modifier = Modifier
                            .padding(16.dp)
                            .height(24.dp)
                            .width(24.dp),
                        tint =
                        if (busyToday != null) {
                            MaterialTheme.colors.primary
                        } else {
                            ExtendedTheme.colors.Black60
                        }
                    )


                    Text(text = "Busy today", style = MaterialTheme.typography.body1)


                }
            }




            Spacer(modifier = Modifier.height(24.dp))


            Text(text = "Other adventures", style = MaterialTheme.typography.h4, textAlign = TextAlign.Center, modifier = Modifier
                .padding(horizontal = 16.dp))



                
            CustomButton(buttonText = "Add availability", onClick = {
                navController.navigate("addAvailabilityPage")
            }, textColor = ExtendedTheme.colors.Black80, backgroundColor = ExtendedTheme.colors.LightGray, modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp))






            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}


@Composable
fun WeeklyCalendarComponent(week: Date, shortened: Boolean = false, modifier: Modifier = Modifier, selectedDate: Date, selectDate: (Date) -> Unit, plots: List<Plot> = listOf(), availability: List<Availability> = listOf()){

    val daysOfWeek = if (shortened) { listOf("S", "M", "T", "W", "T", "F", "S") } else { listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat") }

    Column(modifier = modifier) {
        Row() {
            daysOfWeek.forEach { weekday ->
                Text(text = weekday, style = TextStyle(
                    fontFamily = nunitoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ), textAlign = TextAlign.Center, modifier = Modifier
                    .weight(1F)
                    .padding(vertical = 8.dp))
            }
        }

        Row {
            for (i in 0..6){

                    val calendar = Calendar.getInstance()
                    val date = addDay(week, i)
                    calendar.time = date
                    val busy = availability.firstOrNull{ isSameDay(it.starttime, date)  } != null
                    val plotToday = plots.firstOrNull{ isSameDay(it.starttime, date) } != null


                    CalendarBox(value = calendar.get(Calendar.DATE).toString(), date = date, isSelected = selectedDate == date, modifier = Modifier
                        .weight(1F)
                        .clip(CircleShape)
                        .clickable {
                            selectDate(date)

                        }, isUnavailable = busy, isPlot = plotToday)



            }
        }
    }
}




@SuppressLint("SimpleDateFormat")
@Composable
fun CalendarTitleBar2(navController: NavController, date: Date, selectedDate: Date, nextMonth: (Date) -> Unit, previousMonth: (Date) -> Unit, userViewModel: UserViewModel){

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

        Text(text = if (isSameDay(Date(), selectedDate)){ "Today" } else { SimpleDateFormat("MMM yyyy").format(selectedDate) }, style = MaterialTheme.typography.h1)

        Spacer(Modifier.width(6.dp))

        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .size(36.dp)
                .clip(CircleShape)
                .clickable {
                    previousMonth(addDay(date, -7))
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
                    nextMonth(addDay(date, 7))

                }
                .padding(horizontal = 6.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right_s_line),
            contentDescription = "next month",
        )

        Spacer(modifier = Modifier.weight(1F))



//        IconButton(onClick = { navController.navigate("availabilityPage") }, modifier = Modifier.size(58.dp)) {
//            Icon(
//                modifier = Modifier.size(26.dp),
//                painter = painterResource(id = R.drawable.ic_calendar_check_line),
//                contentDescription = "availability",
//            )
//        }


    }
}

@Composable
fun CalendarBox(value: String, date: Date = Date(), isSelected: Boolean = false, isPlot: Boolean  = false, isUnavailable: Boolean = false, modifier: Modifier = Modifier){
    Box(modifier = modifier, contentAlignment = Alignment.Center){




        if (isSelected) {
            Surface(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colors.primary,
                shape = CircleShape
            ) {}
        } else {

            if (isUnavailable) {
                Surface(
                    modifier = Modifier
                        .padding(top = 28.dp)
                        .size(4.dp),
                    color = ExtendedTheme.colors.Black60,
                    shape = CircleShape
                ) {}
            }

            if (isPlot) {
                Surface(
                    modifier = Modifier
                        .padding(top = 28.dp)
                        .size(4.dp),

                    color = MaterialTheme.colors.primary,
                    shape = CircleShape
                ) {}
            }
        }

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