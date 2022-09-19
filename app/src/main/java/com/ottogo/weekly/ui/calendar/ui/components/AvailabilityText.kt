package com.ottogo.weekly.ui.calendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Availability
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.calendar.DateFunctions.addDay
import com.ottogo.weekly.ui.components.ProfilePicture
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@Composable
fun AvailabilityText(availability: Availability, profile: Profile? = null, onDelete: (suspend (availability: Availability) -> Unit)? = null, modifier: Modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)) {
    val formatter = SimpleDateFormat("h:mma")
    val availabilityText = if (availability.title.isNullOrEmpty()){
        if (profile == null) {
            "Something"
        } else {
            profile.name + " is busy"
        }
    } else {
        if (profile == null) {
            availability.title
        } else {
            profile.name + " has " + availability.title
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val availabilityTimeText = if (availability.starttime != addDay(availability.endtime, 1)) {
        formatter.format(availability.starttime) + " - " + formatter.format(availability.endtime)
    } else {
        "All of today"
    }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column() {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (profile != null){
                    ProfilePicture(url = profile.profile_picture, size = 20)
                    Spacer(Modifier.width(8.dp))
                }

                Text(availabilityText, style = MaterialTheme.typography.h5, modifier = Modifier.heightIn(min = 20.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(availabilityTimeText, style = MaterialTheme.typography.body2)

        }

        if (onDelete != null) {
            IconButton(onClick = {
                coroutineScope.launch {
                    onDelete(availability)
                }
            }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_delete_bin_line),
                    contentDescription = null,
                )
            }
        }


    }

}