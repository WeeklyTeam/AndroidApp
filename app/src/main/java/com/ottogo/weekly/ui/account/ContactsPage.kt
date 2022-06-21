package com.ottogo.weekly.ui.account

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.chat.SearchBar
import com.ottogo.weekly.ui.components.ProfilePicture
import kotlinx.coroutines.launch

data class ContactsModel(val phone: String, val name: String)

@SuppressLint("Range")
class ContactsViewModel(private val token: String, private val context: Context): ViewModel(){

    private val _contacts = mutableStateListOf<ContactsModel>()
    val contacts: List<ContactsModel> = _contacts
    private val _profiles = mutableStateListOf<Profile>()
    val profiles: List<Profile> = _profiles

    init {
        Log.d("contacts", "init")

    }

    suspend fun getContacts(){

        val resolver: ContentResolver = context.contentResolver
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null)

        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt()

                    Log.d("contact", id)



                    if (phoneNumber > 0) {
                        val cursorPhone = resolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                        if (cursorPhone != null) {
                            if(cursorPhone.count > 0) {
                                while (cursorPhone.moveToNext()) {
                                    val phoneNumValue = cursorPhone.getString(
                                        cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                                    _contacts.add(ContactsModel(phone = phoneNumValue, name = name))

                                }
                            }
                        }
                        cursorPhone?.close()
                    }
                }
            } else {
                //   toast("No contacts available!")
            }
        }
        cursor?.close()
        searchContacts()
    }

    suspend fun searchContacts(){
        val contactsList: List<String> = getNumbersList()
        _profiles.clear()
        Log.d("CONTacts", "HEY")
        _profiles.addAll(WeeklyApi.retrofitService.searchContacts(mapOf("Authorization" to "token $token"), mapOf("contacts" to contactsList)))

    }

    private fun getNumbersList(): List<String> {
        val returnList = mutableListOf<String>()
        val regex = Regex("[^0-9]")
        for (contact in contacts) {
            returnList.add(regex.replace(contact.phone, "").takeLast(10))
        }
        return returnList.toList()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactsPage(navController: NavController, userViewModel: UserViewModel) {

    val contactPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_CONTACTS
    )

    val context = LocalContext.current


    Column() {
        TitleBar(navController = navController, title = "Contacts")

        when (contactPermissionState.status) {
            // If the camera permission is granted, then show screen with the feature enabled
            PermissionStatus.Granted -> {
                    ContactsScreen(navController = navController, userViewModel = userViewModel, contactsViewModel = ContactsViewModel(context = context, token = userViewModel.token ?: ""))
            }
            is PermissionStatus.Denied -> {

                Spacer(Modifier.weight(1F))

                Text("Add access to contacts\nfor this feature", style = MaterialTheme.typography.h2, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth())

                CustomButton(buttonText = "Allow", onClick = { contactPermissionState.launchPermissionRequest() }, modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp))

                Spacer(Modifier.weight(1F))

            }
        }
    }

}

@Composable
fun ContactsScreen(navController: NavController, userViewModel: UserViewModel, contactsViewModel: ContactsViewModel) {

    var searchText by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()


    LaunchedEffect(key1 = 1, block = {
        scope.launch {
            contactsViewModel.getContacts()
            Log.d("CONTacts", "lawnefex")

        }
    })

    Column {

        SearchBar(searchText, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { searchText = it }

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)) {

            Text("On the app", style = MaterialTheme.typography.h4, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.height(8.dp))

            contactsViewModel.profiles.forEach{ profile ->
                ContactProfileItem(profile = profile)
            }

            Spacer(Modifier.height(8.dp))

            Text("Contacts", style = MaterialTheme.typography.h4, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.height(8.dp))
            Log.d("CONTacts", contactsViewModel.profiles.count().toString())


            //.filter { searchText.isEmpty() || it.name.lowercase().contains(searchText.lowercase()) }

            contactsViewModel.contacts.forEach{ contact ->
                ContactItem(contact = contact)
            }
        }



    }
}

@Composable
fun ContactItem(contact: ContactsModel){
    Row() {
        ProfilePicture(url = null, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp) )

        Column() {
            Text(text = contact.name, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.body2)
            Text(text = contact.phone, modifier = Modifier.padding(top = 2.dp), style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)
        }

        Spacer(modifier = Modifier.weight(1F))

        CustomSmallButton(buttonText = "Add", onClick = {})

    }
}

@Composable
fun ContactProfileItem(profile: Profile){
    Row() {
        ProfilePicture(url = profile.profile_picture, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp) )

        Column() {
            Text(text = profile.name, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.body2)
            Text(text = profile.username, modifier = Modifier.padding(top = 2.dp), style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60)
        }

        Spacer(modifier = Modifier.weight(1F))

        CustomSmallButton(buttonText = "Add", onClick = {})

    }
}

@Composable
fun CustomSmallButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    onClick: suspend () -> Unit
) {
    var buttonloading: String by remember { mutableStateOf(buttonText) }
    val scope = rememberCoroutineScope()

    Button(onClick = {


        scope.launch {
            runBlocking {
                onClick()
                buttonloading = buttonText
            }

        }

        buttonloading = "loading"

    }, shape = CircleShape, modifier =
    modifier
        .padding(vertical = 8.dp, horizontal = 24.dp)
        .width(96.dp)
        .clip(CircleShape)) {
        Text(text = buttonloading, style = MaterialTheme.typography.h4)

    }
}