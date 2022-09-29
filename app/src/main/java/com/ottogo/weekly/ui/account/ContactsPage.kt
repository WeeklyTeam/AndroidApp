package com.ottogo.weekly.ui.account

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Paint
import android.os.Build
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.chat.CancelButton
import com.ottogo.weekly.ui.chat.SearchBar
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.Purple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities

data class ContactsModel(val phone: String, val name: String)

@SuppressLint("Range")
class ContactsViewModel(): ViewModel(){

    private val _contacts = mutableStateListOf<ContactsModel>()
    val contacts: List<ContactsModel> = _contacts
    private val _profiles = mutableStateListOf<Profile>()
    val profiles: List<Profile> = _profiles


    fun addData(map: Map<String, Any>){
        _contacts.addAll(map["contacts"] as Collection<ContactsModel>)
        _profiles.addAll(map["profiles"] as Collection<Profile>)

    }



}
val regex = Regex("[^0-9]")

@SuppressLint("Range")
@RequiresApi(Build.VERSION_CODES.N)
suspend fun getContacts(token: String, context: Context): Map<String, Any>{

        val tempContacts = mutableListOf<ContactsModel>()


        val resolver: ContentResolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null
        )

        Log.d("contact", "hey")

        if (cursor != null) {
            Log.d("contact", cursor.toString())
            Log.d("contact", cursor.count.toString())

            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                    ).toInt()

                    Log.d("contact", id)



                    if (phoneNumber > 0) {
                        val cursorPhone = resolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            arrayOf(id),
                            null
                        )

                        if (cursorPhone != null) {
                            if (cursorPhone.count > 0) {
                                while (cursorPhone.moveToNext()) {
                                    val phoneNumValue = cursorPhone.getString(
                                        cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    )

                                    tempContacts.add(
                                        ContactsModel(
                                            phone = regex.replace(
                                                phoneNumValue,
                                                ""
                                            ).takeLast(10), name = name
                                        )
                                    )

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
        val profiles = searchContacts(token, tempContacts)
        profiles.forEach{ profile ->
            tempContacts.removeIf { it.phone == profile.phone }
        }
        return mapOf("contacts" to tempContacts, "profiles" to profiles)

}

@RequiresApi(Build.VERSION_CODES.N)
suspend fun searchContacts(token: String, contacts: List<ContactsModel>): List<Profile> {
    val contactsList: List<String> = getNumbersList(contacts)
    Log.d("CONTacts", "HEY")
    return WeeklyApi.retrofitService.searchContacts(mapOf("Authorization" to "token $token"), mapOf("contacts" to contactsList))

}

private fun getNumbersList(contacts: List<ContactsModel>): List<String> {
    val returnList = mutableListOf<String>()
    for (contact in contacts) {
        returnList.add(contact.phone)
    }
    return returnList.toList()
}

@SuppressLint("NewApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactsPage(navController: NavController, userViewModel: UserViewModel) {

    val contactPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_CONTACTS
    )

Log.d("contacts", "recomposing")
    val context = LocalContext.current

    var contacts = remember{
        mutableStateListOf<ContactsModel>()
    }
    var profiles = remember{
        mutableStateListOf<Profile>()
    }

    LaunchedEffect(key1 = contactPermissionState.status, block = {
        if (contactPermissionState.status == PermissionStatus.Granted) {
            Log.d("contacts", "launching")
            withContext(Dispatchers.IO) {


                val map = getContacts(userViewModel?.token ?: "", context = context)
                Log.d("map", map["profiles"].toString())
                //            contactsViewModel.addData(map)
                //            Log.d("viewmodel", contactsViewModel.contacts.toList().toString())
                contacts.addAll(map["contacts"] as Collection<ContactsModel>)
                profiles.addAll(map["profiles"] as Collection<Profile>)

            }
        }

    })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TitleBar(navController = navController, title = "Contacts")

        when (contactPermissionState.status) {
            // If the camera permission is granted, then show screen with the feature enabled
            is PermissionStatus.Granted -> {
                if (contacts.isNotEmpty() || profiles.isNotEmpty()) {
                    ContactsScreen(
                        navController = navController,
                        contacts,
                        profiles,
                        userViewModel = userViewModel
                    )
                } else {
                    Spacer(Modifier.weight(1F))

                    CircularProgressIndicator()

                    Spacer(Modifier.weight(1F))

                }

            }
            is PermissionStatus.Denied -> {

                Spacer(Modifier.weight(1F))

                Text("Add access to contacts\nfor this feature", style = MaterialTheme.typography.h2, textAlign = TextAlign.Center, modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth())

                CustomButton(buttonText = "Allow", onClick = { contactPermissionState.launchPermissionRequest() }, modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp))

                Spacer(Modifier.weight(1F))

            }
        }
    }

}

