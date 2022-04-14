package com.ottogo.weekly.ui.chat

import android.hardware.lights.Light
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun SearchPage(navController: NavController, userViewModel: UserViewModel) {
    Column(modifier = Modifier.padding(16.dp)){
        var searchText by remember { mutableStateOf("") }
        Row(verticalAlignment = Alignment.CenterVertically) {

            Surface(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = searchText,
                    onValueChange = {  searchText = it },
                    modifier = Modifier.height(40.dp),
                    textStyle = MaterialTheme.typography.body2.copy(color = Black60),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Row(
                            Modifier.background(LightGray, RoundedCornerShape(12.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(painter = painterResource(R.drawable.ic_search_line),
                                contentDescription = null,
                                tint = Black60,
                                modifier = Modifier.padding(10.dp))
                            if (searchText.isEmpty()) Text(text = "Search", style = MaterialTheme.typography.body2, color = Black60)
                            innerTextField()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = null, contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                onClick = {
                    navController.popBackStack()
                }) {
                Text(text = "Cancel", style = MaterialTheme.typography.h4, color = Black60)
            }


        }
        SearchResults()
    }
}

@Composable
fun SearchResults() {
    Column() {

    }
}
