package com.ottogo.weekly

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
// for a 'val' variable
import androidx.compose.runtime.getValue

// for a `var` variable also add
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.account.*
import com.ottogo.weekly.ui.bottomModals.ProfileBottomModalSheet
import com.ottogo.weekly.ui.calendar.*
import com.ottogo.weekly.ui.calendar.availability.AddAvailabilityPage
import com.ottogo.weekly.ui.calendar.availability.AvailabilityPage
import com.ottogo.weekly.ui.calendar.plot.PlotPage
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.chat.*
import com.ottogo.weekly.ui.chat.group.AddGroupMembersPage
import com.ottogo.weekly.ui.chat.group.EditGroupPage
import com.ottogo.weekly.ui.chat.group.GroupChatPage
import com.ottogo.weekly.ui.chat.group.GroupPage
import com.ottogo.weekly.ui.components.*
import com.ottogo.weekly.ui.login.*
import com.ottogo.weekly.ui.plot.PlotDatePage
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private var webSocket: WebSocketClient? = null
    val executorService: ExecutorService = Executors.newFixedThreadPool(4)





    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = StoreUserToken(context = applicationContext)


        setContent {

            WeeklyTheme {
                // A surface container using the 'background' color from the theme

                val token = dataStore.getToken.collectAsState(initial = null)
                if (token.value == null) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                        Column(modifier = Modifier.systemBarsPadding()) {
                            MainNavigation(userViewModel = userViewModel, webSocket = webSocket)
                        }
                    }
                }else {

                    if (token.value == "") {
                        WindowCompat.setDecorFitsSystemWindows(window, false)

                        ProvideWindowInsets {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colors.background
                            ) {
                                LoginNavigation(userViewModel = userViewModel)

                            }
                        }
                    } else {
                        userViewModel.setToken(token.value!!)
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            Column(modifier = Modifier.systemBarsPadding()) {
                                MainNavigation(
                                    userViewModel = userViewModel,
                                    webSocket = webSocket
                                )
                            }
                        }

                    }
                }


            }
        }
    }



    override fun onResume() {
        super.onResume()

        if (!userViewModel.token.isNullOrBlank()) {

            val headers = mapOf("authorization" to "token ${userViewModel.token}")

            val uri: URI? = URI("wss://plotsme.herokuapp.com/chat/")

            webSocket = object : WebSocketClient(uri, headers) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.d("WebSocket", "Connected")
                }

                override fun onMessage(message: String?) {


                    Log.d("WebSocket", message.toString())


//                    Log.d("WebSocket", message.toString())
//                    val moshi = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).add(
//                        KotlinJsonAdapterFactory()
//                    ).build()
//                    val adapter: JsonAdapter<ChatMessage> = moshi.adapter(ChatMessage::class.java)
//                    val chatMessage = adapter.fromJson(message)
//                    if (chatMessage != null) {
//                        Log.d("WebSocket", chatMessage.toString())
//
//                        if (userViewModel.profile?.user_id != chatMessage.user_id) {
//                            userViewModel.addMessage(chatMessage)
//
//                        }
//
//                    }

                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.d("WebSocket", "Closed")
                    if (reason != null) {
                        Log.d("WebSocket", reason)
                    }


                }

                override fun onError(ex: Exception?) {
                    Log.d("WebSocket", "Error")
                    Log.d("WebSocket", ex.toString())

                }

            }

            (webSocket as WebSocketClient).connect()

        }
    }

    override fun onPause() {
        super.onPause()
        if (webSocket != null) {
            webSocket!!.close()
        }
    }
}

