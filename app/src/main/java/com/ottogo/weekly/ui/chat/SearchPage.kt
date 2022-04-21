package com.ottogo.weekly.ui.chat

import android.app.Instrumentation
import android.hardware.lights.Light
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.DragInteraction
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.WeeklyApiService
import com.ottogo.weekly.api.models.ApiList
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import com.squareup.moshi.Json
import kotlinx.coroutines.runBlocking

@Composable
fun SearchPage(navController: NavController, userViewModel: UserViewModel) {
    Column(modifier = Modifier.padding(16.dp)){
        var searchText by remember { mutableStateOf("") }
        Row(verticalAlignment = Alignment.CenterVertically) {

            SearchBar(searchText, Modifier.weight(1f)) { searchText = it }

            Spacer(modifier = Modifier.width(16.dp))

            CancelButton(navController)

        }

        Spacer(Modifier.height(8.dp))

        SearchResults(searchText)
    }
}

@Composable
fun SearchBar(searchText: String, modifier: Modifier = Modifier, searchType: (String) -> Unit) {
    Surface(modifier = modifier) {
        BasicTextField(
            value = searchText,
            onValueChange = searchType,
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
}

@Composable
fun CancelButton(navController: NavController) {
    Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        elevation = null, contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        onClick = {
            navController.popBackStack()
        }) {
        Text(text = "Cancel", style = MaterialTheme.typography.h4, color = Black60)
    }
}

@Composable
fun SearchResults(searchText: String) {

    val searchResults = remember { mutableStateListOf<Profile>() }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        if (searchText.isNotBlank() || searchText.isNotEmpty()) {
            LaunchedEffect(key1 = searchText) {
                var searchApiList = WeeklyApi.retrofitService.search(
                    mapOf("Authorization" to "token 265245769906872d88b40205147f5cbf63538b83"),
                    searchText
                )
                searchResults.clear()
                searchResults.addAll(searchApiList.results)
                Log.d("status", searchApiList.count.toString())
            }
        }
        else {
            searchResults.clear()
        }

        for (searchResult in searchResults) {
            Log.d("status", searchResult.name)
            SearchResultsItem(searchResult.name, searchResult.username)
        }
    }
}

@Composable
fun SearchResultsItem(name: String, userName: String) {
    Spacer(Modifier.height(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.ic_search_line), contentDescription = "profile picture")

        Spacer(modifier = Modifier.width(16.dp))

        Column() {
            Text(text = name, style = MaterialTheme.typography.body2)
            Text(text = userName, style = MaterialTheme.typography.body1, color = Black40)
        }
    }
    Spacer(Modifier.height(16.dp))
}
