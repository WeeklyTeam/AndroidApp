package com.ottogo.weekly.ui.calendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.api.models.Profile
import kotlin.collections.ArrayList

@Composable
fun PlotMemberList(title: String, members: Iterable<Profile>) {
    val membersPerRow = 4
    var count = 0
    val rows : MutableList<MutableList<Profile>> = ArrayList()
    var rowMembers: MutableList<Profile> = ArrayList()

    Text(text = title, style = MaterialTheme.typography.body1, modifier = Modifier.padding(start = 16.dp))
    Spacer(modifier = Modifier.padding(bottom = 17.dp))

    // group the members into groups of 4
    for (member in members) {
        if (count % membersPerRow == 0) {
            rowMembers = ArrayList()
            rows.addAll(listOf(rowMembers))
            count = 0
        }
        rowMembers.add(member)
        count++
    }

    // display the groups in row layouts
    for (group in rows) {
        Spacer(modifier = Modifier.padding(bottom = 16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (member in group) {
                FriendItem(profile = member, imgSize = 60)
            }
        }
    }
}
