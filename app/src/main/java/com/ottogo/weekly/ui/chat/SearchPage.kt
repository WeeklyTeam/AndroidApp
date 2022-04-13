package com.ottogo.weekly.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun SearchPage(userViewModel: UserViewModel) {
    Column(modifier = Modifier.padding(16.dp)){
        var searchText by remember { mutableStateOf("") }
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.padding())
    }
}
