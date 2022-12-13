package com.ottogo.weekly


import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.account.*
import com.ottogo.weekly.ui.bottomModals.ProfileBottomModalSheet
import com.ottogo.weekly.ui.calendar.*
import com.ottogo.weekly.ui.calendar.DateFunctions.addMonth
import com.ottogo.weekly.ui.calendar.DateFunctions.initialMonth
import com.ottogo.weekly.ui.calendar.availability.AddAvailabilityPage
import com.ottogo.weekly.ui.calendar.plot.PlotEditPage
import com.ottogo.weekly.ui.calendar.plot.PlotPage
import com.ottogo.weekly.ui.calendar.ui.components.CalendarComponent
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.chat.*
import com.ottogo.weekly.ui.chat.group.AddGroupMembersPage
import com.ottogo.weekly.ui.chat.group.EditGroupPage
import com.ottogo.weekly.ui.chat.group.GroupChatPage
import com.ottogo.weekly.ui.chat.group.GroupPage
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.ScrollPicker
import com.ottogo.weekly.ui.login.*
import com.ottogo.weekly.ui.plot.PlotDatePage
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.onesignal.OneSignal
import com.ottogo.weekly.ui.bottomModals.EmojiSheet
import com.ottogo.weekly.ui.calendar.plot.AddPlotMembersPage
import com.ottogo.weekly.ui.calendar.plot.NewPlotsPage