@Composable
fun ContactsScreen(navController: NavController, contacts: List<ContactsModel>, profiles: List<Profile>, userViewModel: UserViewModel) {

    var searchText by remember {
        mutableStateOf("")
    }

    Log.d("contacts", "recomposing2")


    val context = LocalContext.current
    val invitationMessage = (userViewModel.profile?.name ?: "Someone") + " wants to hang out with you on Weekly https://www.theweeklyapp.com/app/"

    Column {


        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(16.dp))


            SearchBar(searchText, Modifier.weight(1f)) { searchText = it }

//            Spacer(modifier = Modifier.width(16.dp))
//
//            CancelButton(navController)
            Spacer(modifier = Modifier.width(16.dp))


        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)) {

            Text("Invite via", style = MaterialTheme.typography.h4, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.width(4.dp))


                InviteItem(resource = R.drawable.ic_link, contentDescription = "Copy", tint = ExtendedTheme.colors.Black60, color = ExtendedTheme.colors.LightGray) {
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("text",  invitationMessage)
                    clipboardManager.setPrimaryClip(clipData)

                    val toast = Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT)
                    toast.show()

                }

                InviteItem(resource = R.drawable.ic_ghost_logo__for_light_backgrounds_, contentDescription = "Snapchat", tint = Color.Unspecified, color = Color(red = 255, blue = 0, green = 252)) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, invitationMessage)
                        type = "text/plain"
                    }
                    sendIntent.`package` = "com.snapchat.android"

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    try {
                        context.startActivity(sendIntent)
                    } catch (e: Exception){
                        val toast = Toast.makeText(context, "Snapchat not found", Toast.LENGTH_SHORT)
                        toast.show()
                    }

                }

                InviteItem(resource = R.drawable.ic_instagram_glyph_white, contentDescription = "Instagram", tint = Color.Unspecified, color = Color(red = 255, blue = 105, green = 0)) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, invitationMessage)
                        type = "text/plain"
                    }
                    sendIntent.`package` = "com.instagram.android"
                    try {
                        context.startActivity(sendIntent)
                    } catch (e: Exception){
                        val toast = Toast.makeText(context, "Instagram not found", Toast.LENGTH_SHORT)
                        toast.show()
                    }

                }

                InviteItem(resource = R.drawable.ic_discord, contentDescription = "Discord", tint = Color.Unspecified, color = Color(red = 88, blue = 242, green = 101)) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, invitationMessage)
                        type = "text/plain"
                    }
                    sendIntent.`package` = "com.discord"
                    try {
                        context.startActivity(sendIntent)
                    } catch (e: Exception){
                        val toast = Toast.makeText(context, "Discord not found", Toast.LENGTH_SHORT)
                        toast.show()
                    }

                }

//                InviteItem(resource = R.drawable.ic_discord, contentDescription = "WhatsApp", tint = Color.Unspecified, color = Color( 89, 206, 114)) {
//                    val sendIntent: Intent = Intent().apply {
//                        action = Intent.ACTION_SEND
//                        putExtra(Intent.EXTRA_TEXT, invitationMessage)
//                        type = "text/plain"
//                    }
//
//                    val shareIntent = Intent.createChooser(sendIntent, null)
//                    context.startActivity(shareIntent)
//
//                }

                InviteItem(resource = R.drawable.ic_share_forward_fill, contentDescription = "Share", tint = MaterialTheme.colors.onPrimary, color = MaterialTheme.colors.primary) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, invitationMessage)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)

                }


                Spacer(modifier = Modifier.width(4.dp))


            }
            Spacer(modifier = Modifier.height(24.dp))




            Text("On the app", style = MaterialTheme.typography.h4, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.height(8.dp))

            profiles.filter {
                it.username.lowercase().contains(searchText.lowercase())
                it.name.lowercase().contains(searchText.lowercase())
            }.forEach{ profile ->
                ContactProfileItem(profile = profile, userViewModel = userViewModel)
            }

            Spacer(Modifier.height(8.dp))

            Text("Contacts", style = MaterialTheme.typography.h4, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.height(8.dp))
