package com.ottogo.weekly

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ottogo.weekly.ui.account.AccountPage
import com.ottogo.weekly.ui.calendar.CalendarPage
import com.ottogo.weekly.ui.chat.ChatPage
import com.ottogo.weekly.ui.chat.PlotEditPage
import com.ottogo.weekly.ui.chat.SearchPage
import com.ottogo.weekly.ui.login.*
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.Black40
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.ui.theme.WeeklyTheme
import com.ottogo.weekly.viewmodels.UserViewModel


class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    if (userViewModel.token == null) {
                        LoginNavigation()
                    } else {
                        MainNavigation(userViewModel = userViewModel)
                    }




                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "landingPage") {

        composable("landingPage") { LandingPage(navController) }
        composable( "loginPage") { LoginPage(navController) }
        composable("signupPage/{dob}") { backStackEntry -> SignupPage(navController,
            backStackEntry.arguments?.getString("dob")!!)
        }
        composable("signupInterestsPage/{token}") { backStackEntry -> SignupInterestsPage(navController,
            backStackEntry.arguments?.getString("token")!!
        ) }
        composable("signupProfilePage/{token}") { backStackEntry -> SignupProfilePage(navController,
            backStackEntry.arguments?.getString("token")!!
        ) }
        composable("signupBirthdayPage") { SignupBirthdayPage(navController) }
        composable("signupVerifyPage/{token}") { backStackEntry -> SignupVerifyPage(navController,
            backStackEntry.arguments?.getString("token")!!
        ) }
        /*...*/
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation(userViewModel: UserViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "homePage") {

        composable("homePage") { HomePage(navController, userViewModel) }
        composable("searchPage") { SearchPage(navController, userViewModel)}
        composable("plotEditPage") { PlotEditPage(navController, userViewModel) }

    }
}

@Composable
fun HomePage(navController: NavController, userViewModel: UserViewModel){
    var bottomBarSelection by rememberSaveable{ mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween){
        when(bottomBarSelection){
           0 -> CalendarPage(navController = navController, userViewModel = userViewModel)
           1 -> ChatPage(navController = navController, userViewModel = userViewModel)
           2 -> AccountPage(navController = navController, userViewModel = userViewModel)
        }
        Column(Modifier.fillMaxWidth()) {
            Divider(
                color = LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1.dp)
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)) {
                
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




            }
        }
    }
}



