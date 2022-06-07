package com.ottogo.weekly.ui.login.ui.components

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.theme.nunitoFamily

/*
*
* Built by: Alondra
*
* Follow the figma file, note the spacing but keep in mind that the button will have a large tappable area
* there should be a clickable back button with 24dp padding all around
* you can find the icon on remixicons.com
* if you have any questions please feel free to reach out, try your best!
*
* */
@Composable
fun LoginTitle(navController: NavController, title: String) {



    Column(){

        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(72.dp)) {
            Icon(painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                contentDescription = "back",
                modifier = Modifier.size(24.dp)
            )
        }


        Text(text = title,
            fontSize = 42.sp,
            fontWeight = FontWeight(700),
            fontFamily = nunitoFamily,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}