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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ottogo.weekly.BottomSheetType
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaModifierListOwner


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
fun SearchPage(navController: NavController, userViewModel: UserViewModel, openSheet: (Profile) -> Unit) {

    SearchPageScreen(navController = navController, userViewModel = userViewModel, openSheet = openSheet)

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPageScreen(navController: NavController, model: SearchPageViewModel = viewModel(), userViewModel: UserViewModel, openSheet: (Profile) -> Unit) {

    SearchPageContent(navController, userViewModel, openSheet)

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPageContent(navController: NavController, userViewModel: UserViewModel, openSheet: (Profile) -> Unit) {

    Column(){

        var searchText by remember { mutableStateOf("") }
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(16.dp))


            SearchBar(searchText, Modifier.weight(1f)) { searchText = it }

            Spacer(modifier = Modifier.width(16.dp))

            CancelButton(navController)
            Spacer(modifier = Modifier.width(16.dp))


        }

        Spacer(Modifier.height(16.dp))

        SearchResults(searchText, userViewModel = userViewModel, openSheet = openSheet)
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchResults(searchText: String, model: SearchPageViewModel = viewModel(), userViewModel: UserViewModel, openSheet: (Profile) -> Unit) {

    val searchResults by model.searchResultsLiveData.observeAsState(emptyList())
    val keyboardController = LocalSoftwareKeyboardController.current


    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        // api call for search results
        if (searchText.isNotBlank() || searchText.isNotEmpty()) {
            LaunchedEffect(key1 = searchText) {
                var searchApiList = WeeklyApi.retrofitService.search(
                    mapOf("Authorization" to "token ${userViewModel.token}"),
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
            SearchResultsItem(searchResult.user_id, searchResult.name, searchResult.username, searchResult.profile_picture, modifier = Modifier.clickable{
                keyboardController?.hide()

                var relationship = searchResult.user_id?.let {
                    runBlocking {
                        WeeklyApi.retrofitService.relationship(
                            mapOf("Authorization" to "token ${userViewModel.token}"), it
                        )
                    }
                }

                if (relationship != null) {
                    searchResult.requesting = relationship["requesting"]
                    searchResult.urequested = relationship["urequested"]
                    searchResult.friend = relationship["friend"]
                    searchResult.blocked = relationship["blocked"]
                }

                openSheet(searchResult)
            })
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResultsItem(userId: Int, name: String, userName: String, profilePicture: String?, modifier: Modifier = Modifier)  {


    Row(modifier = modifier
        .padding(vertical = 8.dp, horizontal = 16.dp)
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        ProfilePicture(profilePicture)

        Spacer(modifier = Modifier.width(16.dp))

        Column() {
            Text(text = name, style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(2.dp))
            Text(text = userName, style = MaterialTheme.typography.body2, color = Black40)
        }
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




