package com.ottogo.weekly.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.viewmodels.UserViewModel


//NOTE: I don't know how to remove underscore for text fields. I also don't know how to make the API
// request.

@Composable
fun AddAvailabilityPage (navController: NavController, userViewModel: UserViewModel) {
    var selected by remember { mutableStateOf("") }
    var activityName by remember { mutableStateOf("") }
    var timeRange by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(top = 44.dp)
    ) {
        AddAvailabilityPageTitleBar(navController)
        activityName = activityNameTextBox()
        timeRange = timeRangeTextBox()
        Spacer(modifier = Modifier.padding(vertical = 16.dp))
        selected = radioGroup()
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            CustomButton(
                buttonText = "Add",
                onClick = {
                    // add API call here
                } )
        }
    }
}


@Composable
fun timeRangeTextBox(): String {
    var timeRange by remember { mutableStateOf("") }

    Text(
        modifier = Modifier.padding(start = 19.dp, top = 33.dp),
        text = "From",
        style = MaterialTheme.typography.h3)
    TextField(
        value = timeRange,
        onValueChange = { timeRange = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 16.dp, start = 16.dp, end = 65.dp),
        singleLine = true)

    return timeRange
}


@Composable
fun radioGroup(): String {
    var selected by remember { mutableStateOf("Busy") }

    Column (modifier = Modifier.padding(start = 16.dp)) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = selected == "Busy",
                onClick = { selected = "Busy"})
            Text(text = "Busy",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clickable(onClick = { selected = "Busy" }),
                style = MaterialTheme.typography.body2)
        }
        Spacer(modifier = Modifier.padding(bottom = 16.dp))
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = selected == "Free",
                onClick = { selected = "Free"})
            Text(text = "Free",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clickable(onClick = { selected = "Free" }),
                style = MaterialTheme.typography.body2)
        }
    }
    return selected
}


@Composable
fun activityNameTextBox(): String {
    var name : String by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(text = "Name (Optional)", style = MaterialTheme.typography.h3)
        TextField(
            value = name,
            onValueChange = { name = it },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(CircleShape)
                .padding(top = 8.dp),
            singleLine = true
            )
    }

    return name
}


@Composable
fun AddAvailabilityPageTitleBar (navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.size(height = 56.dp, width = 375.dp).padding(bottom = 25.dp)
    ) {
        IconButton(onClick = {
            navController.navigate("availabilityPage/")
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                contentDescription = null)
        }

        Text(text = "Add Availability",
            modifier = Modifier.padding(),
            style = MaterialTheme.typography.h2)

    }
}