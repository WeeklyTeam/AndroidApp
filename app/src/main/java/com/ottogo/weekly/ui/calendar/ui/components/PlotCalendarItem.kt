package com.ottogo.weekly.ui.calendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.login.coloredShadow
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import java.text.SimpleDateFormat

@Composable
fun PlotCalendarItem(plot: Plot, userViewModel: UserViewModel, onClick: () -> Unit) {

    val time = if (plot.starttime != null){
        SimpleDateFormat("h:mma").format(plot.starttime)

    } else {
        "Date undecided"
    }


    Column(modifier = Modifier.clickable { onClick() }.fillMaxWidth()) {


        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

            EmojiCircle(emoji = plot.emoji)

            Spacer(modifier = Modifier.width(16.dp))

            Column() {
                Text(plot.name, style = MaterialTheme.typography.h3)
                Spacer(modifier = Modifier.height(8.dp))
                Text(time, style = MaterialTheme.typography.body2,
                    color = ExtendedTheme.colors.Black60)
            }
        }

    }
}