const val ONESIGNAL_APP_ID = "2262537a-7d61-4fac-b35d-5c8f27a9f578"

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private var webSocket: WebSocketClient? = null
    val executorService: ExecutorService = Executors.newFixedThreadPool(4)


    val ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE"
    val ARG_AUTH_TYPE = "AUTH_TYPE"
    val ARG_ACCOUNT_NAME = "ACCOUNT_NAME"
    val ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        createNotificationChannels()

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )

        val appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    200)
            }
        }

        userViewModel.getToken(context = applicationContext)




        setContent {

                WeeklyTheme {


                        if (userViewModel.token == null) {
                            WindowCompat.setDecorFitsSystemWindows(window, false)

                            ProvideWindowInsets {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colors.background
                                ) {
                                    LoginNavigation(userViewModel = userViewModel, notificationManager = notificationManager)

                                }
                            }
                        } else {
                            webSocketCreate()

                            WindowCompat.setDecorFitsSystemWindows(window, true)

                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colors.background
                            ) {
                                Column(modifier = Modifier) {
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

    override fun onDestroy() {
        //Connor
        //By using android lifcycle you can set the users version to update once they stop using the app, put it to sleep
        //aka set the shared preferences verision value to the current version so we can use this later
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        if (!userViewModel.token.isNullOrBlank()) {

            webSocket!!.reconnect()


            userViewModel.getMain()




        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannels(){
        val name = "Reminders"
        val description = "Plan reminder notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            "WeeklyPlotReminders", name, importance
        )
        channel.description = description
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )

        notificationManager.createNotificationChannel(channel)

        val name3 = "Announcements"
        val description3 = "Updates and new features"
        val importance3 = NotificationManager.IMPORTANCE_HIGH
        val channel3 = NotificationChannel(
            "WeeklyAnnouncements", name, importance
        )
        channel.description = description3

        notificationManager.createNotificationChannel(channel3)

        val name2 = "Friends"
        val description2 = "RSVPS, friend requests, invites, etc..."
        val importance2 = NotificationManager.IMPORTANCE_HIGH
        val channel2 = NotificationChannel(
            "WeeklyFriendNotifications", name, importance
        )
        channel.description = description2

        notificationManager.createNotificationChannel(channel2)
    }

    fun webSocketCreate(){
        val headers = mapOf("authorization" to "token ${userViewModel.token}")

        val uri: URI? = URI("wss://www.theweeklyapp.com/chat/")

        webSocket = object : WebSocketClient(uri, headers) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("WebSocket", "Connected")
            }

            override fun onMessage(message: String?) {

                Log.d("WebSocket", message.toString())
                val moshi = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).add(
                    KotlinJsonAdapterFactory()
                ).build()
                val adapter: JsonAdapter<ChatMessage> = moshi.adapter(ChatMessage::class.java)
                val chatMessage = adapter.fromJson(message)
                if (chatMessage != null) {
                    Log.d("WebSocket", chatMessage.toString())

                    if (userViewModel.profile?.user_id != chatMessage.user_id) {
                        userViewModel.addMessage(chatMessage)

                    }

                }

            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("WebSocket", "Closed")
                if (reason != null) {
                    Log.d("WebSocket", reason)
                }


            }

            override fun onError(ex: Exception?) {
                Log.d("WebSocket", "Error")
                Log.d("WebSocket", ex?.stackTraceToString() ?: "")


            }

        }

        (webSocket as WebSocketClient).connect()
    }

    override fun onPause() {
        super.onPause()
        if (webSocket != null) {
            webSocket!!.close()
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginNavigation(userViewModel: UserViewModel, notificationManager: NotificationManager){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "landingPage") {
        composable("signupNotificationPage/{token}/{username}") { backStackEntry -> SignupNotificationPage(navController,
            backStackEntry.arguments?.getString("token")!!,
            backStackEntry.arguments?.getString("username")!!,
            userViewModel
        ) }
        composable("landingPage") { LandingPage(navController) }
        composable( "loginPage") { LoginPage(navController, userViewModel) }
        composable("signupPage/{dob}") { backStackEntry -> SignupPage(navController,
            backStackEntry.arguments?.getString("dob")!!)
        }
        composable("signupInterestsPage/{token}") { backStackEntry -> SignupInterestsPage(navController,
            backStackEntry.arguments?.getString("token")!!,
            userViewModel
        ) }
        composable("signupProfilePage/{token}/{username}") { backStackEntry -> SignupProfilePage(navController,
            backStackEntry.arguments?.getString("token")!!,
            backStackEntry.arguments?.getString("username")!!,
            notificationManager,
            userViewModel
        ) }
        composable("signupBirthdayPage") { SignupBirthdayPage(navController) }
        composable("signupVerifyPage/{token}/{username}") { backStackEntry -> SignupVerifyPage(navController,
            backStackEntry.arguments?.getString("token")!!,
            backStackEntry.arguments?.getString("username")!!
        ) }
        composable("webviewPage/{title}?url={url}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry ->
            Column(modifier = Modifier.systemBarsPadding()) {
                WebViewPage(
                    navController,
                    backStackEntry.arguments?.getString("title")!!,
                    backStackEntry.arguments?.getString("url")!!
                )
            }
        }

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


    val closeSheet: () -> Unit  = {
        //focusManager.clearFocus()
        keyboardController?.hide()
        bottomSheetViewModel.clear()
        scope.launch { modalBottomSheetState.hide() }
    }

    LaunchedEffect(key1 = userViewModel.token, block = {
        userViewModel.clear()
        Log.d("load", "load")
        userViewModel.getMain()


    })

    val openEmoji: () -> Unit = {
        scope.launch {

                bottomSheetViewModel.bottomSheetType = BottomSheetType.Emoji
                modalBottomSheetState.show()


            }
    }

    val openSheet: (profile: Profile?) -> Unit = { it
        scope.launch {
            if (it is Profile){
                if (it.user_id != userViewModel.profile?.user_id) {
                    bottomSheetViewModel.profile = it
                    bottomSheetViewModel.bottomSheetType = BottomSheetType.Profile
                    modalBottomSheetState.show()

                }
            }

            else {
                bottomSheetViewModel.clear()
                bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning1
                modalBottomSheetState.show()
            }
        }
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
                    userViewModel = userViewModel,
                )
            }
        },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
        NavHost(navController = navController, startDestination = "homePage") {

            composable("homePage") { HomePage(navController, userViewModel, openSheet, openEmoji, closeSheet, bottomSheetViewModel)
            }
            composable("searchPage") { SearchPage(navController, userViewModel, openSheet) }
            composable("settingsPage") { SettingsPage(userViewModel, navController) }
            composable("reportPage") { ReportPage(navController, userViewModel) }
            composable("activitiesPage") { ActivitiesPage(navController, userViewModel) }
            composable("addActivityPage") { AddActivityPage(navController, userViewModel) }
            composable("contactsPage") { ContactsPage(navController, userViewModel) }
            composable("editProfilePage") { EditProfilePage(navController, userViewModel) }
            composable("createGroupPage") { CreateGroupPage(navController) }
            composable("addAvailabilityPage") { AddAvailabilityPage(navController, userViewModel, openEmoji, closeSheet, bottomSheetViewModel) }
            composable("newPlotsPage") { NewPlotsPage(navController, userViewModel) }

            composable("chatSearchPage") { ChatSearchPage(navController, userViewModel) }
            composable("groupPage/{group_id}") { backStackEntry ->
                GroupPage(
                    navController,
                    backStackEntry.arguments?.getString("group_id")!!.toInt(),
                    userViewModel,
                    openSheet
                )
            }
            composable("editGroupPage/{group_id}") { backStackEntry ->
                EditGroupPage(
                    navController,
                    backStackEntry.arguments?.getString("group_id")!!.toInt(),
                    userViewModel,
                )
            }
            composable("groupAddMembersPage/{group_id}") { backStackEntry ->
                AddGroupMembersPage(
                    navController,
                    backStackEntry.arguments?.getString("group_id")!!.toInt(),
                    userViewModel,
                )
            }
            composable("groupChatPage/{group_id}") { backStackEntry ->
                GroupChatPage(
                    navController,
                    userViewModel,
                    backStackEntry.arguments?.getString("group_id")!!.toInt(),
                    webSocket,
                    openSheet
                )
            }
            composable("accountPage") { AccountPage(navController, userViewModel) }
            composable("plotDatePage") { PlotDatePage(navController) }
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
                    backStackEntry.arguments?.getString("plot_id")!!.toInt(),
                    userViewModel,
                    openSheet
                )
            }
            composable("plotEditPage/{plot_id}") { backStackEntry ->
                PlotEditPage(
                    navController,
                    backStackEntry.arguments?.getString("plot_id")!!.toInt(),
                    userViewModel,
                    openEmoji, closeSheet, bottomSheetViewModel
                )
            }
            composable("addPlotMembersPage/{plot_id}") { backStackEntry ->
                AddPlotMembersPage(
                    navController,
                    backStackEntry.arguments?.getString("plot_id")!!.toInt(),
                    userViewModel,
                )
            }
            composable("privateChatPage/{userId}") { backStackEntry ->
                PrivateChatPage(
                    navController, userViewModel,
                    backStackEntry.arguments?.getString("userId")!!.toInt(), webSocket,
                    openSheet = openSheet
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

    fun clear(){
        bottomSheetType = null
        profile = null
        plotEmoji = null
        plotName = null
        plotDate = null
    }

}

enum class BottomSheetType() {
    Planning1, Planning2, Planning3, Profile, Emoji
}

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
        BottomSheetType.Emoji -> EmojiSheet(bottomSheetViewModel = bottomSheetViewModel)
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
    val selectedProfileIds = remember {
        mutableStateListOf<Int>()
        mutableStateListOf<Int>()
    }

    val context = LocalContext.current


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
                            selectedProfileIds.clear()
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
                            selectedProfileIds.add(friend.user_id)
                            selectedGroupId = null
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
                    selectedProfileIds.count() > 1 -> {
                        body["invited"] = selectedProfileIds
                    }
                }
                val plot = WeeklyApi.retrofitService.createPlot(
                    mapOf("Authorization" to "token ${userViewModel.token}"),
                    body
                )

                userViewModel.addPlot(plot, context)
                closeSheet()
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


    var displayMonth by rememberSaveable {
        mutableStateOf(initialMonth())
    }

    Log.d("dateButtons", displayMonth.toString())

    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = { displayMonth = addMonth(displayMonth, -1); selectedDate = displayMonth }, modifier = Modifier.size(56.dp)) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_left_s_line),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                SimpleDateFormat(if (displayCalendar) {"MMM yyyy"} else {"MMM dd"}).format(displayMonth),
                modifier = Modifier.weight(1F), style = MaterialTheme.typography.h2, textAlign = TextAlign.Center)

            IconButton(onClick = { displayMonth = addMonth(displayMonth, 1); selectedDate = displayMonth }, modifier = Modifier.size(56.dp)) {
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
                if (it != null) {
                    displayMonth = it
                }
            })
        }
        else {
            ScrollPicker(options = listOf(List(12){ index -> String.format("%02d", (index+1))}, List(60){ index -> String.format("%02d", (index))}, listOf("AM", "PM")), selectItem = listOf(
                {   it ->
                    Log.d("selector", it.toString())
                        val calendar = Calendar.getInstance()
                        calendar.time = selectedDate
                        calendar[Calendar.HOUR] = it+1
                        selectedDate = calendar.time

                },
                {   it ->
                    Log.d("selector", it.toString())
                    val calendar = Calendar.getInstance()
                    calendar.time = selectedDate
                    calendar[Calendar.MINUTE] = it
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
        mutableStateOf(bottomSheetViewModel.plotName ?: "")
    }
    var emoji by remember{
        mutableStateOf(bottomSheetViewModel.plotEmoji ?: "\uD83C\uDF0A")
    }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = bottomSheetViewModel.bottomSheetType == BottomSheetType.Planning1){
        focusRequester.requestFocus()
        bottomSheetViewModel.plotName = ""
    }


    LaunchedEffect(key1 = title){
        if(title.contains("[^A-Za-z0-9 ]".toRegex())){
            emoji = title.replace("[A-Za-z0-9 ]".toRegex(), "")
        }
    }




    Column(modifier = Modifier.padding(24.dp)) {
        EmojiCircle(emoji = emoji, onClick = {
            bottomSheetViewModel.bottomSheetType = BottomSheetType.Emoji
        })
        Spacer(modifier = Modifier.height(24.dp))

        
        CustomTextField(
            helper = "Title (add an emoji)",
            hint = "What are you doing?",
            input = title.replace("[^A-Za-z0-9 ]".toRegex(), ""),
            onChange = {
                title = it
                bottomSheetViewModel.plotName = title.replace("[^A-Za-z0-9 ]".toRegex(), "")
            },
            modifier = Modifier.focusRequester(focusRequester),
            keyboardActions = KeyboardActions(onNext = {
                bottomSheetViewModel.plotEmoji = emoji
                bottomSheetViewModel.plotName = title.replace("[^A-Za-z0-9 ]".toRegex(), "")
                focusManager.clearFocus()
                bottomSheetViewModel.bottomSheetType = BottomSheetType.Planning2
            }),
        )
    }
}

@Composable
fun HomePage(navController: NavController, userViewModel: UserViewModel, openSheet: (profile: Profile?) -> Unit, openEmoji: () -> Unit, closeSheet: () -> Unit, bottomSheetViewModel: BottomSheetViewModel){
    var bottomBarSelection by rememberSaveable{ mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()){
        when(bottomBarSelection){
           0 -> CalendarPage(navController = navController, userViewModel = userViewModel, openEmoji, closeSheet, bottomSheetViewModel)
           1 -> ChatPage(navController = navController, userViewModel = userViewModel, openSheet = openSheet)
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

        FloatingActionButton(onClick = { openSheet(null) }, Modifier
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



