package com.ottogo.weekly.ui.chat


import android.app.appsearch.SearchResults
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import com.squareup.moshi.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterialApi::class)
class SearchPageViewModel: ViewModel() {

    val sheetStateLiveData: LiveData<ModalBottomSheetState>
        get() = sheetState

    private val sheetState = MutableLiveData<ModalBottomSheetState>()

    fun expandCollapse() {
        viewModelScope.launch {
            if (sheetState.value?.isVisible == true) {
                sheetState.value!!.hide()
            }
            else {
                sheetState.value!!.show()
            }
        }
    }

}

@Composable
fun SearchPage(navController: NavController, userViewModel: UserViewModel) {

    SearchPageScreen(navController = navController)

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPageScreen(searchPageViewModel: SearchPageViewModel = viewModel(), navController: NavController) {
    val sheetState by searchPageViewModel.sheetStateLiveData.observeAsState(ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden))

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Text("hello")
        }
    ) {

        SearchPageContent(navController)

    }
}

@Composable
fun SearchPageContent(navController: NavController) {
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

        val focusManager = LocalFocusManager.current

        TextField(
            value = searchText,
            onValueChange = searchType,
            modifier = Modifier.padding(0.dp),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Search", style = MaterialTheme.typography.body2, color = Black60) },
            singleLine = true,
            leadingIcon = { Image(painter = painterResource(id = R.drawable.ic_search_line), contentDescription = "search icon") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Black60,
                leadingIconColor = Black60,
                backgroundColor = LightGray,
                cursorColor = Black60,

                // removes underline
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.body2,
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
fun SearchResults(searchText: String) {

    val searchResults = remember { mutableStateListOf<Profile>() }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        // api call for search results
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

        // display search results
        for (searchResult in searchResults) {
            SearchResultsItem(searchResult.name, searchResult.username, searchResult.profile_picture)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResultsItem(name: String, userName: String, profilePicture: String?, searchPageViewModel: SearchPageViewModel = viewModel())  {

    Spacer(Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {

        ProfilePicture(profilePicture)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.clickable {

            searchPageViewModel.expandCollapse()

        }) {
            Text(text = name, style = MaterialTheme.typography.body2)
            Text(text = userName, style = MaterialTheme.typography.body1, color = Black40)
        }
    }

    Spacer(Modifier.height(16.dp))

}


@Composable
fun ProfilePicture(profilePicture: String?) {
    // TODO: use profilePicture in ".data()"
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://cdn.britannica.com/55/174255-050-526314B6/brown-Guernsey-cow.jpg")
            .build(),
        contentDescription = "profile picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(CircleShape)
            .height(50.dp)
            .width(50.dp))
}
