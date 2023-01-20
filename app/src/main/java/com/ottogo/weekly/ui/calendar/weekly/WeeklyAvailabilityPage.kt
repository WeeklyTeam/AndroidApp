package com.ottogo.weekly.ui.calendar.weekly

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.login.Loader
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.ui.theme.ExtendedTheme
import kotlinx.coroutines.selects.select

@Composable
fun WeeklyAvailabilityPage(navController: NavController) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val availabilityDates = listOf("M", "T", "W", "Th", "F", "S", "Su")

    var selectedList = remember{ mutableStateMapOf<Int, List<Int>>() }

    Column {

        TitleBar(navController = navController, title = "Weekly")
        Divider(color = ExtendedTheme.colors.LightGray, )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {


            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Availability",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h3,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            daysOfWeek.forEachIndexed {
                index, dow ->
                WeeklyAvailabilityItem(dow = dow, selected = (selectedList.containsKey(index)), options = selectedList[index] ?: listOf(), onSelect = { if (!selectedList.containsKey(index)) {
                    selectedList[index] = listOf(1,2,3)
                } else
                {
                    selectedList.remove(index)
                }}, onChange = { it ->
                    val value: MutableList<Int> = selectedList.remove(index)?.toMutableList() ?: mutableListOf()

                    if (it == 0){
                        if (value.count() != 3){
                            selectedList[index] = listOf(1,2,3)
                        }
                    } else {
                        if (value.count() == 3) {
                            value.clear()
                        }

                        if (!value.contains(it)) {

                            value.add(it)
                            value.sorted()
                        }else {
                            value.remove(it)
                        }
                        selectedList[index] = value


                    }
                })
            }


            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                buttonText = "Next",
                onClick = {
                    if (selectedList.count() > 0) {
                        var availability = ""
                        for (item in selectedList){
                            availability += availabilityDates[item.key] + item.value.joinToString(separator = "")
                        }
                        navController.navigate("weeklyActivityPage/${availability}")
                    } else {
                        navController.navigate("weeklyFailurePage")
                    }

                    },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))


        }
    }

}

@Composable
fun WeeklyAvailabilityItem(modifier: Modifier = Modifier, dow: String, selected: Boolean, options: List<Int>, onSelect: () -> Unit, onChange: (option: Int) -> Unit) {
    
    Column() {
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.clickable{
            onSelect()
        }, verticalAlignment = Alignment.CenterVertically) {
            Text(
                dow,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(start = 16.dp)
            )
            
            Spacer(Modifier.weight(1f))

            Icon(
                painter = painterResource(id = if (selected){ R.drawable.ic_checkbox_circle_fill } else { R.drawable.ic_checkbox_blank_circle_line }),
                tint = if (selected){ ExtendedTheme.colors.DarkGreen } else { ExtendedTheme.colors.Black60 },
                contentDescription = "checkbox",
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        if (selected) {
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.width(8.dp))

                listOf("All day", "Morning", "Afternoon", "Evening").forEachIndexed { index, text ->
                        Chip(text,
                            isSelected = if (options.count() == 3) { index == 0 } else {options.contains(index)},
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    onChange(index)
                                })

                    }
                Spacer(modifier = Modifier.width(8.dp))

            }
        }   


        Spacer(modifier = Modifier.height(8.dp))

    }
}


@Composable
fun Chip(text: String, isSelected: Boolean = false, modifier: Modifier = Modifier){
    Row(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    ExtendedTheme.colors.LightGreen
                } else {
                    ExtendedTheme.colors.LightGray
                }
            )
            .height(40.dp)
            .padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {


        Text(
            text = text,
            style = MaterialTheme.typography.h4,
            color = if (isSelected) { ExtendedTheme.colors.DarkGreen } else { ExtendedTheme.colors.Black60 }
        )
    }


}
