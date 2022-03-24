package com.ottogo.weekly.ui.login

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.Activity
import com.ottogo.weekly.api.ActivityCategory
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.nunitoFamily
import kotlinx.coroutines.runBlocking

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

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(24.dp)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Header()

            for (activityCategory in activityCategories) {
                ActivityList(activityCategory)
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

    Column() {
        var numRows = 0

        numRows = if (activityCategory.activities.size % 2 == 0) {
            activityCategory.activities.size / 2
        } else {
            activityCategory.activities.size / 2 + 1
        }

        for (row in 0 until numRows) {
            var firstIndex = row * 2
            var secondIndex = firstIndex + 1
            var firstColActivity = activityCategory.activities[firstIndex]
            var secondColActivity: Activity? = null
                if (secondIndex == activityCategory.activities.size) {
                    Spacer(modifier = Modifier.fillMaxWidth())
                }
                else {
                    secondColActivity = activityCategory.activities[secondIndex]
                }
            GridItems(firstColActivity, secondColActivity)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GridItems(firstColActivity: Activity, secondColActivity: Activity? = null) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .weight(1f)
                .coloredShadow(
                    color = Color(0xFF000000),
                    alpha = 0.05f,
                    offsetX = 3.dp,
                    offsetY = 5.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .shadow(12.dp, shape = RoundedCornerShape(12.dp))
                .background(color = Color(0xFFFFFFFF))
                .height(46.dp)
                .clickable {

                },
        ) {
            Text(
                text = firstColActivity.activity,
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .height(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        if (secondColActivity != null) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .coloredShadow(
                        color = Color(0xFF000000),
                        alpha = 0.05f,
                        offsetX = 3.dp,
                        offsetY = 5.dp
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(12.dp, shape = RoundedCornerShape(12.dp))
                    .background(color = Color(0xFFFFFFFF))
                    .height(46.dp)
                    .clickable {

                    },
            ) {
                Text(
                    text = secondColActivity.activity,
                    fontFamily = nunitoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .height(22.dp)
                )
            }
        } else {
            Row(modifier = Modifier.weight(1f)) {}
        }
    }
}

@Composable
fun Header() {
    Spacer(modifier = Modifier.height(65.dp))
    Image(painter = painterResource(id = R.drawable.ic_arrow_left_s_line), contentDescription = "back arrow")
    Spacer(modifier = Modifier.height(30.dp))
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

// Shadow with more customizability
fun Modifier.coloredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = composed {

    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha= 0f).toArgb()

    this.drawBehind {

        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = transparent

            frameworkPaint.setShadowLayer(
                shadowRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor
            )
            it.drawRoundRect(
                0f,
                0f,
                this.size.width,
                this.size.height,
                borderRadius.toPx(),
                borderRadius.toPx(),
                paint
            )
        }
    }
}
