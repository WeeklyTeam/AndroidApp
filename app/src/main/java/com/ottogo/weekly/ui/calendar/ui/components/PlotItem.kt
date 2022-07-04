package com.ottogo.weekly.ui.calendar.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.login.coloredShadow
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.nunitoFamily
import com.ottogo.weekly.viewmodels.UserViewModel
import java.text.SimpleDateFormat

@Composable
fun PlotItem(plot: Plot, userViewModel: UserViewModel, onClick: () -> Unit) {
    val date = SimpleDateFormat("EEEE, MMM dd").format(plot.starttime)
    val time = SimpleDateFormat("h:mm a").format(plot.starttime)

    Column() {

        Spacer(modifier = Modifier.height(16.dp))

        Text(date, style = MaterialTheme.typography.h5,
            color = ExtendedTheme.colors.Black40, modifier = Modifier.padding(horizontal = 16.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .coloredShadow(
                    color = Color(0xFFB3B3B3),
                    alpha = .2F,
                    offsetX = 3.dp,
                    offsetY = 5.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onClick()
                }
                .background(MaterialTheme.colors.background)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                
                EmojiCircle(emoji = plot.emoji)

                Spacer(modifier = Modifier.width(16.dp))

                Column() {
                    Text(plot.name, style = MaterialTheme.typography.h3)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(time, style = MaterialTheme.typography.body2,
                        color = ExtendedTheme.colors.Black60)
                }
            }
            Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)
            if (!plot.is_going) {
                Text("Are you going?", style = MaterialTheme.typography.h5, color = ExtendedTheme.colors.Black60, modifier = Modifier.padding(16.dp))

                Row(
                    Modifier
                        .width(IntrinsicSize.Min)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)) {
                    CustomButton(buttonText = "No", onClick = { userViewModel.rejectPlotInvite(plot) }, modifier = Modifier.weight(1F))
                    Spacer(modifier = Modifier.width(16.dp))
                    CustomButton(buttonText = "Yes", onClick = { userViewModel.acceptPlotInvite(plot) }, modifier = Modifier.weight(1F))

                }
            }
        }


    }
}