class StoreUserToken(private val context: Context) {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user")
        val USER_TOKEN_KEY = stringPreferencesKey("token")
    }

    //get the saved email
    val getToken: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN_KEY] ?: ""
        }

    //save email into datastore
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }


}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginNavigation(userViewModel: UserViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "landingPage") {

        composable("landingPage") { LandingPage(navController) }
        composable( "loginPage") { LoginPage(navController, userViewModel) }
        composable("signupPage/{dob}") { backStackEntry -> SignupPage(navController,
            backStackEntry.arguments?.getString("dob")!!)
        }
        composable("signupInterestsPage/{token}") { backStackEntry -> SignupInterestsPage(navController,
            backStackEntry.arguments?.getString("token")!!,
            userViewModel
        ) }
        composable("signupProfilePage/{token}") { backStackEntry -> SignupProfilePage(navController,
            backStackEntry.arguments?.getString("token")!!
        ) }
        composable("signupBirthdayPage") { SignupBirthdayPage(navController) }
        composable("signupVerifyPage/{token}") { backStackEntry -> SignupVerifyPage(navController,
            backStackEntry.arguments?.getString("token")!!
        ) }
        composable("webviewPage/{title}?url={url}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry -> WebViewPage(navController,
            backStackEntry.arguments?.getString("title")!!, backStackEntry.arguments?.getString("url")!!
        ) }

    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation(userViewModel: UserViewModel, webSocket: WebSocketClient?, bottomSheetViewModel: BottomSheetViewModel = BottomSheetViewModel()){
    val navController = rememberNavController()

    val modalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )

    val scope = rememberCoroutineScope()
    //val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current


    val closeSheet = {
        //focusManager.clearFocus()
        keyboardController?.hide()

        bottomSheetViewModel.bottomSheetType = null
        scope.launch { modalBottomSheetState.hide() }
    }

    val openSheet = {
        scope.launch { modalBottomSheetState.show() }
    }
    val activity = (LocalContext.current as? Activity)

    BackHandler {
        if (modalBottomSheetState.isVisible) {
            when (bottomSheetViewModel.bottomSheetType) {
                BottomSheetType.Planning2 -> {
                    bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning1
                }
                BottomSheetType.Planning3 -> {
                    bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning2
                }
                else -> {

                    closeSheet()

                }
            }
        } else {


            if (navController.currentDestination?.route == "homePage") {
                activity?.finish()
            } else {
                navController.navigateUp()
            }
        }
    }

    ModalBottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = modalBottomSheetState,
        sheetContent = {
            Spacer(Modifier.height(1.dp))

            bottomSheetViewModel.bottomSheetType?.let {

                SheetLayout(
                    closeSheet = {
                        closeSheet()
                    },
                    bottomSheetViewModel = bottomSheetViewModel,
                    userViewModel = userViewModel,)
            }
        },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        NavHost(navController = navController, startDestination = "homePage") {

            composable("homePage") { HomePage(navController, userViewModel) {
                bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning1
                openSheet()
            }
            }
            composable("searchPage") { SearchPage(navController, userViewModel){
                bottomSheetViewModel.profile = it
                bottomSheetViewModel.bottomSheetType = BottomSheetType.Profile
                openSheet()
            } }
            composable("settingsPage") { SettingsPage(navController) }
            composable("reportPage") { ReportPage(navController, userViewModel) }
            composable("activitiesPage") { ActivitiesPage(navController, userViewModel) }
            composable("addActivityPage") { AddActivityPage(navController, userViewModel) }
            composable("contactsPage") { ContactsPage(navController, userViewModel) }
            composable("editProfilePage") { EditProfilePage(navController, userViewModel) }
            composable("createGroupPage") { CreateGroupPage(navController) }
            composable("addAvailabilityPage") { AddAvailabilityPage(navController, userViewModel) }
            composable("chatSearchPage") { ChatSearchPage(navController, userViewModel) }
            composable("groupPage/{group_id}") { backStackEntry ->
                GroupPage(
                    navController,
                    backStackEntry.arguments?.get("group_id").toString().toInt(),
                    userViewModel
                )
            }
            composable("groupAddMembersPage/{group_id}") { backStackEntry ->
                AddGroupMembersPage(navController,
                    backStackEntry.arguments?.get("group_id").toString().toInt() ,
                    userViewModel)
            }
            composable("editGroupPage/{group_id}") { backStackEntry ->
                EditGroupPage(
                    navController,
                    backStackEntry.arguments?.get("group_id").toString().toInt(),
                    userViewModel
                )
            }
            composable("groupChatPage/{group_id}") { backStackEntry ->
                GroupChatPage(
                    navController,
                    userViewModel,
                    backStackEntry.arguments?.get("group_id").toString().toInt(),
                    webSocket
                )
            }
            composable("accountPage") { AccountPage(navController, userViewModel) }
            composable("createCalendarPage") { CreateCalendarPage(navController, userViewModel) }
            composable("plotDatePage") { PlotDatePage(navController) }
            composable("availabilityPage") { AvailabilityPage(navController, userViewModel) }
            composable("inviteGroupPage/{groupName}") { backStackEntry ->
                InviteGroupPage(
                    navController,
                    backStackEntry.arguments?.getString("groupName")!!,
                    navController.previousBackStackEntry?.arguments?.getParcelable<Uri>("imageUri"),
                    userViewModel
                )
            }
            composable("plotPage/{plot_id}") { backStackEntry ->
                PlotPage(
                    navController,
                    backStackEntry.arguments?.get("plot_id").toString().toInt(),
                    userViewModel
                )
            }
            composable("privateChatPage/{userId}") { backStackEntry ->
                PrivateChatPage(
                    navController, userViewModel,
                    backStackEntry.arguments?.getString("userId")!!.toInt(), webSocket
                )
            }
            composable(
                "webviewPage/{title}?url={url}",
                arguments = listOf(navArgument("userId") { defaultValue = "" })
            ) { backStackEntry ->
                WebViewPage(
                    navController,
                    backStackEntry.arguments?.getString("title")!!,
                    backStackEntry.arguments?.getString("url")!!
                )
            }


        }
    }
}

