package com.ottogo.weekly.ui.chat

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class SearchPageViewModel : ViewModel() {

    val nameLiveData: LiveData<String>
        get() = name

    val userNameLiveData: LiveData<String>
        get() = userName

    val profilePictureLiveData: LiveData<String>
        get() = profilePicture


    val name = MutableLiveData<String>()

    val userName = MutableLiveData<String>()

    val profilePicture = MutableLiveData<String>()

}


@Composable
fun SearchPage(navController: NavController, userViewModel: UserViewModel) {

    SearchPageScreen(navController = navController)

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPageScreen(navController: NavController, model: SearchPageViewModel = viewModel()) {

    val name by model.nameLiveData.observeAsState("")
    val userName by model.userNameLiveData.observeAsState("")
    val profilePicture by model.profilePictureLiveData.observeAsState("")

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent = { ModalBottomSheetContent(name = name, userName = userName, profilePicture = profilePicture) }
    ) {

        SearchPageContent(navController, coroutineScope, sheetState)

    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPageContent(navController: NavController, coroutineScope: CoroutineScope, sheetState: ModalBottomSheetState) {

    Column(modifier = Modifier.padding(16.dp)){

        var searchText by remember { mutableStateOf("") }

        Row(verticalAlignment = Alignment.CenterVertically) {

            SearchBar(searchText, Modifier.weight(1f)) { searchText = it }

            Spacer(modifier = Modifier.width(16.dp))

            CancelButton(navController)

        }

        Spacer(Modifier.height(8.dp))

        SearchResults(searchText, coroutineScope, sheetState)
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResults(searchText: String, coroutineScope: CoroutineScope, sheetState: ModalBottomSheetState) {

    val searchResults = remember { mutableStateListOf<Profile>() }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        // api call for search results
        if (searchText.isNotBlank() || searchText.isNotEmpty()) {
            LaunchedEffect(key1 = searchText) {
                var searchApiList = WeeklyApi.retrofitService.search(
                    mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"),
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
            SearchResultsItem(searchResult.name, searchResult.username, searchResult.profile_picture, coroutineScope, sheetState)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResultsItem(name: String, userName: String, profilePicture: String?, coroutineScope: CoroutineScope, sheetState: ModalBottomSheetState, model: SearchPageViewModel = viewModel())  {

    Spacer(Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {

        ProfilePicture(profilePicture)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.clickable {

            coroutineScope.launch {
                if (sheetState.isVisible) {
                    sheetState.hide()
                }
                else {
                    model.name.value = name
                    model.userName.value = userName
                    model.profilePicture.value = profilePicture
                    sheetState.show()
                }
            }

        }) {
            Text(text = name, style = MaterialTheme.typography.body2)
            Text(text = userName, style = MaterialTheme.typography.body1, color = Black40)
        }
    }

    Spacer(Modifier.height(16.dp))

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
fun PopUpConfirmationSheetContent(title: String, showDialog: Boolean, onDismiss: () -> Unit) {

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = title, style = MaterialTheme.typography.h4)
            },
            shape = RoundedCornerShape(24.dp),
            buttons = {
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp)) {
                    CustomButton(buttonText = "No",
                        backgroundColor = LightGray,
                        textColor = Black,
                        modifier = Modifier.weight(1f)) { onDismiss.invoke() }
                    Spacer(modifier = Modifier.width(24.dp))
                    CustomButton(buttonText = "Yes",
                        modifier = Modifier.weight(1f)) { onDismiss.invoke() }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        )
    }

}


@Composable
fun ModalBottomSheetContent(name: String, userName: String, profilePicture: String) {

    val showDialog = remember { mutableStateOf(false) }

    Card() {
        if (showDialog.value) {
            PopUpConfirmationSheetContent(title = "Are you sure you want to remove $name as a friend?",
                showDialog = showDialog.value,
                onDismiss = { showDialog.value = false })
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        ProfilePicture(profilePicture = profilePicture)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = name, style = MaterialTheme.typography.body2)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = userName, style = MaterialTheme.typography.body1, color = Black60)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(start = 60.dp, end = 60.dp)) {
            Box(modifier = Modifier.weight(1f)) {

                CustomButton(
                    buttonText = "Add",
                    onClick = {
                        showDialog.value = true
                    })

            }
            Spacer(modifier = Modifier.width(20.dp))
            IconButton(onClick = { }, content = { Image(painterResource(id = R.drawable.ic_spam_line), contentDescription = "report icon") })
        }

    }

    Spacer(modifier = Modifier.height(24.dp))

}


@Composable
fun ProfilePicture(profilePicture: String?) {

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(profilePicture)
            .build(),
        contentDescription = "profile picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(CircleShape)
            .height(50.dp)
            .width(50.dp))

}
