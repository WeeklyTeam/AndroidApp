package com.ottogo.weekly.ui.calendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.ExtendedTheme

@Composable
fun FriendItem(profile: Profile, imgSize : Int, modifier: Modifier = Modifier) {

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width((screenWidth.minus(32.dp)).div(4.0f))
    ) {
        ProfilePicture(url = profile.profile_picture, size = imgSize)
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        Text(text = profile.name.substringBefore(" "), style = MaterialTheme.typography.body2, maxLines = 1)
        Spacer(modifier = Modifier.padding(bottom = 2.dp))
        Text(text = profile.username, style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60, maxLines = 1)
    }
}