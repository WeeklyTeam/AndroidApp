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

    var activityCategories = remember { mutableStateListOf<ActivityCategory>() }
    runBlocking {
        activityCategories.addAll(WeeklyApi.retrofitService.activity(mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7")))
    }

    LazyColumn(modifier = Modifier
        .background(color = Color.White)
        .padding(24.dp)) {

        item() {
            HeaderStuff()
        }


        for (activityCategory in activityCategories) {
            item() {
                Text(
                    text = activityCategory.title,
                    fontFamily = nunitoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }

            gridItems(
                data = activityCategory.activities,
                columnCount = 2,
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) { activity ->

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .shadow(12.dp, shape = RoundedCornerShape(12.dp))
                        .background(color = Color(0xFFF0EAFF))
                        .height(46.dp)
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
        }

        item {
            FooterStuff()
        }
    }
}

@Composable
fun HeaderStuff() {
    Text(
        text = "Interests",
        fontFamily = nunitoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp
    )
    Spacer(modifier = Modifier.height(32.dp))
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
}

@Composable
fun FooterStuff() {
    Spacer(modifier = Modifier.height(48.dp))
    CustomButton(buttonText = "Finish", onClick = {})
}

// Extends
fun <T> LazyListScope.gridItems(
    data: List<T>,
    columnCount: Int,
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val size = data.count()
    val rows = if (size == 0) 0 else 1 + (size - 1) / columnCount
    items(rows) { rowIndex ->
        Row(
            horizontalArrangement = horizontalArrangement,
            modifier = modifier
        ) {
            for (columnIndex in 0 until columnCount) {
                val itemIndex = rowIndex * columnCount + columnIndex
                if (itemIndex < size) {
                    Box(
                        modifier = Modifier.weight(1F, fill = true),
                        propagateMinConstraints = true
                    ) {
                        itemContent(data[itemIndex])
                    }
                } else {
                    Spacer(Modifier.weight(1F, fill = true))
                }
            }
        }
    }
}

// placing a Lazygrid inside a Column
// another way to create a grid?
// using FlowRow or GridItems?
// using grid items lead to Key 0 already in use (this was because grid items
// extension created a key based on 'it.hashcode' (which probably generated the same key multiple
// times).

/* test data

var testDataCategory = listOf(
    TestDataCategory("Title 1", listOf(TestData(1, "Basketball"), TestData(2, "Soccer"), TestData(3, "Volleyball"))),
    TestDataCategory("Title 2", listOf(TestData(3, "Minecraft")))
) */

/* test data classes
data class TestData (var id: Int, var activity: String)
data class TestDataCategory(var title:String, var testDatas: List<TestData>) */

