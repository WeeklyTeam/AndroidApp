package com.ottogo.weekly.ui.account

import android.Manifest
import android.content.ContentValues
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.components.*
import com.ottogo.weekly.ui.login.getFile
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.OutputStream
import kotlin.math.min


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditProfilePage(navController: NavController, userViewModel: UserViewModel) {

    val context = LocalContext.current
    var error: String? by remember { mutableStateOf(null) }
    var name: String by rememberSaveable { mutableStateOf(userViewModel.profile?.name ?: "") }
    var username: String by rememberSaveable { mutableStateOf(userViewModel.profile?.username ?: "") }
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
                val tempFile = getFile(imageUri = imageUri!!, context)
                bitmap = BitmapFactory.decodeFile(tempFile?.path).toSquare()
                    ?.let { Bitmap.createScaledBitmap(it, 400, 400, false) }
                var fileName = tempFile?.nameWithoutExtension
                var compressFormat = Bitmap.CompressFormat.JPEG
                if (tempFile?.extension == "jpg"){
                    fileName += ".jpeg"
                } else if (tempFile?.extension == "png"){
                    compressFormat = Bitmap.CompressFormat.PNG
                }

                //file = bitmap?.let { bitmapToFile(bitmap = it, fileNameToSave = tempFile?.name ?: "weekly/androidProfilePicture.jpeg", compressFormat = compressFormat) }

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, tempFile?.nameWithoutExtension)
                    put(MediaStore.MediaColumns.MIME_TYPE, MimeTypeMap.getSingleton().getMimeTypeFromExtension(tempFile?.extension))
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }
                //use application context to get contentResolver
                val contentResolver = context.contentResolver

                var fos: OutputStream? = null

                contentResolver.also { resolver ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        imageUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
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
                ProfilePicture(url = userViewModel.profile?.profile_picture,
                    size = 96,
                    modifier = Modifier
                        .clip(
                            CircleShape
                        )
                        .clickable {
                            when (storagePermissionStatus.status) {
                                // If the camera permission is granted, then show screen with the feature enabled
                                PermissionStatus.Granted -> {
                                    galleryLauncher.launch("image/")
                                }
                                is PermissionStatus.Denied -> {
                                    storagePermissionStatus.launchPermissionRequest()

                                }
                            }
                        })

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
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
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
            }

            Spacer(modifier = Modifier.height(32.dp))

        }
    }

}

fun Bitmap.toSquare(): Bitmap?{
    val side = min(width, height)

    val xOffset = (width - side)/2
    val yOffset = (height - side)/2

    return Bitmap.createBitmap(
    this,
        xOffset,
        yOffset,
        side,
        side
    )
}


//fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String, compressFormat: Bitmap.CompressFormat): File? { // File name like "image.png"
//    //create a file to write bitmap data
//    var file: File? = null
//    return try {
//        file = File(Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave)
//        file.createNewFile()
//
//        //Convert bitmap to byte array
//        val bos = ByteArrayOutputStream()
//        bitmap.compress(compressFormat, 100, bos) // YOU can also save it in JPEG
//        val bitmapdata = bos.toByteArray()
//
//        //write the bytes in file
//        val fos = FileOutputStream(file)
//        fos.write(bitmapdata)
//        fos.flush()
//        fos.close()
//        Log.d("FILE", file.toString())
//        file
//    } catch (e: Exception) {
//        e.printStackTrace()
//        file // it will return null
//    }
//}
