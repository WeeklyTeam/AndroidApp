package com.ottogo.weekly

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ottogo.weekly.ui.login.*
import com.ottogo.weekly.ui.theme.WeeklyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeeklyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginNavigation()
                }
            }
        }
    }

    //added code from Linda 3/20
    @Composable
    fun WeeklyTheme(){
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "login_page", builder = {
            composable("login_page", content = { LoginPage(navController = navController) })
        })
    }
}

@Preview
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
        composable("signupBirthdayPage") { SignupBirthdayPage(navController)}
        composable("signupVerifyPage/{token}") { backStackEntry -> SignupVerifyPage(navController,
            backStackEntry.arguments?.getString("token")!!
        ) }
        /*...*/
    }
}

class MyNonActivityClass     //save the context received via constructor in a local variable
    (  // variable to hold context
    private val context: Context
)



