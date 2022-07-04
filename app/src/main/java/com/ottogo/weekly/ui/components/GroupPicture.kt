package com.ottogo.weekly.ui.components


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ottogo.weekly.R
import com.ottogo.weekly.api.models.Group

@Composable
fun GroupPicture(group: Group, size: Int = 48, modifier: Modifier = Modifier) {

    if (group.image != null) {
        ProfilePicture(url = group.image, size = size, modifier = modifier)
    } else {
        if (group.members.count() <= 1){
            ProfilePicture(url = group.image, size = size, modifier = modifier)
        } else if (group.members.count() == 2){
            Spacer(modifier = modifier.size(48.dp))

        } else {
            Column(modifier = modifier){
                Row() {
                    ProfilePicture(url = group.members[0].profile_picture, size = 24, Modifier.border(1.dp, color = MaterialTheme.colors.background, shape = CircleShape) )
                            
                    ProfilePicture(url = group.members[1].profile_picture, size = 24, Modifier.border(1.dp, color = MaterialTheme.colors.background, shape = CircleShape))
                     }

                Row() {
                    ProfilePicture(url = group.members[2].profile_picture, size = 24, Modifier.border(1.dp, color = MaterialTheme.colors.background, shape = CircleShape) )

                    if (group.members.count()>3) {
                        ProfilePicture(
                            url = group.members[3].profile_picture,
                            size = 24,
                            Modifier.border(
                                1.dp,
                                color = MaterialTheme.colors.background,
                                shape = CircleShape
                            )
                        )
                    }
                    else {
                        Spacer(modifier = Modifier.size(24.dp))
                    }

                }
            }
        }
    }
}