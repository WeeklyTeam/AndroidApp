package com.ottogo.weekly.ui.calendar.sync

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.account.ui.components.AccountOption
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import java.util.*


@Composable
fun CalendarSyncPage(navController: NavController) {

    val context = LocalContext.current


    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("GoogleSignIn", result.resultCode.toString())
            Log.d("GoogleSignIn", Activity.RESULT_OK.toString())
            Log.d("GoogleSignIn", result.data?.extras.toString())

            //if (result.resultCode == Activity.RESULT_OK) {
            Log.d("GoogleSignIn", "OK")
            val intent = result.data
            if (result.data != null) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(intent)
                handleSignInResult(task);
                Log.d(task.toString(), "OK")
                Log.d("GoogleSignIn", task.toString())
            }
            //}
        }

    Column {
        TitleBar(navController = navController, title = "Options")
        Divider(color = ExtendedTheme.colors.LightGray, thickness = 1.dp)

        AccountOption(R.drawable.ic_canvas_bug_color_rgb, "Canvas") {
            // Daniel
            // Navigate to CanvasWebviewPage to preform Canvas oauth request (you can add your navigation by going to MainActivity and finding mainnavigation)
            // follow there for more instructions
        }

        AccountOption(R.drawable.ic_google_calendar_icon__2020_, "Google Calendar") {
            // Justin
            // Preform oauth request
            // This https://developers.google.com/identity/sign-in/android/start-integrating
            // then this https://developers.google.com/identity/sign-in/android/sign-in
            // finally navigate to the AddPlotsPage.kt (you can add your navigation by going to MainActivity and finding mainnavigation)
            // follow there for more instructions
            //navController.navigate("canvasWebviewPage")
            startForResult.launch(getGoogleLoginAuth(context).signInIntent)

        }

        AccountOption(R.drawable.ic_calendar_line, "Device") {

        }


    }
}

private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
    try {
        val account = completedTask.getResult(
            ApiException::class.java
        )
        Log.d("GoogleSignIn", account.email.toString())
        // Signed in successfully, show authenticated UI.
    } catch (e: ApiException) {
        // The ApiException status code indicates the detailed failure reason.
        // Please refer to the GoogleSignInStatusCodes class reference for more information.
        Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
    }
}



fun getGoogleLoginAuth(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestId()
        .requestIdToken(getString(R.string.server_client_id))
        .requestProfile()
        .build()

//    return GoogleSignIn.requestPermissions(
//        context,
//        RC_REQUEST_PERMISSION_SUCCESS_CONTINUE_FILE_CREATION,
//        GoogleSignIn.getClient(context, gso),
//    );

    return GoogleSignIn.getClient(context, gso)
}