class BottomSheetViewModel: ViewModel() {
    var bottomSheetType: BottomSheetType? by mutableStateOf(null)
    var profile by mutableStateOf<Profile?>(null)
    var plotName by mutableStateOf<String?>(null)
    var plotEmoji by mutableStateOf<String?>(null)
    var plotDate by mutableStateOf<Date?>(null)





}

enum class BottomSheetType() {
    Planning1, Planning2, Planning3, Profile
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SheetLayout(
    bottomSheetViewModel: BottomSheetViewModel,
    userViewModel: UserViewModel,
    closeSheet : () -> Unit
){

    when(bottomSheetViewModel.bottomSheetType){
        BottomSheetType.Planning1 -> Screen1(closeSheet, bottomSheetViewModel)
        BottomSheetType.Planning2 -> Screen2(closeSheet, bottomSheetViewModel)
        BottomSheetType.Planning3 -> Screen3(closeSheet, bottomSheetViewModel, userViewModel)
        BottomSheetType.Profile -> ProfileBottomModalSheet(userViewModel = userViewModel, bottomSheetViewModel = bottomSheetViewModel)
        else ->
            Spacer(Modifier.height(1.dp))
    }

}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Screen3(closeSheet: () -> Unit, bottomSheetViewModel: BottomSheetViewModel, userViewModel: UserViewModel) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp

    var selectedGroupId by remember { mutableStateOf<Int?>(null) }
    var selectedProfileIds by remember {
        mutableStateOf(mutableListOf<Int>())
    }


    Column {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)){

            IconButton(onClick = {  }, modifier = Modifier.size(56.dp)) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                    contentDescription = "back",
                    modifier = Modifier.size(24.dp)
                )
            }


            Text(text = "Invite",
                style = MaterialTheme.typography.h2
            )

        }

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(
            Modifier
                .height(screenHeight - 65.dp - 91.dp - 24.dp)
                .verticalScroll(rememberScrollState())) {

            Text("Groups", style = MaterialTheme.typography.h4, modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp), color = ExtendedTheme.colors.Black60)

            userViewModel.groups.forEach { (index, group) ->
                userViewModel.groups[index]?.let { SelectGroupItem(group = group, selected = selectedGroupId == group.id, modifier = Modifier
                    .clickable {
                        if (selectedGroupId == it.id) {
                            selectedGroupId = null
                        } else {
                            selectedGroupId = it.id
                            selectedProfileIds = mutableListOf<Int>()
                        }
                    }
                    .padding(horizontal = 8.dp)) }
            }

            Text("Friends", style = MaterialTheme.typography.h4, modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp), color = ExtendedTheme.colors.Black60)

            userViewModel.friends.forEach { (index, friend) ->
                userViewModel.friends[index]?.let { SelectProfileItem(profile = friend, selected = selectedProfileIds.contains(friend.user_id), modifier = Modifier
                    .clickable {
                        if (selectedProfileIds.contains(friend.user_id)) {
                            selectedProfileIds.remove(friend.user_id)
                        } else {
                            selectedGroupId = null
                            selectedProfileIds.add(friend.user_id)
                        }
                    }
                    .padding(horizontal = 8.dp)) }
            }
        }

        Divider(color = ExtendedTheme.colors.LightGray, thickness = 1.dp)

        CustomButton(buttonText = "Invite", onClick = {
            try {
                var body: MutableMap<String, Any?> = mutableMapOf(
                    "starttime" to bottomSheetViewModel.plotDate,
                    "name" to bottomSheetViewModel.plotName,
                    "emoji" to bottomSheetViewModel.plotEmoji,

                    )
                when {
                    selectedGroupId != null -> {
                        body["group"] = selectedGroupId
                    }
                    selectedProfileIds.count() == 1 -> {
                        body["relationship"] = userViewModel.friends[selectedProfileIds[0]]?.relationship_id
                    }
                    else -> {
                        body["invited"] = selectedProfileIds
                    }
                }
                val plot = WeeklyApi.retrofitService.createPlot(
                    mapOf("Authorization" to "token ${userViewModel.token}"),
                    body
                )
                userViewModel.addPlot(plot)
            } catch (exception: Exception){
                Log.d("createplanexception", exception.toString())
                closeSheet()
            }

        }, modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 18.dp))
    }
}

