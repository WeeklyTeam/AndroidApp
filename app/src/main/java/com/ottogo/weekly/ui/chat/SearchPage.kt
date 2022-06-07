package com.ottogo.weekly.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.Black40
import com.ottogo.weekly.ui.theme.Black60
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun SearchPage(navController: NavController, userViewModel: UserViewModel) {
    var searchText by remember { mutableStateOf("") }

    Column(){

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {

            SearchBar(searchText, Modifier.weight(1f)) { searchText = it }

            Spacer(modifier = Modifier.width(8.dp))

            CancelButton(navController)

        }

        Spacer(Modifier.height(16.dp))

        SearchResults(searchText, userViewModel = userViewModel)
    }
}


@Composable
fun SearchBar(searchText: String, modifier: Modifier = Modifier, searchType: (String) -> Unit) {
    Surface(modifier = modifier.fillMaxWidth()) {

        val focusManager = LocalFocusManager.current

        TextField(
            value = searchText,
            onValueChange = searchType,
            shape = CircleShape,
            placeholder = { Text("Search", style = MaterialTheme.typography.body1, color = Black60) },
            singleLine = true,
            leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_search_line), contentDescription = "search icon") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Black,
                leadingIconColor = Black60,
                backgroundColor = LightGray,
                cursorColor = Black60,

                // removes underline
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.body1,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
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
fun SearchResults(searchText: String, userViewModel: UserViewModel) {

    val searchResults = remember { mutableStateListOf<Profile>() }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        // api call for search results
        if (searchText.isNotBlank() || searchText.isNotEmpty()) {
            LaunchedEffect(key1 = searchText) {
                val searchApiList = WeeklyApi.retrofitService.search(
                    mapOf("Authorization" to "token ${userViewModel.token}"),
                    searchText
                )
                searchResults.clear()
                searchResults.addAll(searchApiList.results)
            }
        }
        else {
            searchResults.clear()
        }

        // display search results
        for (searchResult in searchResults) {
            SearchResultsItem(searchResult.name, searchResult.username, searchResult.profile_picture)
        }
    }
}


@Composable
fun SearchResultsItem(name: String, userName: String, profilePicture: String?) {


    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        ProfilePicture(profilePicture)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.clickable { /* TODO: add click functionality */ }) {
            Text(text = name, style = MaterialTheme.typography.body1)
            Text(text = userName, style = MaterialTheme.typography.body2, color = Black40)
        }
    }


}
