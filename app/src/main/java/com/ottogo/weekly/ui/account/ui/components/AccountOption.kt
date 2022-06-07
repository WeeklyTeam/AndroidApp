package com.ottogo.weekly.ui.account.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.viewmodels.UserViewModel


@Composable
fun AccountOption(icon: Int, text: String, onClick: () -> Unit) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(painter = painterResource(id = icon),
                contentDescription = "back",
                modifier = Modifier.size(56.dp).padding(16.dp)
            )

            Text(text = text, style = MaterialTheme.typography.body1)

        }



}
