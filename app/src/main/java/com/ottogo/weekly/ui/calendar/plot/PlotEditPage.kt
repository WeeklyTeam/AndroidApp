package com.ottogo.weekly.ui.calendar.plot

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.Black60
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class PlotEditPageViewModel : ViewModel() {

    val dateTimeLiveData: LiveData<String>
        get() = dateTime

    private var dateTime = MutableLiveData<String>("")

    var startTime = "";

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectDateTime(context: Context) {
        val calendar: Calendar = Calendar.getInstance()
        val date: Date

        var time: String

        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val hour: Int = calendar.get(Calendar.HOUR)
        var minute: Int = calendar.get(Calendar.MINUTE)



        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, day: Int ->

            TimePickerDialog(context, { _:TimePicker, hour: Int, minute: Int ->

                calendar.set(year, month, day, hour, minute)

                time = "${monthNames[month]}. $day, $year at ${calculateTime(hour, minute)}"
                updateDateTime(time)

                // "2020-10-17T19:53:13-07:00" iso 8601
                var theDate = ZonedDateTime.of(LocalDateTime.of(year, month+1, day, hour, minute), ZoneId.systemDefault())
                var dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                startTime = dateFormat.format(theDate)

            }, hour, minute, false).show()

        }, year, month, day).show()

    }


    private fun calculateTime(hour: Int, minute: Int): String {
        var minuteString = minute.toString()
        if (minute < 10) {
            minuteString = "0$minute"
        }
        return when {
            hour == 0 -> {
                "12:$minuteString am"
            }
            hour < 12 -> {
                "$hour:$minuteString am"
            }
            hour == 12 -> {
                "12:$minuteString pm"
            }
            else -> {
                "${hour-12}:$minuteString pm"
            }
        }
    }

    private fun updateDateTime(time: String) {
        dateTime.value = time
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlotEditPage(navController: NavController, plotId: Int, userViewModel: UserViewModel, openEmoji: () -> Unit, closeSheet: () -> Unit, bottomSheetViewModel: BottomSheetViewModel) {

    PlotEditPageContent(navController, plotId = plotId, userViewModel = userViewModel, openEmoji, closeSheet, bottomSheetViewModel)

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlotEditPageContent(navController: NavController, plotId: Int, userViewModel: UserViewModel, openEmoji: () -> Unit, closeSheet: () -> Unit, bottomSheetViewModel: BottomSheetViewModel) {

    val viewModel: PlotEditPageViewModel = viewModel()
    var emoji by remember { mutableStateOf(userViewModel.plots.firstOrNull { it.id == plotId }?.emoji) }

    var titleInput by remember { mutableStateOf(userViewModel.plots.firstOrNull { it.id == plotId }?.name ?: "") }
    var detailsInput by remember { mutableStateOf(userViewModel.plots.firstOrNull { it.id == plotId }?.description ?: "") }

    var starttime by remember { mutableStateOf(userViewModel.plots.firstOrNull { it.id == plotId }?.starttime ?: Date()) }

    var coroutine = rememberCoroutineScope()

    var saved by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = bottomSheetViewModel.plotEmoji, block = {
        if (!bottomSheetViewModel.plotEmoji.isNullOrEmpty()){
            emoji = bottomSheetViewModel.plotEmoji ?: ""
            closeSheet()
        }
    })
    LaunchedEffect(key1 = titleInput, block =
    {
        if(titleInput.contains("[^A-Za-z0-9 ]".toRegex())){
            emoji = titleInput.replace("[A-Za-z0-9 ]".toRegex(), "")
        }
    })

    Column(modifier = Modifier
        .fillMaxSize()) {

        TitleBar(navController = navController, title = "Edit", iconButtons = {
            IconButton(onClick = {
                coroutine.launch {
                    WeeklyApi.retrofitService.deletePlot(
                        mapOf("Authorization" to "token ${userViewModel.token}"),
                        plotId
                    )
                    userViewModel.removePlot(plotId = plotId)
                    navController.popBackStack("homePage", inclusive = false)
                }

            }, modifier = Modifier.size(56.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_delete_bin_line),
                    contentDescription = null,
                )
            }
        })



        Divider(color = LightGray, thickness = 1.dp)



        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)) {

            if (saved){
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Saved!", color = ExtendedTheme.colors.Green, style = MaterialTheme.typography.h5, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))


            if (!emoji.isNullOrEmpty()){
                EmojiCircle(emoji = emoji!!, Modifier.size(72.dp), onClick = {openEmoji()})
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(helper = "Title", hint = "What's the plan?", input = titleInput.replace("[^A-Za-z0-9 ]".toRegex(), ""), onChange = { titleInput = it })

            Spacer(modifier = Modifier.height(16.dp))

            Date(starttime, {starttime = it})

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Details", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .border(border = BorderStroke(1.dp, LightGray), shape = RoundedCornerShape(16.dp))) {
                TextField(modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    value = detailsInput, onValueChange = { detailsInput = it })
            }

            Spacer(modifier = Modifier.height(16.dp))


            CustomButton(buttonText = "Save",
                    onClick = {
                        Log.d("status", "Start Time: " + viewModel.startTime)
                        Log.d("status", "Name: $titleInput")
                        Log.d("status", "Details: $detailsInput")
                        val body: MutableMap<String, Any> = mutableMapOf()
                        body["name"] = titleInput.replace("[^A-Za-z0-9 ]".toRegex(), "")
                        body["description"] = detailsInput
                        body["starttime"] = starttime
                        body["emoji"] = emoji ?: ""


                        WeeklyApi.retrofitService.patchPlot(
                            mapOf("Authorization" to "token ${userViewModel.token}"), plotId,
                            body
                        )
                        saved = true
                        val tempPlot = userViewModel.plots[userViewModel.plots.indexOfFirst { plotId == it.id }]
                        userViewModel.updatePlot(plotId, tempPlot.copy(name = titleInput, emoji = emoji ?: "", description = detailsInput, starttime = starttime))


                    })
            Spacer(modifier = Modifier.height(24.dp))



        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Date(date: Date, setTime: (Date) -> Unit) {
    val context = LocalContext.current

    val calendar = Calendar.getInstance()


    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
    val hour: Int = calendar.get(Calendar.HOUR)
    var minute: Int = calendar.get(Calendar.MINUTE)

    Text(text = "Date", style = MaterialTheme.typography.h4)
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = SimpleDateFormat("EEEE, MMM d").format(date)+" at "+SimpleDateFormat("h:mm a").format(date), style = MaterialTheme.typography.body2)



        Button(colors = ButtonDefaults.buttonColors(backgroundColor = LightGray), shape = RoundedCornerShape(12.dp),
            elevation = null, contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            onClick = {
                DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, day: Int ->

                    TimePickerDialog(context, { _:TimePicker, hour: Int, minute: Int ->
                        val calendar = Calendar.getInstance()

                        calendar.set(year, month, day, hour, minute)

                        setTime(calendar.time)

                    }, hour, minute, false).show()

                }, year, month, day).show()
            }) {
            Text(text = "Change", style = MaterialTheme.typography.h4, color = Black60)
        }
    }
}




@Composable
fun ActionIconButton(resourceId: Int, onClick: () -> Unit) {

    IconButton(onClick = onClick ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(id = resourceId),
            contentDescription = "back arrow",
        )
    }

}