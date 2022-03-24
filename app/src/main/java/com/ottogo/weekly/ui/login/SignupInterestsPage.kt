package com.ottogo.weekly.ui.login

import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ottogo.weekly.api.Activity
import com.ottogo.weekly.api.ActivityCategory
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.nunitoFamily
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KProperty

/*
*
* Built by: Napoleon
*
* Send a get request to the interests url
* when the user taps on an interest send a request to like the interest
* when the response is successful add a red heart to the item
* keep track of how many interests the user likes
* set the user view model token to the token recieved
* Try your best! Please don't hesitate to ask any questions
*
* */

@Preview
@Composable
fun SignupInterestsPage(navController: NavController = rememberNavController(), token: String = "hello") {

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(24.dp)) {
        Text(
            text = "Interests",
            fontFamily = nunitoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 42.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = Color(0xFFF0EAFF))
                    .padding(16.dp)
            ) {
                Text(
                    text = "What do you like to do? (Minimum 3)",
                    color = Color(0xFF4C21C2),
                    fontFamily = nunitoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }

            var activities = remember { mutableStateListOf<ActivityCategory>() }
            runBlocking {
                activities.addAll(WeeklyApi.retrofitService.activity(mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7")))
            }

            for (element in activities) {
                ActivityList(element)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        CustomButton(buttonText = "Finish", onClick = {})
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityList(activityCategory: ActivityCategory) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        text = activityCategory.title,
        fontFamily = nunitoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )
    Spacer(modifier = Modifier.height(16.dp))

    var checked by remember { mutableStateOf(activityCategory.activities) }

    Column() {
        var numColumns = 0
        if (activityCategory.activities.size % 2 == 0) {
            numColumns = activityCategory.activities.size / 2
        }
        else {
            numColumns = activityCategory.activities.size / 2 + 1
        }
        for (column in 0..(numColumns-1)) {
            var firstIndex = column * 2
            var secondIndex = firstIndex + 1
            Row(modifier = Modifier.fillMaxWidth()) {
                GridItems(activityCategory.activities[firstIndex])
                if (secondIndex == activityCategory.activities.size) {
                    Spacer(modifier = Modifier.fillMaxWidth())
                }
                else {
                    GridItems(activityCategory.activities[secondIndex])
                }
            }
        }
    }
}

@Composable
fun GridItems(activity: Activity) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .shadow(12.dp, shape = RoundedCornerShape(12.dp))
            .background(color = Color(0xFFF0EAFF))
            .height(46.dp)
            .fillMaxWidth()
            .clickable {

            },
        horizontalArrangement = Arrangement.spacedBy(38.dp)
    ) {
        Text(
            text = activity.activity,
            fontFamily = nunitoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(10.dp)
                .height(22.dp)
        )
    }
}

// placing a Lazygrid inside a Column
// another way to create a grid?
// using FlowRow or GridItems?

/* test data

var testDataCategory = listOf(
    TestDataCategory("Title 1", listOf(TestData(1, "Basketball"), TestData(2, "Soccer"), TestData(3, "Volleyball"))),
    TestDataCategory("Title 2", listOf(TestData(3, "Minecraft")))
) */

/* test data classes
data class TestData (var id: Int, var activity: String)
data class TestDataCategory(var title:String, var testDatas: List<TestData>) */
