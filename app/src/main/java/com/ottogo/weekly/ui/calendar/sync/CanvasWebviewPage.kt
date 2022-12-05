package com.ottogo.weekly.ui.account

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme


//https://www.geeksforgeeks.org/webview-in-android-using-jetpack-compose/
@Composable
fun CanvasWebviewPage(navController: NavController){

    Column() {
        TitleBar(navController = navController, title = "GoogleAuth")

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)


        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = WebViewClient()
                loadUrl("https://www.googleapis.com/auth/calendar.readonly?client_id=325137562061-gvjgho17o4tnnu7tf4b8d0fgk2b5b8kp.apps.googleusercontent.com")
            }
        }, update = {
            it.loadUrl("https://www.googleapis.com/auth/calendar.readonly?client_id=325137562061-gvjgho17o4tnnu7tf4b8d0fgk2b5b8kp.apps.googleusercontent.com")
        })
    }
}