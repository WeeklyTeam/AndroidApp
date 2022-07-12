package com.ottogo.weekly.ui.bottomModals

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.BottomSheetViewModel
import com.ottogo.weekly.R
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.ui.chat.PopUpBlockSheetContent
import com.ottogo.weekly.ui.chat.PopUpConfirmationSheetContent
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.ProfilePicture
import com.ottogo.weekly.ui.theme.Black
import com.ottogo.weekly.ui.theme.Black60
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun ProfileBottomModalSheet(userViewModel: UserViewModel, bottomSheetViewModel: BottomSheetViewModel) {

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var blockDisplay by remember { mutableStateOf(false) }

    var onConfirm by remember { mutableStateOf({ }) }

    LaunchedEffect(key1 = bottomSheetViewModel.profile?.user_id, block = {
        Log.d("LE", "hi")
    })


    if (showDialog && !blockDisplay) {
        PopUpConfirmationSheetContent(title = title,
            onDismiss = { showDialog = false },
            onConfirm = { onConfirm.invoke() })
    } else if (showDialog && blockDisplay) {
        PopUpBlockSheetContent(title = title, userId = bottomSheetViewModel.profile?.user_id ?: -1,
            onDismiss = { showDialog = false },
            onBlock = {
                // To recompose bottom sheet button

                bottomSheetViewModel.profile = bottomSheetViewModel.profile?.copy(blocked = true, urequested = false, friend = false, requesting = false)

                // To recompose search results with updated profile
                //TODO: ADD NEW ACtion

            })
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        ProfilePicture(bottomSheetViewModel.profile?.profile_picture)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = bottomSheetViewModel.profile?.name ?: "", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = bottomSheetViewModel.profile?.username ?: "", style = MaterialTheme.typography.body2, color = Black60)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 60.dp, end = 60.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {

                if (bottomSheetViewModel.profile?.blocked == true) {
                    CustomButton(
                        buttonText = "This user is blocked",
                        backgroundColor = Color.White,
                        textColor = Black
                    ) {
                        title = "Are you sure you want to unblock ${bottomSheetViewModel.profile?.name}?"
                        blockDisplay = false
                        onConfirm = {
                            runBlocking {
                                WeeklyApi.retrofitService.unblock(
                                    mapOf("Authorization" to "token ${userViewModel.token}"),
                                    bottomSheetViewModel.profile?.user_id ?: -1
                                )
                            }

                            // To recompose bottom sheet button
                            bottomSheetViewModel.profile = bottomSheetViewModel.profile?.copy(blocked = false)

                            // To recompose search results with updated profile
                            //TODO: Add extra actions
//                            var newProfile = searchResults[profileIndex].copy()
//                            newProfile.blocked = false
//                            model.updateSearchItem(profileIndex, newProfile)
                        }
                        showDialog = true
                    }
                } else if (bottomSheetViewModel.profile?.friend == true) {
                    CustomButton(
                        buttonText = "Added",
                        backgroundColor = Color.White,
                        outlineColor = LightGray,
                        textColor = Black60
                    ) {
                        title = "Are you sure you want to remove ${bottomSheetViewModel.profile?.name} as a friend?"
                        blockDisplay = false
                        onConfirm = {
                            runBlocking {
                                WeeklyApi.retrofitService.reject(
                                    mapOf("Authorization" to "token ${userViewModel.token}"),
                                    bottomSheetViewModel.profile?.user_id ?:-1
                                )
                            }

                            // To recompose bottom sheet button
                            bottomSheetViewModel.profile = bottomSheetViewModel.profile?.copy(friend = false)

                            // To recompose search results with updated profile
                            bottomSheetViewModel.profile?.let { userViewModel.removeFriend(it.user_id) }
                            //TODO: ADD NEW ACtion
                        }
                        showDialog = true
                    }
                } else if (bottomSheetViewModel.profile?.urequested == true) {
                    CustomButton(
                        buttonText = "Requested",
                        backgroundColor = LightGray,
                        textColor = Black60,
                        onClick = {
                            title = "Are you sure you want to cancel this request?"
                            blockDisplay = false
                            onConfirm = {
                                runBlocking {
                                    WeeklyApi.retrofitService.reject(
                                        mapOf("Authorization" to "token ${userViewModel.token}"),
                                        bottomSheetViewModel.profile?.user_id ?: -1
                                    )
                                }

                                // To recompose bottom sheet button
                                bottomSheetViewModel.profile = bottomSheetViewModel.profile?.copy(urequested = false)

                                // To recompose search results with updated profile
                                //TODO: ADD NEW ACtion

                            }
                            showDialog = true
                        })
                } else if (bottomSheetViewModel.profile?.requesting == true) {
                    Row() {
                        CustomButton(
                            buttonText = "Reject", backgroundColor = LightGray,
                            textColor = Black, modifier = Modifier.weight(1f)
                        ) {
                            runBlocking {
                                WeeklyApi.retrofitService.reject(
                                    mapOf("Authorization" to "token ${userViewModel.token}"),
                                    bottomSheetViewModel.profile?.user_id ?:-1
                                )
                            }

                            // To recompose bottom sheet button
                            bottomSheetViewModel.profile = bottomSheetViewModel.profile?.copy(requesting = false)

                            // To recompose search results with updated profile
                            //TODO: ADD NEW ACtion

                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        CustomButton(buttonText = "Accept", modifier = Modifier.weight(1f)) {
                            runBlocking {
                                WeeklyApi.retrofitService.accept(
                                    mapOf("Authorization" to "token ${userViewModel.token}"),
                                    bottomSheetViewModel.profile?.user_id ?: -1
                                )
                            }

                            // To recompose bottom sheet button
                            bottomSheetViewModel.profile = bottomSheetViewModel.profile?.copy(requesting = false, friend = true)

                            // To recompose search results with updated profile
                            bottomSheetViewModel.profile?.let { userViewModel.addFriend(it) }

                            //TODO: ADD NEW ACtion

                        }
                    }
                } else {
                    CustomButton(
                        buttonText = "Add",
                        onClick = {
                            runBlocking {
                                WeeklyApi.retrofitService.add(
                                    mapOf("Authorization" to "token ${userViewModel.token}"),
                                    bottomSheetViewModel.profile?.user_id ?: -1
                                )
                            }

                            // To recompose bottom sheet button
                            bottomSheetViewModel.profile = bottomSheetViewModel.profile?.copy(urequested = true)

                            // To recompose search results with updated profile
                            //TODO: ADD NEW ACtion

                        })
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            IconButton(
                onClick = {
                    title = "Are you sure you want to block ${bottomSheetViewModel.profile?.name}?"
                    blockDisplay = true
                    showDialog = true
                },
                content = {
                    Image(
                        painterResource(id = R.drawable.ic_spam_line),
                        contentDescription = "report icon"
                    )
                })
        }

        Spacer(modifier = Modifier.height(24.dp))

    }

}

