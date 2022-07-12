package com.ottogo.weekly.ui.chat.group


import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ottogo.weekly.api.models.Group
import com.ottogo.weekly.ui.components.*
import com.ottogo.weekly.ui.login.getFile
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.viewmodels.UserViewModel
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditGroupPage(navController: NavController, groupId: Int, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val error: String? by remember { mutableStateOf(null) }
    var imageUri: Uri? by remember { mutableStateOf(null) }
    var file: File? by remember { mutableStateOf(null) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val storagePermissionStatus = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val group = userViewModel.groups?.get(groupId)
    var groupName by remember { mutableStateOf(group?.name) }
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

    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        TitleBar(navController = navController, title = "Edit")
        Divider(thickness = 2.dp, color = ExtendedTheme.colors.LightGray)
        Spacer(modifier = Modifier.padding(bottom = 32.dp))

        if (error != null) {
            Spacer(modifier = Modifier.padding(32.dp))
            Message(error.toString())
        }

        if (bitmap == null && group != null) {
            ProfilePicture(url = group.image
                , modifier = Modifier
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
        } else if (bitmap != null) {
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
        }
        else {
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
        // TODO: Implement button functionality
        if (group != null) {
            Column() {
                EditGroupName(text = groupName, onTextChange = { groupName = it })
                MembersAddSideScroll(group = group)
                CustomButton(
                    buttonText = "Save",
                    onClick = {},
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 120.dp))
            }
        }
    }
}

@Composable
fun EditGroupName(text: String?, onTextChange: (String) -> Unit) {
    Spacer(modifier = Modifier.padding(bottom = 8.dp))
    CustomTextField(
        helper = "Group",
        hint = "Group",
        input = text!!,
        onChange = onTextChange,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .size(width = 340.dp, height = 120.dp))
}

@Composable
fun MembersAddSideScroll(group: Group?) {
    Row (modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Start)
        {
            group?.members?.forEach { member ->
                ProfilePicture(
                    url = member.profile_picture
                    , size = 32
                    , modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
        Spacer(Modifier.weight(1f))
        CustomButton(
            buttonText = "Add",
            modifier = Modifier
                .size(width = 100.dp, 40.dp)
                .border(
                    width = 3.dp,
                    color = ExtendedTheme.colors.LightGray,
                    shape = RoundedCornerShape(12.dp)
                ),
            backgroundColor = com.ottogo.weekly.ui.theme.White,
            onClick = {} )
    }
}