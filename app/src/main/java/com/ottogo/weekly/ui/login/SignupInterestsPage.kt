package com.ottogo.weekly.ui.login



import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.nunitoFamily
import kotlinx.coroutines.runBlocking

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.R
import com.ottogo.weekly.api.Activity
import com.ottogo.weekly.api.ActivityCategory
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.launch

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

//https://stackoverflow.com/questions/70487159/compose-mutablestate-from-viewmodel
class ActivityCategoriesViewModel(private val token: String) : ViewModel() {
    private val _activityCategories = mutableStateListOf<ActivityCategory>()
    val activityCategories: List<ActivityCategory> = _activityCategories
    var numberLiked by mutableStateOf(0)


    init {

        runBlocking {
            _activityCategories.addAll(WeeklyApi.retrofitService.activity(mapOf("Authorization" to "token $token")))
        }

    }


    fun favorite(activityCategoryIndex: Int, activityIndex: Int) {
        val activityId = activityCategories[activityCategoryIndex].activities[activityIndex].id
        runBlocking {
            WeeklyApi.retrofitService.favoriteActivity(mapOf("Authorization" to "token $token"), activityId)
            updateActivityCategories(activityCategoryIndex, activityIndex, true)
            numberLiked++

        }

    }

    fun unfavorite(activityCategoryIndex: Int, activityIndex: Int) {
        val activityId = activityCategories[activityCategoryIndex].activities[activityIndex].id

        runBlocking {
            WeeklyApi.retrofitService.unfavoriteActivity(mapOf("Authorization" to "token $token"), activityId)
            updateActivityCategories(activityCategoryIndex, activityIndex, false)
            numberLiked--
        }
    }



    private fun updateActivityCategories(activityCategoryIndex: Int, activityIndex: Int, like: Boolean){
        _activityCategories[activityCategoryIndex] = _activityCategories[activityCategoryIndex].let {
            var activity = _activityCategories[activityCategoryIndex].activities[activityIndex]
            activity = activity.let { it.copy(liked = like) }
            val activities = _activityCategories[activityCategoryIndex].activities.toMutableList()
            activities[activityIndex] = activity
            it.copy(activities = activities)
        }
    }
}

@Composable
fun SignupInterestsPage(navController: NavController, token: String, userViewModel: UserViewModel) {
    SignupInterestsScreen(navController, token, ActivityCategoriesViewModel(token), userViewModel)
}



@Composable
fun SignupInterestsScreen(navController: NavController, token: String, activityCategoriesViewModel: ActivityCategoriesViewModel, userViewModel: UserViewModel) {


    Column(modifier =  Modifier.systemBarsPadding()) {

        LoginTitle(navController = navController, title = "Interests")
        Spacer(modifier = Modifier.height(8.dp))

        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.padding(bottom = 24.dp)) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
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
                Spacer(Modifier.height(32.dp))


                for (activityCategoryIndex in 0 until activityCategoriesViewModel.activityCategories.size) {
                    ActivityList(activityCategoryIndex, activityCategoriesViewModel)
                }

                Spacer(Modifier.height(60.dp))


            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RectangleShape)
                    .background(Color.White)
            )





            Box(Modifier.padding(horizontal = 24.dp)) {
                Button(
                    onClick = {
                        if (activityCategoriesViewModel.numberLiked >= 3) {
                            userViewModel.token = token
                        }

                    },
                    Modifier.fillMaxWidth().height(48.dp).clip(CircleShape)

                ) {
                    Text(
                        text = "Finish (${activityCategoriesViewModel.numberLiked}/3)",
                        style = MaterialTheme.typography.h4
                    )

                }
            }


        }


    }
}

@Composable
fun ActivityList(activityCategoryIndex: Int, activityCategoriesViewModel: ActivityCategoriesViewModel) {
    val activityCategory = activityCategoriesViewModel.activityCategories[activityCategoryIndex]


    Column {

        Text(
            text = activityCategory.title,
            fontFamily = nunitoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(16.dp))


        val numRows = if (activityCategory.activities.size % 2 == 0) {
            activityCategory.activities.size / 2
        } else {
            activityCategory.activities.size / 2 + 1
        }

        for (row in 0 until numRows) {
            val firstIndex = row * 2
            var secondIndex = firstIndex + 1

            if (firstIndex == activityCategory.activities.size) {
                secondIndex = -1
            }


            GridItems(firstIndex, secondIndex, activityCategoryIndex, activityCategoriesViewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))


    }
}


@Composable
fun GridItems(firstActivityIndex: Int, secondActivityIndex: Int, activityCategoryIndex: Int, activityCategoriesViewModel: ActivityCategoriesViewModel) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ActivityCol(activityCategoryIndex, firstActivityIndex, activityCategoriesViewModel, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(32.dp))

        if (secondActivityIndex != -1) {
            ActivityCol(activityCategoryIndex, secondActivityIndex, activityCategoriesViewModel, modifier = Modifier.weight(1f))
        } else {
            Row(modifier = Modifier.weight(1f)) {}
        }
    }
}


@Composable
fun ActivityCol(categoryIndex: Int, activityIndex: Int, activityCategoriesViewModel: ActivityCategoriesViewModel, modifier: Modifier = Modifier) {
    val activity = activityCategoriesViewModel.activityCategories[categoryIndex].activities[activityIndex]
    Row(
        modifier = modifier
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
                if (!activity.liked) {
                    activityCategoriesViewModel.favorite(categoryIndex, activityIndex)
                } else {
                    activityCategoriesViewModel.unfavorite(categoryIndex, activityIndex)
                }

            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
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

        if (activity.liked) {
            HeartIcon()
        }

    }
}


@Composable
fun HeartIcon() {
    Icon(
        painter = painterResource(R.drawable.ic_heart_fill),
        contentDescription = "",
        tint = ExtendedTheme.colors.LoveRed,
        modifier = Modifier
            .padding(end = 16.dp)
    )
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