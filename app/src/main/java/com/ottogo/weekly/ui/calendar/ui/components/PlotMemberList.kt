package com.ottogo.weekly.ui.calendar.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.theme.ExtendedTheme
import kotlin.collections.ArrayList
import kotlin.math.ceil

@Composable
fun PlotMemberList(title: String, members: List<Profile>, openSheet: (profile: Profile?) -> Unit, modifier: Modifier = Modifier) {
    val membersPerRow = 4
    var count = 0

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    Log.d("size", (screenWidth.minus(32.dp)).div(4).toString())
    Log.d("size", (screenWidth.minus(32.dp)).toString())

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.h5, color = ExtendedTheme.colors.Black60)

            // display the groups in row layouts
            for (firstIndex in 1..(ceil(members.count()/4.0).toInt())) {
                val toIndex = if ((firstIndex-1)*4+4 <= members.count()) { (firstIndex-1)*4+4 } else { (firstIndex-1)*4+(members.count()%4) }

                val subList = members.subList(fromIndex = (firstIndex-1)*4, toIndex = toIndex)

                Log.d("index", subList.toString())

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.width((screenWidth.minus(32.dp)).div(subList.count()/4.0f)),
                ) {


                    for (member in subList) {
                        FriendItem(profile = member, imgSize = 60, modifier = Modifier.clickable{
                            openSheet(member)
                        })
                    }
                }
            }
        }
    }

}