package com.ottogo.weekly

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.ottogo.weekly.api.models.ChatMessage
import com.ottogo.weekly.api.models.Plot
import com.ottogo.weekly.ui.account.*
import com.ottogo.weekly.ui.calendar.CalendarPage
import com.ottogo.weekly.ui.calendar.CreateCalendarPage
import com.ottogo.weekly.ui.calendar.plot.PlotPage
import com.ottogo.weekly.ui.chat.*
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.login.*
import com.ottogo.weekly.ui.plot.PlotDatePage
import com.ottogo.weekly.ui.theme.*
import com.ottogo.weekly.viewmodels.UserViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.io.File
import java.lang.Exception
import java.net.URI
import java.util.*
import java.util.concurrent.Executor
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation(userViewModel: UserViewModel, webSocket: WebSocketClient?){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "homePage") {

        composable("homePage") { HomePage(navController, userViewModel) }
        composable("searchPage") { SearchPage(navController, userViewModel) }
        composable("settingsPage") { SettingsPage(navController) }
        composable("reportPage") { ReportPage(navController, userViewModel) }
        composable("activitiesPage") { ActivitiesPage(navController, userViewModel) }
        composable("addActivityPage") { AddActivityPage(navController, userViewModel) }
        composable("contactsPage") { ContactsPage(navController, userViewModel) }
        composable("editProfilePage") { EditProfilePage(navController, userViewModel) }
        composable("createGroupPage") { CreateGroupPage(navController) }
        composable("groupPage/{group_id}") { backStackEntry -> GroupPage(navController, backStackEntry.arguments?.getInt("group_id")!!, userViewModel) }
        composable("createCalendarPage") { CreateCalendarPage(navController, userViewModel) }
        composable("plotDatePage") { PlotDatePage(navController) }
        composable("inviteGroupPage/{groupName}") { backStackEntry -> InviteGroupPage(navController,
            backStackEntry.arguments?.getString("groupName")!!,
            navController.previousBackStackEntry?.arguments?.getParcelable<Uri>("imageUri"),
            userViewModel
        ) }
        composable("plotPage/{plot_id}") { backStackEntry -> PlotPage(navController,
            backStackEntry.arguments?.getInt("plot_id")!!,
            userViewModel
        ) }
        composable("privateChatPage/{userId}") { backStackEntry -> PrivateChatPage(navController, userViewModel,
            backStackEntry.arguments?.getString("userId")!!.toInt(), webSocket
        ) }
        composable("webviewPage/{title}?url={url}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry -> WebViewPage(navController,
            backStackEntry.arguments?.getString("title")!!, backStackEntry.arguments?.getString("url")!!
        ) }



    }
}

@Composable
fun HomePage(navController: NavController, userViewModel: UserViewModel){
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
                .height(60.dp).background(color = MaterialTheme.colors.background)) {
                
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

                IconButton(onClick = {bottomBarSelection = 2}, modifier =
                Modifier
                    .fillMaxSize()
                    .weight(1f)) {
                    Box(contentAlignment = Alignment.Center, modifier =
                    Modifier
                        .fillMaxSize()) {

                        Card(
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp),
                            border = BorderStroke(3.dp,if (bottomBarSelection == 2) Black else White),
                            elevation = 0.dp

                        ){

                        }
                        ProfilePicture(
                            url = userViewModel.profile?.profile_picture,
                            size = 24
                        )



                    }
                }




            }
        }
    }
}



