package com.ottogo.weekly.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.ottogo.weekly.BottomSheetType
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.WEEKLY_COMPLETE_PREF_KEY
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Availability
import com.ottogo.weekly.api.models.ModifiedPlot
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.api.models.Status
import com.ottogo.weekly.dataStore
import com.ottogo.weekly.ui.calendar.DateFunctions.*
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.calendar.ui.components.PlotCalendarItem
import com.ottogo.weekly.ui.calendar.weekly.LottieLoader
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.ScrollPicker
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.Black40
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextInt

class WeeklyViewModel(context: Context): ViewModel(){
    val weeklyCompleteDate =
        context.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[WEEKLY_COMPLETE_PREF_KEY] ?: ""
            }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalendarPage(navController: NavController, userViewModel: UserViewModel, openEmoji: () -> Unit, closeSheet: () -> Unit, bottomSheetViewModel: BottomSheetViewModel, weeklyViewModel: WeeklyViewModel) {

    var displayWeek by rememberSaveable{
        mutableStateOf(beginningOfWeek())
    }

    val context = LocalContext.current

    val weeklyCompletePref = weeklyViewModel.weeklyCompleteDate.collectAsState(initial = if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {SimpleDateFormat("dd-MM-yyyy").format(
            addDay(Date(), 1))} else {SimpleDateFormat("dd-MM-yyyy").format(Date())})


    var selectedDate: Date by rememberSaveable {
        mutableStateOf(beginningOfDay(Date()))
    }

    val coroutine = rememberCoroutineScope()

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp


//    val statuses = listOf(
//        Status(emoji = "\uD83D\uDCBC", title = "Busy"),
//        Status(emoji = "\uD83E\uDD17", title = "Free"),
//        Status(emoji = "\uD83E\uDD73", title = "Out"),
//        Status(emoji = "\uD83C\uDFEB", title = "Class"),
//        Status(emoji = "\uD83E\uDD6A", title = "Hungry"),
//        Status(emoji = "\uD83E\uDDCB", title = "Parched"),
//        Status(emoji = "\uD83E\uDD71", title = "Bored"),
//        Status(emoji = "+", title = "Custom"))

//    val friendStatuses = remember{
//        mutableStateListOf<ModifiedPlot>()
//    }
    val friendAdventures = remember{
        mutableStateListOf<ModifiedPlot>()
    }


//    var showStatusDuration: Boolean by remember {
//        mutableStateOf(false)
//    }
//
//    var showCustomStatus: Boolean by remember {
//        mutableStateOf(false)
//    }

//    var status by remember{
//        mutableStateOf("")
//    }
//    var statusEmoji by remember{
//        mutableStateOf("✨")
//    }

//    var statusMinutes by remember { mutableStateOf(0) }
//    var statusHours by remember { mutableStateOf(0) }
//
//    LaunchedEffect(key1 = bottomSheetViewModel.plotEmoji, block = {
//        if (!bottomSheetViewModel.plotEmoji.isNullOrEmpty() && bottomSheetViewModel.bottomSheetType == null){
//            statusEmoji = bottomSheetViewModel.plotEmoji ?: ""
//            closeSheet()
//            showCustomStatus = true
//        }
//    })


    LaunchedEffect(key1 = selectedDate, block = {

//            friendStatuses.clear()
            friendAdventures.clear()
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val hmsFormat = SimpleDateFormat("HH:mm:ss")

                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    hmsFormat.timeZone = TimeZone.getTimeZone("UTC")


                    val SA = WeeklyApi.retrofitService.getStatusesAndAdventures(
                        mapOf("Authorization" to "token ${userViewModel.token}"),
                        dateFormat.format(selectedDate),
                        java.net.URLEncoder.encode(hmsFormat.format(Date()), "utf-8"),
                        java.net.URLEncoder.encode(hmsFormat.format(selectedDate), "utf-8")
                    )
//                    friendStatuses.addAll(SA.statuses)
                    friendAdventures.addAll(SA.adventures)



                } catch (e: HttpException) {
                    Log.d("S+A", e.toString())

                }


    })

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var showWeekly by rememberSaveable { mutableStateOf(true) }
    //if it isn't, create a display that navigates users to the calendarsyncpage when tapped
    if (((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && weeklyCompletePref.value != SimpleDateFormat("dd-MM-yyyy").format(Date())) || weeklyCompletePref.value == "") && showWeekly) {
        Dialog(
            onDismissRequest = {
                showWeekly = false
            },
            content = {

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colors.background)
                    .padding(24.dp)) {
                    LottieLoader(modifier = Modifier.height(screenHeight*1/3), res = R.raw.eventsearch)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Let's find something to do!", textAlign = TextAlign.Center, style = MaterialTheme.typography.h3)
                    Spacer(modifier = Modifier.height(24.dp))


                    CustomButton(buttonText = "Okay!") {
                        navController.navigate("weeklyAvailabilityPage")
                    }


                }
            }
        )
    }






