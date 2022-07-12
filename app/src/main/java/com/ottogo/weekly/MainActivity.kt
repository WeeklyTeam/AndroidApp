package com.ottogo.weekly

import android.app.Activity
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.ui.account.*
import com.ottogo.weekly.ui.calendar.*
import com.ottogo.weekly.ui.calendar.availability.AddAvailabilityPage
import com.ottogo.weekly.ui.calendar.availability.AvailabilityPage
import com.ottogo.weekly.ui.calendar.plot.PlotPage
import com.ottogo.weekly.ui.calendar.ui.components.EmojiCircle
import com.ottogo.weekly.ui.chat.*
import com.ottogo.weekly.ui.chat.group.AddGroupMembersPage
import com.ottogo.weekly.ui.chat.group.EditGroupPage
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
import kotlinx.coroutines.launch
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

        setContent {

                WeeklyTheme {
                    // A surface container using the 'background' color from the theme


                        if (userViewModel.token == null) {
                            WindowCompat.setDecorFitsSystemWindows(window, false)

                            ProvideWindowInsets {
                                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                                    LoginNavigation(userViewModel = userViewModel)

                                }
                            }
                        } else {
                            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                                Column(modifier = Modifier.systemBarsPadding()) {
                                    MainNavigation(userViewModel = userViewModel, webSocket = webSocket)
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
                    val moshi = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).add(
                        KotlinJsonAdapterFactory()
                    ).build()
                    val adapter: JsonAdapter<ChatMessage> = moshi.adapter(ChatMessage::class.java)
                    val chatMessage = adapter.fromJson(message)
                    if (chatMessage != null) {
                        Log.d("WebSocket", chatMessage.timestamp.toString())

                    }
                    if (userViewModel.profile?.user_id != chatMessage?.user_id) {
                        userViewModel.friends?.get(chatMessage?.user_id)?.messages = listOf(chatMessage) as List<ChatMessage>
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
                    Log.d("WebSocket", ex.toString())

                }

            }

            (webSocket as WebSocketClient).connect()
        }
    }
