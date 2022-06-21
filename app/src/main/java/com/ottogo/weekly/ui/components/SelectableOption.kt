package com.ottogo.weekly.ui.components

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.Typography
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun SelectableOption(
    optionText: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,

    ) {

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier
        .fillMaxWidth()
        ) {
        Icon(painter = painterResource(id =
        if(isSelected){
            R.drawable.ic_checkbox_circle_fill
        } else {
            R.drawable.ic_checkbox_blank_circle_line
        }), contentDescription = "Checkbox",
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .height(24.dp)
                .width(24.dp),
            tint =
            if(isSelected){
                MaterialTheme.colors.primary
            } else {
                ExtendedTheme.colors.Black60
            }
        )

        Text(text = optionText, style = MaterialTheme.typography.body1)
    }
}