//    if(showStatusDuration){
//        Dialog(
//            onDismissRequest = {
//
//                showStatusDuration = false
//                status = "✨"
//                statusEmoji = ""
//            },
//            content = {
//                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
//                    .clip(RoundedCornerShape(24.dp))
//                    .background(MaterialTheme.colors.background)
//                    .padding(24.dp)) {
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    Row(){
//                        Text("Hours", style=MaterialTheme.typography.h5, modifier = Modifier.width(72.dp), textAlign = TextAlign.Center)
//                        Text("Minutes", style=MaterialTheme.typography.h5, modifier = Modifier.width(72.dp), textAlign = TextAlign.Center)
//
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//                    ScrollPicker(options = listOf(List(24){ index -> String.format("%02d", (index))}, List(60){ index -> String.format("%02d", (index))}), selectItem = listOf(
//                        {   it ->
//                            statusHours = it
//
//                        },
//                        {   it ->
//                            statusMinutes = it
//
//                        }
//                    ))
//
//                    Spacer(Modifier.height(24.dp))
//                        CustomButton(
//                            buttonText = "Set",
//
//                        ) {
//                            val endtime = Calendar.getInstance()
//                            endtime.add(Calendar.MINUTE, statusMinutes+statusHours*60)
//                            userViewModel.addPlot(
//                                WeeklyApi.retrofitService.createPlot(
//                                    mapOf("Authorization" to "token ${userViewModel.token}"),
//                                    mapOf("emoji" to statusEmoji, "name" to status.replace("[^A-Za-z0-9 .?!()\"]".toRegex(), ""), "starttime" to Date(), "endtime" to endtime.time, "is_plot" to false)
//                                )
//                            )
//                            showCustomStatus = false
//                            showStatusDuration = false
//                            status = "✨"
//                            statusEmoji = ""
//                        }
//
//
//                }
//
//
//            }
//        )
//    }
//
//
//    LaunchedEffect(key1 = status){
//        if(status.contains("[^A-Za-z0-9 .?!()\"]".toRegex())){
//            statusEmoji = status.replace("[A-Za-z0-9 .?!()\"]".toRegex(), "")
//        }
//    }
    val focusRequester = remember { FocusRequester() }



    var calendarSwipeOffset by remember { mutableStateOf(0f) }


    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Column() {
//        if(showCustomStatus){
//            Dialog(
//                onDismissRequest = {
//                    showCustomStatus = false
//
//                },
//                content = {
//                    Column(modifier = Modifier
//                        .clip(RoundedCornerShape(24.dp))
//                        .background(MaterialTheme.colors.background)
//                        .padding(24.dp)) {
//                        EmojiCircle(emoji = statusEmoji, onClick = {
//                            showCustomStatus = false
//                            openEmoji()
//                        })
//                        Spacer(modifier = Modifier.height(24.dp))
//
//
//                        CustomTextField(
//                            helper = "Status",
//                            hint = "What you up to?",
//                            input = status.replace("[^A-Za-z0-9 .?!()\"]".toRegex(), ""),
//                            onChange = {
//                                status = it
//                            },
//                            modifier = Modifier.focusRequester(focusRequester),
//                            keyboardActions = KeyboardActions(onNext = {
//                                showCustomStatus = false
//                                showStatusDuration = true
//                            }),
//
//                            )
//                        Spacer(modifier = Modifier.height(24.dp))
//
//
//
//                        CustomButton(buttonText = "Next") {
//                            showStatusDuration = true
//                        }
//
//
//
//
//                    }
//                },
//
//                )
//        }
        
        CalendarTitleBar2(navController = navController, date = displayWeek, selectedDate = selectedDate, resetDate = {
            selectedDate = beginningOfDay(Date())
            displayWeek = beginningOfWeek()
        }, nextMonth = { displayWeek = it }, previousMonth = { displayWeek = it }, userViewModel = userViewModel)

        WeeklyCalendarComponent(displayWeek,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 12.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    reverseDirection = true,
                    onDragStopped = {
                        if (calendarSwipeOffset > 50) {
                            displayWeek = addDay(displayWeek, 7)
                            calendarSwipeOffset = 0f

                        } else if (calendarSwipeOffset < -50) {
                            displayWeek = addDay(displayWeek, -7)
                            calendarSwipeOffset = 0f

                        }
                    },
                    state = rememberDraggableState { delta ->
                        calendarSwipeOffset += delta

                        Log.d("drag", delta.toString())
                    }
                ),
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

        Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.Start) {






            //Connor
            //Here you can add an if statement that checks if the user is new to the current version
            //by checking if the shared preferences saved version is equal to the current version


            if (userViewModel.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate("recommendedPlotsPage") }
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colors.primary,
                                    ExtendedTheme.colors.Pink
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
                            painter = painterResource(id = R.drawable.ic_calendar_2_line),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary

                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("See your recommendations!", style = MaterialTheme.typography.h4, color = MaterialTheme.colors.onPrimary)

                    }
                }
            }

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
                val plots = userViewModel.plots.filter { isSameDay(it.starttime ?: Date(), selectedDate) && it.starttime != null && it.is_going }
                plots.forEachIndexed{ index, it ->
                    PlotCalendarItem(plot = it, navController = navController, userViewModel = userViewModel) {
                        navController.navigate("plotPage/${it.id}")
                    }

                    if (index != plots.lastIndex) {
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(ExtendedTheme.colors.LightGray)
                                .height(64.dp)
                                .width(1.dp)
                        )
                    }

                }