//            Log.d("CONTacts", profiles.count().toString())


            //.filter { searchText.isEmpty() || it.name.lowercase().contains(searchText.lowercase()) }

            contacts.filter {
                it.name.lowercase().contains(searchText.lowercase())
            }.forEach{ contact ->
                ContactItem(contact = contact, userViewModel = userViewModel)
            }
        }



    }
}

@Composable
fun InviteItem(resource: Int, contentDescription: String, tint: Color, color: Color, onClick: () -> Unit){
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Icon(
            painter = painterResource(id = resource),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onClick()
                }
                .background(color)
                .padding(12.dp)
                .size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = contentDescription, style = MaterialTheme.typography.body2)
    }
}

@Composable
fun ContactItem(contact: ContactsModel, userViewModel: UserViewModel){
    var invited by remember {
        mutableStateOf(false)
    }

    Row() {
        ProfilePicture(url = null, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp) )

        Column(Modifier.weight(1f)) {
            Text(text = contact.name, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.body2, overflow = TextOverflow.Ellipsis, maxLines = 1)
            Text(text = contact.phone, modifier = Modifier.padding(top = 2.dp), style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }


        if (!invited) {
            CustomSmallButton(buttonText = "Invite", onClick = {
                    WeeklyApi.retrofitService.inviteContact(
                        mapOf("Authorization" to "token ${userViewModel.token}"),
                        contact.phone
                    )

            }, backgroundColor = ExtendedTheme.colors.LightGray, textColor = MaterialTheme.colors.primary)
        } else {
            CustomSmallButton(buttonText = "Sent", textColor = ExtendedTheme.colors.Green, backgroundColor = MaterialTheme.colors.onPrimary, onClick = {

            })
        }

    }
}

@Composable
fun ContactProfileItem(profile: Profile, userViewModel: UserViewModel){
    var profile by remember {
        mutableStateOf(profile)
    }
    Row() {
        ProfilePicture(url = profile.profile_picture, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp) )

        Column(Modifier.weight(1f)) {
            Text(text = profile.name, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.body2, overflow = TextOverflow.Ellipsis, maxLines = 1)
            Text(text = profile.username, modifier = Modifier.padding(top = 2.dp), style = MaterialTheme.typography.body2, color = ExtendedTheme.colors.Black60, overflow = TextOverflow.Ellipsis, maxLines = 1)
        }


        if (profile.friend == true) {
//            CustomSmallButton(buttonText = "Added", onClick = {
//
//            }, backgroundColor = MaterialTheme.colors.background,  )
            Text("Added", color = MaterialTheme.colors.onBackground, textAlign = TextAlign.Center, modifier = Modifier
                .width(125.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp))
        } else if (profile.urequested == true) {
//            CustomSmallButton(buttonText = "Requested", textColor = ExtendedTheme.colors.Black60, backgroundColor = ExtendedTheme.colors.LightGray, onClick = {
//
//            })
            Text("Requested", color = MaterialTheme.colors.onBackground, textAlign = TextAlign.Center, modifier = Modifier
                .width(125.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp))

        } else if (profile.requesting == true) {
            CustomSmallButton(buttonText = "Add", onClick = {
                WeeklyApi.retrofitService.accept(mapOf("Authorization" to "token ${userViewModel.token}"), profile.user_id)
                profile = profile.copy(requesting = false, friend = true)
                userViewModel.addFriend(profile = profile)
            })
        } else {
            CustomSmallButton(buttonText = "Add", onClick = {
                WeeklyApi.retrofitService.add(mapOf("Authorization" to "token ${userViewModel.token}"), profile.user_id)
                profile = profile.copy(urequested = true)
            })
        }

    }
}

@Composable
fun CustomSmallButton(
    buttonText: String,
    backgroundColor: Color = Purple,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: suspend () -> Unit,

    ) {
    var buttonloading: String by remember { mutableStateOf(buttonText) }
    val scope = rememberCoroutineScope()

    Button(onClick = {


        scope.launch {
            onClick()
            buttonloading = buttonText


        }

        buttonloading = "loading"

    }, elevation = null, colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor), shape = RoundedCornerShape(24.dp), modifier =
    modifier
        .padding(vertical = 8.dp, horizontal = 24.dp)
        .width(96.dp)) {
        Text(text = buttonloading, style = MaterialTheme.typography.h4, color = textColor)

    }
}