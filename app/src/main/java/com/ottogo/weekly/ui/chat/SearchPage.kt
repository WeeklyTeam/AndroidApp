package com.ottogo.weekly.ui.chat

import android.hardware.lights.Light
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
import kotlinx.coroutines.runBlocking


class SearchPageViewModel : ViewModel() {

    val userIdLiveData: LiveData<Int>
        get() = userId

    val nameLiveData: LiveData<String>
        get() = name

    val userNameLiveData: LiveData<String>
        get() = userName

    val profilePictureLiveData: LiveData<String>
        get() = profilePicture

    val searchResultsLiveData: MutableLiveData<MutableList<Profile>>
        get() = searchResults

    val urequestedLiveData: LiveData<Boolean>
        get() = urequested
    
    val requestingLiveData: LiveData<Boolean>
        get() = requesting

    val friendLiveData: LiveData<Boolean>
        get() = friend

    val blockedLiveData: LiveData<Boolean>
        get() = blocked

    val profileIndexLiveData: LiveData<Int>
        get() = profileIndex


    val userId = MutableLiveData<Int>()

    val name = MutableLiveData<String>()

    val userName = MutableLiveData<String>()

    val profilePicture = MutableLiveData<String>()

    val urequested = MutableLiveData<Boolean>()

    val requesting = MutableLiveData<Boolean>()

    val friend = MutableLiveData<Boolean>()

    val blocked = MutableLiveData<Boolean>()

    val profileIndex = MutableLiveData<Int>()

    val searchResults = MutableLiveData<MutableList<Profile>>()

    fun addSearchItems(items: List<Profile>) {
        searchResults.value = items.toMutableList()
    }

    fun updateSearchItem(profileIndex: Int, newProfile: Profile) {
        var tempList:MutableList<Profile>? = searchResults.value?.toMutableList()

        if (tempList != null) {
            tempList[profileIndex] = newProfile
            Log.d("status", tempList[profileIndex].urequested.toString())
            searchResults.value = tempList.toMutableList()
        }
    }

    fun clearSearch() {
        searchResults.value = emptyList<Profile>().toMutableList()
    }

}


@Composable
fun SearchPage(navController: NavController, userViewModel: UserViewModel) {

    SearchPageScreen(navController = navController)

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPageScreen(navController: NavController, model: SearchPageViewModel = viewModel()) {

    val userId by model.userIdLiveData.observeAsState(1)
    val name by model.nameLiveData.observeAsState("")
    val userName by model.userNameLiveData.observeAsState("")
    val profilePicture by model.profilePictureLiveData.observeAsState("")
    val urequested by model.urequestedLiveData.observeAsState(false)
    val requesting by model.requestingLiveData.observeAsState(false)
    val friend by model.friendLiveData.observeAsState(false)
    val blocked by model.blockedLiveData.observeAsState(false)
    val profileIndex by model.profileIndexLiveData.observeAsState(0)

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent = { ModalBottomSheetContent(userId = userId, name = name, userName = userName, profilePicture = profilePicture,
        urequested = urequested, requesting = requesting, friend = friend, blocked = blocked, profileIndex = profileIndex
        ) }
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
fun SearchResults(searchText: String, coroutineScope: CoroutineScope, sheetState: ModalBottomSheetState, model: SearchPageViewModel = viewModel()) {

    val searchResults by model.searchResultsLiveData.observeAsState(emptyList())

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
                model.addSearchItems(searchApiList.results)
            }
        }
        else {
            model.clearSearch()
        }

        // display search results
        searchResults.forEachIndexed { profileIndex, searchResult ->
            SearchResultsItem(searchResult.user_id, searchResult.name, searchResult.username, searchResult.profile_picture,
                searchResult.urequested, searchResult.requesting, searchResult.friend, searchResult.blocked, profileIndex,
                coroutineScope, sheetState)
            Log.d("status", searchResult.toString())
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResultsItem(userId: Int, name: String, userName: String, profilePicture: String?, urequested: Boolean?,
                      requesting: Boolean?, friend: Boolean?, blocked: Boolean?, profileIndex: Int,
                      coroutineScope: CoroutineScope, sheetState: ModalBottomSheetState, model: SearchPageViewModel = viewModel())  {

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
                    model.userId.value = userId
                    model.name.value = name
                    model.userName.value = userName
                    model.profilePicture.value = profilePicture
                    model.urequested.value = urequested
                    model.requesting.value = requesting
                    model.friend.value = friend
                    model.blocked.value = blocked
                    model.profileIndex.value = profileIndex
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
fun PopUpBlockSheetContent(title: String, userId: Int, onDismiss: () -> Unit, onBlock: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, style = MaterialTheme.typography.h4)
        },
        shape = RoundedCornerShape(24.dp),
        buttons = {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp)
            ) {
                CustomButton(
                    buttonText = "Block",
                    backgroundColor = LightRed,
                    textColor = DarkRed
                ) {
                    onDismiss.invoke()
                    runBlocking {
                        WeeklyApi.retrofitService.block(
                            mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"),
                            userId
                        )
                    }
                    onBlock.invoke()
                }

                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    buttonText = "Block & Report",
                    backgroundColor = Color.White,
                    textColor = Black60,
                    outlineColor = LightGray
                ) {
                    onDismiss.invoke()
                    runBlocking {
                        WeeklyApi.retrofitService.block(
                            mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"),
                            userId
                        )
                        WeeklyApi.retrofitService.report(
                            mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"),
                            userId
                        )
                    }
                    onBlock.invoke()
                }

                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    buttonText = "Cancel",
                    backgroundColor = LightGray,
                    textColor = Black
                ) { onDismiss.invoke() }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    )
}