//            if (isSameDay(Date(), selectedDate)) {
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Text(
//                    text = "Status",
//                    style = MaterialTheme.typography.h4,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                )

//                Spacer(modifier = Modifier.height(16.dp))

//                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//
//
//                    for (row in 1..(statuses.count() / 4))
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
//                                .padding(horizontal = 16.dp)
//                        ) {
//                            for (col in 0..3) {
//                                val statusItem = statuses[4 * (row - 1) + col]
//                                Column(
//                                    horizontalAlignment = Alignment.CenterHorizontally, modifier =
//                                    Modifier
//                                        .width((screenWidth - 56.dp) / 4)
//                                        .clip(RoundedCornerShape(12.dp))
//                                        .clickable {
//                                            if (statusItem.equals(
//                                                    Status(
//                                                        title = "Custom",
//                                                        emoji = "+"
//                                                    )
//                                                )
//                                            ) {
//                                                showCustomStatus = true
//                                            } else {
//                                                status = statusItem.title
//                                                statusEmoji = statusItem.emoji
//                                                showStatusDuration = true
//                                            }
//                                        }
//                                        .border(
//                                            width = 1.dp,
//                                            color = ExtendedTheme.colors.LightGray,
//                                            shape = RoundedCornerShape(12.dp)
//                                        )
//                                        .padding(vertical = 16.dp)
//                                ) {
//
//                                    Text(
//                                        text = statusItem.emoji, style = TextStyle(
//                                            fontFamily = nunitoFamily,
//                                            fontWeight = FontWeight.Normal,
//                                            fontSize = 24.sp
//                                        ), textAlign = TextAlign.Center
//                                    )
//                                    Spacer(modifier = Modifier.height(8.dp))
//                                    Text(
//                                        text = statusItem.title,
//                                        style = MaterialTheme.typography.body2,
//                                        textAlign = TextAlign.Center
//                                    )
//                                }
//
//                            }
//
//
//                        }
//                }
//
//                if (statuses.count() > 0) {
//                    Spacer(modifier = Modifier.height(24.dp))
//
//
//                    Text(
//                        text = "Friends",
//                        style = MaterialTheme.typography.h4,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp)
//                    )
//
//                    Row(
//                        modifier = Modifier
//                            .horizontalScroll(rememberScrollState())
//                            .padding(8.dp)
//                    ) {
//
//                        for (status in friendStatuses) {
//                            StatusItem(
//                                status = status,
//                                userViewModel = userViewModel,
//                                modifier = Modifier.clickable { navController.navigate("privateChatPage/${status.user_id}") })
//                        }
//                    }
//                }
//
//
//            }


            if (friendAdventures.count() > 0) {


                Spacer(modifier = Modifier.height(24.dp))


                Text(
                    text = "Other adventures",
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))

                for (adventure in friendAdventures) {
                    AdventureItem(
                        adventure,
                        userViewModel,
                        Modifier.clickable { navController.navigate("privateChatPage/${adventure.user_id}") })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


//
//            CustomButton(buttonText = "Add availability", onClick = {
//                navController.navigate("addAvailabilityPage")
//            }, textColor = ExtendedTheme.colors.Black80, backgroundColor = ExtendedTheme.colors.LightGray, modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp))
//





            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
fun AdventureItem(adventure: ModifiedPlot, userViewModel: UserViewModel, modifier: Modifier = Modifier){
    // Grammar rules for adventures
    // if the adventures first word is ending in -ing then write name is going _____
    // i.e. john is going bowling, angela is going rockclimbing at joshua tree
    // else the adventureText is equal to name is going to ____
    // i.e. barbara is going to starbucks, emilio is going to park

    val adventureText = if (adventure.name.substringBefore(" ").endsWith("ing")) {
        userViewModel.friends[adventure.user_id]?.name?.substringBefore(" ") + " is going " + adventure.name + " " + adventure.emoji
    } else {
        userViewModel.friends[adventure.user_id]?.name?.substringBefore(" ") + " is going to " + adventure.name + " " + adventure.emoji
    }

    Row(modifier = modifier) {
        ProfilePicture(url = userViewModel.friends[adventure.user_id]?.profile_picture, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        Column(Modifier.weight(1f)) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = adventureText, style = MaterialTheme.typography.body1, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Tap to chat!", style = MaterialTheme.typography.body2, color = Black40, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@Composable
fun StatusItem(status: ModifiedPlot, userViewModel: UserViewModel, modifier: Modifier = Modifier){
    Column(modifier = modifier.padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd){
            ProfilePicture(url = userViewModel.friends[status.user_id]?.profile_picture, modifier = Modifier.padding(8.dp))
            Text(text = status.emoji)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = userViewModel.friends[status.user_id]?.name?.substringBefore(" ") ?: "", style = MaterialTheme.typography.body2, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = status.name, style = MaterialTheme.typography.body2, color = Black40, maxLines = 1, overflow = TextOverflow.Ellipsis
        )


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
                    val plotToday = plots.firstOrNull{ isSameDay(it.starttime, date) && it.is_plot } != null


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
fun CalendarTitleBar2(navController: NavController, date: Date, selectedDate: Date, resetDate: () -> Unit, nextMonth: (Date) -> Unit, previousMonth: (Date) -> Unit, userViewModel: UserViewModel){

    val title = if (isSameDay(Date(), selectedDate) && date== beginningOfWeek()){ "Today" } else { SimpleDateFormat("MMM yyyy").format(if(selectedDate <= addDay(date, 6) && selectedDate >= date){selectedDate} else {date})}

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

        Text(text = title, style = MaterialTheme.typography.h1)

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


        if (!isSameDay(Date(), selectedDate) || date!=beginningOfWeek()) {
            IconButton(onClick = { resetDate() }, modifier = Modifier.size(58.dp)) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_calendar_event_line),
                    contentDescription = null,
                )
            }

        }
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