package com.ottogo.weekly.ui.account

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.WeeklyApiService
import com.ottogo.weekly.api.models.Holiday
import com.ottogo.weekly.api.models.HolidayCategory
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.SelectableOption
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.Typography
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun HolidaysPage(navController: NavController, userViewModel: UserViewModel) {
    val holidayCategories = remember { mutableStateListOf<HolidayCategory>() }
    val activeHolidays = remember { mutableStateListOf<Int>() }

    LaunchedEffect(key1 = 1) {
        WeeklyApi.retrofitService.holidays(mapOf("Authorization" to "token ${userViewModel.token}")).forEach { holidayCategory ->
            holidayCategories.add(holidayCategory)
            holidayCategory.holidays.forEach { holiday ->
                if (holiday.active) {
                    activeHolidays.add(holiday.id)
                }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .scrollable(rememberScrollState(0), orientation = Orientation.Vertical)) {
        TitleBar(navController = navController, title = "Holidays")
        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray, modifier = Modifier.padding(bottom = 5.dp))

        holidayCategories.forEach {
            HolidayCategoryList(holidayCategoryName = it.holiday_category_name,
                holidays = it.holidays,
                onClick = { id ->
                          if (id in activeHolidays) {
                              activeHolidays.remove(id)
                          } else {
                              activeHolidays.add(id)
                          }
                },
                activeHolidays = activeHolidays,
                userViewModel = userViewModel)
        }
    }
}

@Composable
fun HolidayCategoryList(holidayCategoryName: String, holidays: List<Holiday>, activeHolidays: List<Int>, onClick: (Int) -> Unit, userViewModel: UserViewModel) {
    Text(holidayCategoryName,
        fontFamily = nunitoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 19.dp, bottom = 16.dp))
    holidays.forEach {
        HolidayListItem(holiday = Holiday(it.id, it.holiday_name, it.emoji, date=it.date, active=it.active), it.id in activeHolidays, onClick = onClick, userViewModel = userViewModel)
    }
}

@Composable
fun HolidayListItem(holiday: Holiday, isSelected: Boolean, onClick: (Int) -> Unit, userViewModel: UserViewModel) {

    SelectableOption(optionText = holiday.holiday_name, isSelected = isSelected, modifier = Modifier.clickable {
        onClick(holiday.id)
        if (isSelected) {
            runBlocking {
                WeeklyApi.retrofitService.deactivateHoliday(
                    mapOf("Authorization" to "token ${userViewModel.token}"),
                    holiday.id
                )
            }
        } else {
            runBlocking {
                WeeklyApi.retrofitService.activateHoliday(
                    mapOf("Authorization" to "token ${userViewModel.token}"),
                    holiday.id
                )
            }
        }
    })
}