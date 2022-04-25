package com.ottogo.weekly.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun AvailabilityPage (navController: NavController, userViewModel: UserViewModel) {
    Column() {
        AvailabilityPageTitleBar(navController)
        Cal
    }
}

@Composable
fun AvailabilityPageTitleBar (navController: NavController) {
    Row(Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                    contentDescription = null,
                )
            }
        }

        Text(text = "Availability",
            Modifier
                .padding(top = 13.dp, bottom = 13.dp)
                .align(Alignment.CenterVertically), style = MaterialTheme.typography.h1)

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_add_line),
                    contentDescription = null,
                )
            }
        }
    }
}