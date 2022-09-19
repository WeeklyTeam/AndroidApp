package com.ottogo.weekly.ui.chat

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.components.TitleBar
import com.ottogo.weekly.ui.login.getFile
import com.ottogo.weekly.ui.theme.ExtendedTheme
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreateGroupPage(navController: NavController){

    val context = LocalContext.current
    var groupName: String by rememberSaveable { mutableStateOf("") }
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
                file = getFile(imageUri = imageUri!!, context)
                bitmap = BitmapFactory.decodeFile(file?.path)
            }
        }
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TitleBar(navController = navController, title = "Group")
        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)


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

        CustomTextField(helper = "Name", hint = "Name", input = groupName, onChange = { groupName = it }, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(24.dp))
        CustomButton(
            buttonText = "Next",
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            navController.currentBackStackEntry?.arguments?.putParcelable("imageUri", imageUri)
            navController.navigate("inviteGroupPage/$groupName")
        }
    }
}
