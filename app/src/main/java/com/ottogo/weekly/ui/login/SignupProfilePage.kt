package com.ottogo.weekly.ui.login

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.ui.components.Message
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.lang.Exception

/*
*
* Built by: Juan
*
* follow the specifications of the figma file, note the spacing between elements,
* This one may be a little tricky don't hesitate to ask for help or clarification
* when a user taps on the grey circle, an image picker activity should launch up
* when selected show the user the image they picked,
* and send a multipart patch request to the my profile url
* with a header map using the token that was passed
* afterwards navigate to the signup interests page and pass the token
* Try your best!
*
* to test this in previews you can use the token 8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7
*
* */

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignupProfilePage(navController: NavController, token: String = "8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7") {
    var error : String? by remember { mutableStateOf(null) }
    var name : String by remember { mutableStateOf("") }
    var imageUri: Uri? by remember { mutableStateOf(null) }
    var file: File? by remember { mutableStateOf(null) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val storagePermissionStatus = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
                uri: Uri? -> imageUri = uri
            if (imageUri != null) {
                file = getFile(imageUri = imageUri!!, context)
                bitmap = BitmapFactory.decodeFile(file?.path)
            }
        }
    )

    LoginTitle(navController = navController, title = "Profile")
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        if (error != null) {
            Spacer(modifier = Modifier.padding(32.dp))
            Message(error.toString())
        }


        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = 32.dp)
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
                painter =  painterResource(id = R.drawable.default_profile_picture),
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = 32.dp)
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

        CustomTextField(helper = "Name", hint = "Name", input = name , onChange = {name = it} )
        Spacer(modifier = Modifier.padding(bottom = 32.dp))
        CustomButton(
            buttonText = "Next",
            onClick = {
                try {
                    val profileName = name.toRequestBody("text/plain".toMediaTypeOrNull())
                    var picture: MultipartBody.Part? = null

                    if (file != null) {
                        val reqFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                        picture = MultipartBody.Part.createFormData(
                            "profile_picture",
                            file?.name, reqFile
                        )
                    }

                    WeeklyApi.retrofitService.patchProfile(
                        mapOf("Authorization" to "token $token"),
                        mapOf("name" to profileName),
                        picture
                    )
                } catch (e: Exception) {
                    if (e is HttpException) {
                        if (e.code() >= 400) {
                            error = "Issue Signing up.\n Please Try again."
                        }
                    }
                    navController.navigate("signupInterestsPage/$token")
                }
            })
    }
}

fun getFile(imageUri: Uri, context: Context): File? {
    var file: File? = null
    val cursor = context.contentResolver?.query(imageUri, null, null, null, null)
    val column = "_data"
    if (cursor != null) {
        while (cursor.moveToNext()) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
            file = File(cursor.getString(columnIndex))
        }
    }

    cursor?.close()
    return file
}