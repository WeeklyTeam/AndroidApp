package com.ottogo.weekly.ui.account

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.Message
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.login.getFile
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditProfilePage(navController: NavController, userViewModel: UserViewModel) {

    val context = LocalContext.current
    var error: String? by remember { mutableStateOf(null) }
    var name: String by remember { mutableStateOf(userViewModel.profile?.name ?: "") }
    var username: String by remember { mutableStateOf(userViewModel.profile?.username ?: "") }
    var imageUri: Uri? by remember { mutableStateOf(null) }
    var file: File? by remember { mutableStateOf(null) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val storagePermissionStatus = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
                uri: Uri? -> imageUri = uri
            if (imageUri != null) {
                file = getFile(imageUri = imageUri, context)
                bitmap = BitmapFactory.decodeFile(file?.path)
            }
        }
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {


        TitleBar(navController = navController, title = "Edit Profile")

        Divider(color = ExtendedTheme.colors.LightGray, thickness = 1.dp)

        Column(Modifier
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally){

            if (error != null) {
                Spacer(modifier = Modifier.padding(32.dp))
                Message(error.toString())
            }

            Spacer(modifier = Modifier.height(32.dp))


            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .clickable(
                            enabled = true,
                            onClickLabel = "Choose Profile Image",
                            onClick = {
                                galleryLauncher.launch("image/")
                            }
                        )
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_profile_picture),
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .clickable(
                            enabled = true,
                            onClickLabel = "Choose Profile Image",
                            onClick = {
                                when (storagePermissionStatus.status) {
                                    // If the camera permission is granted, then show screen with the feature enabled
                                    PermissionStatus.Granted -> {
                                        galleryLauncher.launch("image/")
                                    }
                                    is PermissionStatus.Denied -> {
                                        storagePermissionStatus.launchPermissionRequest()

                                    }
                                }

                            }
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextField(
                helper = "Name",
                hint = "Name",
                input = name,
                onChange = { name = it },
                done = true,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))

            CustomTextField(
                helper = "Username",
                hint = "Username",
                input = username,
                done = true,
                onChange = { username = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            CustomButton(
                buttonText = "Save",
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    try {
                        val profileUsername = username.toRequestBody("text/plain".toMediaTypeOrNull())
                        val profileName = name.toRequestBody("text/plain".toMediaTypeOrNull())
                        var picture: MultipartBody.Part? = null

                        if (file != null) {
                            val reqFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                            picture = MultipartBody.Part.createFormData(
                                "profile_picture",
                                file?.name, reqFile
                            )
                        }
                        userViewModel.profile = WeeklyApi.retrofitService.patchProfile(
                            mapOf("Authorization" to "token ${userViewModel.token}"),
                            mapOf("name" to profileName, "username" to profileUsername),
                            picture
                        )

                        navController.navigateUp()

                    } catch (e: Exception) {
                        if (e is HttpException) {
                            if (e.code() >= 400) {
                                error = "Issue Editing Profile.\n Please Try again."
                            }
                        }
                    }
                })

            Spacer(modifier = Modifier.height(32.dp))

        }
    }

}

