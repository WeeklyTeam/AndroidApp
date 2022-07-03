package com.ottogo.weekly.ui.calendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.ProfilePicture

@Composable
fun FriendItem(profile: Profile, imgSize : Int) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfilePicture(url = profile.profile_picture)
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        Text(text = profile.name, style = MaterialTheme.typography.body2)
        Spacer(modifier = Modifier.padding(bottom = 2.dp))
        Text(text = profile.username, style = MaterialTheme.typography.body2)
    }
}