package com.ottogo.weekly.ui.chat

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.Plot
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.WeeklyApiService
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.theme.Black60
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking
import retrofit2.http.Body
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
fun PlotEditPage(navController: NavController, userViewModel: UserViewModel) {

    PlotEditPageContent(navController)
    
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlotEditPageContent(navController: NavController) {

    val viewModel: PlotEditPageViewModel = viewModel()

    var titleInput by remember { mutableStateOf("") }
    var detailsInput by remember { mutableStateOf("") }


    Column(modifier = Modifier
        .fillMaxSize()) {

        Header(navController)

        Divider(color = LightGray, thickness = 1.dp)

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {

            Body(detailsInput = detailsInput, titleInput = titleInput,
                detailsChange = { detailsInput = it }, titleChange = { titleInput = it })

            CustomButton(buttonText = "Save",
                onClick = {
                    Log.d("status", "Start Time: " + viewModel.startTime)
                    Log.d("status", "Name: $titleInput")
                    Log.d("status", "Details: $detailsInput")
                    runBlocking {

                        WeeklyApi.retrofitService.editPlot(
                            // TODO: hook up emoji input to API call
                            mapOf("Authorization" to "token 265245769906872d88b40205147f5cbf63538b83"), 1,
                            Plot(viewModel.startTime, titleInput, "😃", detailsInput)
                        )
                    }
                })

        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Body(detailsInput: String, titleInput: String, detailsChange: (String) -> Unit, titleChange: (String) -> Unit) {

    val viewModel: PlotEditPageViewModel = viewModel()
    val dateTime = viewModel.dateTimeLiveData.observeAsState()

    Column(modifier = Modifier
        .fillMaxWidth()) {

        // TODO: Change "ProfilePicture" to an Emoji TextField
        ProfilePicture(profilePicture = "https://cdn.britannica.com/55/174255-050-526314B6/brown-Guernsey-cow.jpg")

        Spacer(modifier = Modifier.height(16.dp))

        Title(titleInput, titleChange)

        Spacer(modifier = Modifier.height(16.dp))

        Date(dateTime.value, viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        Details(detailsInput, detailsChange)
    }
}


@Composable
fun Title(titleInput: String, titleChange: (String) -> Unit) {
    Text(text = "Title", style = MaterialTheme.typography.h4)
    Spacer(modifier = Modifier.height(8.dp))
    CustomTextField(helper = "", hint = "What's the plan", input = titleInput, onChange = titleChange)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Date(dateTime: String?, viewModel: PlotEditPageViewModel) {
    Text(text = "Date", style = MaterialTheme.typography.h4)
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "$dateTime", style = MaterialTheme.typography.body2)
        ChangeDateButton(viewModel)
    }
}


@Composable
fun Details(detailsInput: String, detailsChange: (String) -> Unit) {
    Text(text = "Details", style = MaterialTheme.typography.h4)
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier
        .height(200.dp)
        .fillMaxWidth()
        .border(border = BorderStroke(1.dp, LightGray), shape = RoundedCornerShape(16.dp))) {
        TextField(modifier = Modifier.fillMaxSize(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
            value = detailsInput, onValueChange = detailsChange)
    }
}


@Composable
fun Header(navController: NavController) {

    Column() {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                ActionIconButton(R.drawable.ic_arrow_left_s_line) { navController.popBackStack() }
                Spacer(modifier = Modifier.width(15.dp))
                Text(text = "Edit", style = MaterialTheme.typography.h2)
            }

            ActionIconButton(R.drawable.ic_delete_bin_line) { /*TODO: Delete plot functionality*/ }

        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChangeDateButton(viewModel: PlotEditPageViewModel) {

    val context = LocalContext.current

            Button(colors = ButtonDefaults.buttonColors(backgroundColor = LightGray), shape = RoundedCornerShape(12.dp),
        elevation = null, contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        onClick = {
            viewModel.selectDateTime(context)
        }) {
        Text(text = "Change", style = MaterialTheme.typography.h4, color = Black60)
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