//
//    fun sendWebSocketMessage(message: String){
//        webSocket!!.send(message)
//    }

    override fun onPause() {
        super.onPause()
        if (webSocket != null) {
            webSocket!!.close()
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
                BottomSheetType.TYPE2 -> {
                    bottomSheetViewModel.bottomSheetType = BottomSheetType.TYPE1
                }
                BottomSheetType.TYPE3 -> {
                    bottomSheetViewModel.bottomSheetType = BottomSheetType.TYPE2
                }
                else -> {

                    closeSheet()

                }
            }
        } else {
            if (navController.currentDestination?.route == "homepage") {
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
            Spacer(modifier = Modifier.height(1.dp))
            bottomSheetViewModel.bottomSheetType?.let {
                SheetLayout(
                    closeSheet = {
                        closeSheet()
                    },
                    bottomSheetViewModel = bottomSheetViewModel,
                    userViewModel = userViewModel,
                    bottomSheetType = it)
            }
        },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
        // TODO: change startDestination to homePage
        NavHost(navController = navController, startDestination = "homePage") {

            composable("homePage") { HomePage(navController, userViewModel) {
                bottomSheetViewModel.bottomSheetType = BottomSheetType.TYPE1
                openSheet() }
            }
            composable("searchPage") { SearchPage(navController, userViewModel) }
            composable("settingsPage") { SettingsPage(navController) }
            composable("reportPage") { ReportPage(navController, userViewModel) }
            composable("activitiesPage") { ActivitiesPage(navController, userViewModel) }
            composable("addActivityPage") { AddActivityPage(navController, userViewModel) }
            composable("contactsPage") { ContactsPage(navController, userViewModel) }
            composable("editProfilePage") { EditProfilePage(navController, userViewModel) }
            composable("createGroupPage") { CreateGroupPage(navController) }
            composable("addAvailabilityPage") { AddAvailabilityPage(navController, userViewModel) }
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
                    backStackEntry.arguments?.getString("plot_id")!!.toInt(),
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

}

enum class BottomSheetType() {
    TYPE1, TYPE2, TYPE3
}

@Composable
fun SheetLayout(
    bottomSheetType: BottomSheetType,
    bottomSheetViewModel: BottomSheetViewModel,
    userViewModel: UserViewModel,
    closeSheet : () -> Unit
){

    when(bottomSheetType){
        BottomSheetType.TYPE1 -> Screen1(closeSheet, bottomSheetViewModel)
        BottomSheetType.TYPE2 -> Screen2(closeSheet, bottomSheetViewModel)
        BottomSheetType.TYPE3 -> Screen3(closeSheet, userViewModel)
    }

}

@Composable
fun Screen3(closeSheet: () -> Unit, userViewModel: UserViewModel) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
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

            userViewModel.groupsOrder?.forEach { index ->
                userViewModel.groups!![index]?.let { SelectGroupItem(group = it, selected = false, modifier = Modifier
                    .clickable {}
                    .padding(horizontal = 8.dp)) }
            }

            Text("Friends", style = MaterialTheme.typography.h4, modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp), color = ExtendedTheme.colors.Black60)

            userViewModel.friendsOrder?.forEach { index ->
                userViewModel.friends!![index]?.let { SelectProfileItem(profile = it, selected = false, modifier = Modifier
                    .clickable {}
                    .padding(horizontal = 8.dp)) }
            }
        }

        Divider(color = ExtendedTheme.colors.LightGray, thickness = 1.dp)

        CustomButton(buttonText = "Invite", onClick = {}, modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 18.dp))
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
            ScrollPicker(options = listOf(List(12){ index -> (index+1).toString()}, listOf("AM", "PM")))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!displayCalendar) {
            CustomButton(buttonText = "Next") { bottomSheetViewModel.bottomSheetType = BottomSheetType.TYPE3 }
        } else {
            CustomButton(buttonText = "Skip", backgroundColor = MaterialTheme.colors.background, foregroundColor = ExtendedTheme.colors.Black60) {  bottomSheetViewModel.bottomSheetType = BottomSheetType.TYPE3 }
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
    var emoji by remember{
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = bottomSheetViewModel.bottomSheetType == BottomSheetType.TYPE1){
        focusRequester.requestFocus()
    }


    Column(modifier = Modifier.padding(24.dp)) {
        if (emoji.isNotEmpty()){
            EmojiCircle(emoji = emoji)
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        CustomTextField(helper = "Title", hint = "What are you doing?", input = emoji, onChange = {
            emoji = it
        }, modifier = Modifier.focusRequester(focusRequester), keyboardActions = KeyboardActions(onNext = {
            focusManager.clearFocus()
            bottomSheetViewModel.bottomSheetType = BottomSheetType.TYPE2
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

                IconButton(onClick = { openSheet() },
                    Modifier
                        .fillMaxSize()
                        .weight(1f)) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(id = R.drawable.ic_add_circle_line),
                        contentDescription = null,
                        tint = Black40
                    )
                }

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

//                IconButton(onClick = {bottomBarSelection = 2}, modifier =
//                Modifier
//                    .fillMaxSize()
//                    .weight(1f)) {
//                    Box(contentAlignment = Alignment.Center, modifier =
//                    Modifier
//                        .fillMaxSize()) {
//
//                        Card(
//                            shape = CircleShape,
//                            modifier = Modifier.size(38.dp),
//                            border = BorderStroke(3.dp,if (bottomBarSelection == 2) Black else White),
//                            elevation = 0.dp
//
//                        ){
//
//                        }
//                        ProfilePicture(
//                            url = userViewModel.profile?.profile_picture,
//                            size = 24
//                        )
//
//
//
//                    }
//                }




            }
        }
    }
}



