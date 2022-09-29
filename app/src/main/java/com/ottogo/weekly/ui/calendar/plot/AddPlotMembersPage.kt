package com.ottogo.weekly.ui.calendar.plot

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
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
import java.io.File

@Composable
fun AddPlotMembersPage(navController: NavController, plotId: Int, userViewModel: UserViewModel) {
    val plot = userViewModel.plots.firstOrNull{ it.id == plotId }

    val selectedIds = remember {
        mutableStateListOf<Int>()
    }

    var message: String? by remember{ mutableStateOf(null)}

    Column {
        TitleBar(navController = navController, title = "Add")
        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        Column(modifier = Modifier.weight(1F).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(8.dp))
            userViewModel.friends?.filter {
                plot?.going?.find { goingUser ->
                    goingUser.user_id == it.key } == null
            }.forEach { (userId, profile) ->
                val selected = selectedIds.contains(userId)
                SelectProfileItem(
                    profile = profile, selected = selected, modifier = Modifier.clickable {
                        if (selected){
                            selectedIds.remove(userId)
                        } else {
                            selectedIds.add(userId)
                        }
                    })

            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Divider(thickness = 1.dp, color = ExtendedTheme.colors.LightGray)

        if (message != null){
            Text(message!!, style = MaterialTheme.typography.h4, color = ExtendedTheme.colors.Green, modifier = Modifier.padding(top = 12.dp, bottom = 6.dp))
        }

        CustomButton(buttonText = "Done", onClick = {

            WeeklyApi.retrofitService.patchPlot(
                mapOf("Authorization" to "token ${userViewModel.token}"),
                plotId,
                mapOf("invite" to selectedIds.toList())
            )

            userViewModel.inviteToPlot(plotId, selectedIds)
            message = "Invites sent"

        }, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16  .dp, top = 12.dp))
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