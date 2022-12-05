package com.ottogo.weekly.ui.calendar.sync

import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt.AuthenticationResult
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.Shapes
import com.ottogo.weekly.viewmodels.UserViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GoogleAuthentication(navController: NavController) {
    
    val coroutineScope = rememberCoroutineScope()
    var text by remember {
        mutableStateOf<String?>(null)
    }
    val signInRequestCode = 1
    
    Column {
        TitleBar(navController = navController, title = "Google Authentication")
        Divider(color = ExtendedTheme.colors.LightGray, thickness = 1.dp)
        SignInButton(
            text = "Sign Up with Google",
            onClick = {

            }
        )
    }
}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}


@ExperimentalMaterialApi
@Composable
fun SignInButton(
    text: String,
    loadingText: String = "Signing in...",
    icon: Int = R.drawable.ic_google_icon,
    isLoading: Boolean = false,
    borderColor: Color = Color.LightGray,
    backgroundColor: Color = MaterialTheme.colors.surface,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(
            enabled = !isLoading,
            onClick = onClick
        ),
        shape = Shapes.medium,
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "SignInButton",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(text = if (isLoading) loadingText else text)
            if (isLoading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )
            }
        }
    }
}

/*
@Composable
fun SignInButton(
    modifier: Modifier = Modifier,
    text: String = "Sign Up with Google",
    loadingText: String = "Creating Account...",
    icon: Int = R.drawable.ic_google_icon,
    borderColor: Color = Color.LightGray,
    backgroundColor: Color = MaterialTheme.colors.surface,
    progressIndicatorColor: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.clickable { clicked = !clicked },
        shape = Shapes.medium,
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Google Button",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (clicked) loadingText else text)
            if (clicked) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )
                onClick()
            }
        }
    }
}

 */


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
private fun GoogleButtonPreview() {
    SignInButton(
        text = "Sign Up with Google",
        loadingText = "Creating Account...",
        onClick = {}
    )
}