@Composable
fun PopUpConfirmationSheetContent(title: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
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
                        modifier = Modifier.weight(1f)) {
                        onConfirm.invoke()
                        onDismiss.invoke()
                    }

                }

                Spacer(modifier = Modifier.height(24.dp))

            }
        )
}


@Composable
fun ModalBottomSheetContent(userId: Int, name: String, userName: String, profilePicture: String, urequested: Boolean?, requesting: Boolean?,
                            friend: Boolean?, blocked: Boolean?, profileIndex: Int, model: SearchPageViewModel = viewModel()) {

    val showDialog = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }
    val blockDisplay = remember { mutableStateOf(false) }
    val searchResults by model.searchResultsLiveData.observeAsState(emptyList())

    val onConfirm = remember { mutableStateOf({ }) }

    if (showDialog.value && !blockDisplay.value) {
        PopUpConfirmationSheetContent(title = title.value,
            onDismiss = { showDialog.value = false },
            onConfirm = { onConfirm.value.invoke() } )
    } else if (showDialog.value && blockDisplay.value) {
        PopUpBlockSheetContent(title = title.value, userId = userId,
            onDismiss = { showDialog.value = false },
            onBlock = {
                // To recompose bottom sheet button
                model.blocked.value = true
                model.urequested.value = false
                model.requesting.value = false
                model.friend.value = false

                // To recompose search results with updated profile
                var newProfile = searchResults[profileIndex].copy()
                newProfile.blocked = true
                newProfile.urequested = false
                newProfile.requesting = false
                newProfile.friend = false
                model.updateSearchItem(profileIndex, newProfile)}
        )
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

                if (blocked == true) {
                    CustomButton(
                        buttonText = "This user is blocked",
                        backgroundColor = Color.White,
                        textColor = Black){
                        title.value = "Are you sure you want to unblock $name?"
                        blockDisplay.value = false
                        onConfirm.value = {
                            runBlocking {
                                WeeklyApi.retrofitService.unblock(mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"), userId)
                            }

                            // To recompose bottom sheet button
                            model.blocked.value = false

                            // To recompose search results with updated profile
                            var newProfile = searchResults[profileIndex].copy()
                            newProfile.blocked = false
                            model.updateSearchItem(profileIndex, newProfile)
                        }
                        showDialog.value = true
                    }
                }

                else if (friend == true) {
                    CustomButton(
                        buttonText = "Added",
                        backgroundColor = Color.White,
                        outlineColor = LightGray,
                        textColor = Black60) {
                        title.value = "Are you sure you want to remove $name as a friend?"
                        blockDisplay.value = false
                        onConfirm.value = {
                            runBlocking {
                                WeeklyApi.retrofitService.reject(mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"), userId)
                            }

                            // To recompose bottom sheet button
                            model.friend.value = false

                            // To recompose search results with updated profile
                            var newProfile = searchResults[profileIndex].copy()
                            newProfile.friend = false
                            model.updateSearchItem(profileIndex, newProfile)
                        }
                        showDialog.value = true
                    }
                }

                else if (urequested == true) {
                    CustomButton(
                        buttonText = "Requested",
                        backgroundColor = LightGray,
                        textColor = Black60,
                        onClick = {
                            title.value = "Are you sure you want to cancel this request?"
                            blockDisplay.value = false
                            onConfirm.value = {
                                runBlocking {
                                    WeeklyApi.retrofitService.reject(mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"), userId)
                                }

                                // To recompose bottom sheet button
                                model.urequested.value = false

                                // To recompose search results with updated profile
                                var newProfile = searchResults[profileIndex].copy()
                                newProfile.urequested = false
                                model.updateSearchItem(profileIndex, newProfile)
                            }
                            showDialog.value = true
                        })
                }

                else if (requesting == true) {
                    Row(){
                        CustomButton(buttonText = "Reject", backgroundColor = LightGray,
                            textColor = Black, modifier = Modifier.weight(1f)) {
                            runBlocking {
                                WeeklyApi.retrofitService.reject(mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"), userId)
                            }

                            // To recompose bottom sheet button
                            model.requesting.value = false

                            // To recompose search results with updated profile
                            var newProfile = searchResults[profileIndex].copy()
                            newProfile.requesting = false
                            model.updateSearchItem(profileIndex, newProfile)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        CustomButton(buttonText = "Accept", modifier = Modifier.weight(1f)) {
                            runBlocking {
                                WeeklyApi.retrofitService.accept(mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"), userId)
                            }

                            // To recompose bottom sheet button
                            model.requesting.value = false
                            model.friend.value = true

                            // To recompose search results with updated profile
                            var newProfile = searchResults[profileIndex].copy()
                            newProfile.requesting = false
                            newProfile.friend = true
                            model.updateSearchItem(profileIndex, newProfile)
                        }
                    }
                }

                else {
                    CustomButton(
                        buttonText = "Add",
                        onClick = {
                            runBlocking {
                                WeeklyApi.retrofitService.add(mapOf("Authorization" to "token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7"), userId)
                            }

                            // To recompose bottom sheet button
                            model.urequested.value = true

                            // To recompose search results with updated profile
                            var newProfile = searchResults[profileIndex].copy()
                            newProfile.urequested = true
                            model.updateSearchItem(profileIndex, newProfile)
                        })
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            IconButton(onClick = {
                title.value = "Are you sure you want to block $name?"
                blockDisplay.value = true
                showDialog.value = true
            }, content = { Image(painterResource(id = R.drawable.ic_spam_line), contentDescription = "report icon") })
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
