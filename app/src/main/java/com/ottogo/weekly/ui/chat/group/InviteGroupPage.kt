package com.ottogo.weekly.ui.chat

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.ottogo.weekly.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.rememberPermissionState
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.models.Profile
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.login.getFile
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File



@Composable
fun InviteGroupPage(navController: NavController, name: String, imageUri: Uri?, userViewModel: UserViewModel) {

    val selectedIds = remember {
        mutableStateListOf<Int>()
    }

    Column {
        TitleBar(navController = navController, title = "Invite")
        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(8.dp))

            userViewModel.friends?.forEach { (userId, profile) ->
                var selected = selectedIds.contains(userId)
                SelectProfileItem(profile = profile, selected = selected, modifier = Modifier.clickable{
                    if (selected){
                        selectedIds.remove(userId)
                    } else {
                        selectedIds.add(userId)
                    }
                })
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(Modifier.weight(1F))

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        CustomButton(buttonText = "Create", onClick = {
            // TODO: create group request 
        }, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 12.dp))


    }
}

@Composable
fun SelectProfileItem(profile: Profile, selected: Boolean, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        ProfilePicture(url = profile.profile_picture, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        Text(text = profile.name, style = MaterialTheme.typography.body1)

        Spacer(Modifier.weight(1F))

        Icon(
            painter = painterResource(id = if (selected){ R.drawable.ic_checkbox_circle_fill } else { R.drawable.ic_checkbox_blank_circle_line }),
            tint = if (selected){ MaterialTheme.colors.primary } else { ExtendedTheme.colors.Black60 },
            contentDescription = "checkbox",
            modifier = Modifier.padding(16.dp)
        )


    }
}

//try {
//    val profileName = name.toRequestBody("text/plain".toMediaTypeOrNull())
//    var picture: MultipartBody.Part? = null
//
//    if (file != null) {
//        val reqFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
//        picture = MultipartBody.Part.createFormData(
//            "profile_picture",
//            file?.name, reqFile
//        )
//    }
//
//    userViewModel.profile = WeeklyApi.retrofitService.patchProfile(
//        mapOf("Authorization" to "token ${userViewModel.token}"),
//        mapOf("name" to profileName),
//        picture
//    )
//} catch (e: Exception) {
//    if (e is HttpException) {
//        if (e.code() >= 400) {
//            error = "Issue Editing Profile.\n Please Try again."
//        }
//    }
//}