@Composable
fun Screen2(closeSheet: () -> Unit, bottomSheetViewModel: BottomSheetViewModel) {
    var selectedDate: Date? by rememberSaveable {
        mutableStateOf(null)
    }

    var displayCalendar by rememberSaveable {
        mutableStateOf(true)
    }


    var displayMonth by rememberSaveable{
        mutableStateOf(initialMonth())
    }

    Column(Modifier.padding(24.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = { displayMonth = addMonth(displayMonth, -1) }, modifier = Modifier.size(56.dp)) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                SimpleDateFormat(if (displayCalendar) {"MMM yyyy"} else {"MMM dd"}).format(displayMonth),
                modifier = Modifier.weight(1F), style = MaterialTheme.typography.h2, textAlign = TextAlign.Center)

            IconButton(onClick = { displayMonth = addMonth(displayMonth, 1) }, modifier = Modifier.size(56.dp)) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_right_s_line),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        if (displayCalendar) {
            CalendarComponent(month = displayMonth, shortened = true, modifier = Modifier
                .padding(16.dp), selectedDate = null, selectDate = {
                displayCalendar = false
                selectedDate = it
            })
        }
        else {
            ScrollPicker(options = listOf(List(12){ index -> (index+1).toString()}, listOf("AM", "PM")), selectItem = listOf(
                {   it ->
                    Log.d("selector", it.toString())
                    val calendar = Calendar.getInstance()
                    calendar.time = selectedDate
                    calendar[Calendar.HOUR] = it+1
                    selectedDate = calendar.time

                }, {
                    Log.d("selector", it.toString())

                    val calendar = Calendar.getInstance()
                    calendar.time = selectedDate
                    calendar[Calendar.AM_PM] = it
                    selectedDate = calendar.time
                    Log.d("selecteddate", selectedDate.toString())
                }, {}
            ))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!displayCalendar) {
            CustomButton(buttonText = "Next") {
                bottomSheetViewModel.plotDate = selectedDate
                bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning3
            }
        } else {
            CustomButton(buttonText = "Skip", backgroundColor = MaterialTheme.colors.background, textColor = ExtendedTheme.colors.Black60) {  bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning3 }
        }
    }
}

//https://stackoverflow.com/questions/22178349/android-how-to-filter-emoji-emoticons-from-a-string
//https://stackoverflow.com/questions/64181930/request-focus-on-textfield-in-jetpack-compose
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Screen1(closeSheet: () -> Unit, bottomSheetViewModel: BottomSheetViewModel) {
    var title by remember{
        mutableStateOf("")
    }
    var emoji = title.replace("[A-Za-z0-9 ]".toRegex(), "")

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = bottomSheetViewModel.bottomSheetType == BottomSheetType.Planning1){
        focusRequester.requestFocus()
    }


    Column(modifier = Modifier.padding(24.dp)) {
        if (emoji.isNotEmpty()){
            EmojiCircle(emoji = emoji)
            Spacer(modifier = Modifier.height(24.dp))
        }

        CustomTextField(helper = "Title (add an emoji)", hint = "What are you doing?", input = title.replace("[^A-Za-z0-9 ]".toRegex(), ""), onChange = {
            title = it
        }, modifier = Modifier.focusRequester(focusRequester), keyboardActions = KeyboardActions(onNext = {
            bottomSheetViewModel.plotEmoji = emoji
            bottomSheetViewModel.plotName = title.replace("[^A-Za-z0-9 ]".toRegex(), "")
            focusManager.clearFocus()
            bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning2
        }),)
    }
}

@Composable
fun HomePage(navController: NavController, userViewModel: UserViewModel, openSheet: () -> Unit){
    var bottomBarSelection by rememberSaveable{ mutableStateOf(0) }


    Box(modifier = Modifier.fillMaxSize()){
        when(bottomBarSelection){
            0 -> CalendarPage(navController = navController, userViewModel = userViewModel)
            1 -> ChatPage(navController = navController, userViewModel = userViewModel)
            2 -> AccountPage(navController = navController, userViewModel = userViewModel)
        }
        Column(
            Modifier
                .fillMaxWidth()
                .height(61.dp)
                .align(Alignment.BottomCenter)) {
            Divider(
                color = LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp)

            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = MaterialTheme.colors.background)) {

                IconButton(onClick = {bottomBarSelection = 0},
                    Modifier
                        .fillMaxSize()
                        .weight(1f)) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(id = R.drawable.ic_calendar_line),
                        contentDescription = null,
                        tint = if(bottomBarSelection == 0) Black else Black40

                    )
                }

                Spacer(Modifier.weight(1f))

                IconButton(onClick = {bottomBarSelection = 1},
                    Modifier
                        .fillMaxSize()
                        .weight(1f)) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(id = R.drawable.ic_chat_3_line),
                        contentDescription = null,
                        tint = if(bottomBarSelection == 1) Black else Black40
                    )
                }

            }



        }

        FloatingActionButton(onClick = { openSheet() }, Modifier
            .padding(bottom = 24.dp)
            .align(Alignment.BottomCenter)
            .size(56.dp)
        ) {
            Icon(
                modifier = Modifier.size(36.dp),
                painter = painterResource(id = R.drawable.ic_add_line),
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary
            )
        }

    }
}


