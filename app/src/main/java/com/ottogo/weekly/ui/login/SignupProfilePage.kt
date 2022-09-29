package com.ottogo.weekly.ui.login

import android.Manifest
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.onesignal.OneSignal
import com.onesignal.OneSignalNotificationManager
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.account.toSquare
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.login.ui.components.LoginTitle
import com.ottogo.weekly.ui.components.Message
import com.ottogo.weekly.viewmodels.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.OutputStream
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
fun SignupProfilePage(navController: NavController, token: String, username: String, notificationManager: NotificationManager, userViewModel: UserViewModel) {
    var error : String? by remember { mutableStateOf(null) }
    var name : String by rememberSaveable { mutableStateOf("") }
    var imageUri: Uri? by remember { mutableStateOf(null) }
    var file: File? by remember { mutableStateOf(null) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val storagePermissionStatus = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
                uri: Uri? -> imageUri = uri
            if (imageUri != null) {
                val tempFile = getFile(imageUri = imageUri!!, context)
                bitmap = BitmapFactory.decodeFile(tempFile?.path).toSquare()
                    ?.let { Bitmap.createScaledBitmap(it, 400, 400, false) }
                var fileName = tempFile?.nameWithoutExtension
                var compressFormat = Bitmap.CompressFormat.JPEG
                if (tempFile?.extension == "jpg") {
                    fileName += ".jpeg"
                } else if (tempFile?.extension == "png") {
                    compressFormat = Bitmap.CompressFormat.PNG
                }

                //file = bitmap?.let { bitmapToFile(bitmap = it, fileNameToSave = tempFile?.name ?: "weekly/androidProfilePicture.jpeg", compressFormat = compressFormat) }

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, tempFile?.nameWithoutExtension)
                    put(
                        MediaStore.MediaColumns.MIME_TYPE,
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(tempFile?.extension)
                    )
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }
                //use application context to get contentResolver
                val contentResolver = context.contentResolver

                var fos: OutputStream? = null

                contentResolver.also { resolver ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        imageUri = resolver.insert(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                    }
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }

                fos?.use { bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it) }

                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                contentResolver.update(imageUri!!, contentValues, null, null)

                file = getFile(imageUri = imageUri!!, context)
            }
        }
    )

    Column(modifier = Modifier.systemBarsPadding().verticalScroll(rememberScrollState())){
        LoginTitle(navController = navController, title = "Profile")

        Column(modifier = Modifier
            .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    painter = painterResource(id = R.drawable.default_profile_picture),
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

            CustomTextField(helper = "Name", hint = "Name", input = name, onChange = { name = it }, keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
                done = true)
            Spacer(modifier = Modifier.padding(bottom = 32.dp))
            CustomButton(
                buttonText = "Next",
                onClick = {
                    try {
                        val pushTokenId = OneSignal.getDeviceState()?.userId ?: ""

                        val profileName = name.toRequestBody("text/plain".toMediaTypeOrNull())
                        val pushToken = pushTokenId.toRequestBody("text/plain".toMediaTypeOrNull())

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
                            mapOf("name" to profileName, "token" to pushToken),
                            picture
                        )
                        if (notificationManager.isNotificationPolicyAccessGranted) {
                            userViewModel.setToken(username, token, context)

                        } else {
                            navController.navigate("signupNotificationPage/$token/$username")
                        }

                    } catch (e: Exception) {
                        if (e is HttpException) {
                            if (e.code() >= 400) {
                                error = "Issue Signing up.\n Please Try again."
                            }
                        }
//                        navController.navigate("signupInterestsPage/$token")
                    }
                })
        }
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