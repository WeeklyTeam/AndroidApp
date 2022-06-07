package com.ottogo.weekly.ui.account

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.account.ui.components.AccountOption
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.theme.ExtendedTheme

@Composable
fun SettingsPage(navController: NavController){
    val context = LocalContext.current

    Column() {
        TitleBar(navController = navController, title = "Settings")

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        AccountOption(icon = R.drawable.ic_file_info_line, text = "Privacy Policy") {
            navController.navigate("webviewPage/Privacy Policy?url=https://www.privacypolicies.com/live/879e93a8-0691-459f-85fa-1d1a4c56bf12")
        }

        AccountOption(icon = R.drawable.ic_file_text_line, text = "EULA") {
            navController.navigate("webviewPage/EULA?url=https://www.privacypolicies.com/live/eed0418f-1191-4c07-8354-ffd904340564")
        }

        AccountOption(icon = R.drawable.ic_file_list_3_line, text = "Open Source") {
            //https://github.com/google/play-services-plugins/issues/100
            com.google.android.gms.internal.oss_licenses.zzf.dummy_placeholder = context.resources.getIdentifier("third_party_license_metadata", "raw", context.packageName)
            context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))

        }

    }
}