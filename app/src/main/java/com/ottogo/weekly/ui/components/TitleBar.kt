package com.ottogo.weekly.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.theme.nunitoFamily

@Composable
fun TitleBar(navController: NavController, title: String, modifier: Modifier = Modifier, iconButtons: @Composable () -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier){
        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(56.dp)) {
            Icon(painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                contentDescription = "back",
                modifier = Modifier.size(24.dp)
            )
        }

        Text(text = title,
            style = MaterialTheme.typography.h2
        )

        Spacer(modifier = Modifier.weight(1F))

        iconButtons()
    }
}