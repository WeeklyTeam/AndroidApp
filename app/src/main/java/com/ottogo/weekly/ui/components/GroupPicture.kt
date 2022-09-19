package com.ottogo.weekly.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun GroupPicture(group: Group, size: Int = 48, onImageClick: () -> Unit = {}, modifier: Modifier = Modifier) {

    if (group.image != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(group.image)
                .crossfade(true)
                .build(),
            contentDescription = group.name,
            placeholder = painterResource(R.drawable.default_group_picture),
            fallback = painterResource(R.drawable.default_group_picture),
            contentScale = ContentScale.Crop,
            modifier = modifier

                .clip(CircleShape)
                .clickable{
                    onImageClick()
                }
                .size(size.dp)
                )
    } else {
        if (group.members.count() <= 2){
            Image(painter = painterResource(id = R.drawable.default_group_picture), contentDescription = group.name, modifier
                .clip(CircleShape)
                .clickable{
                    onImageClick()
                }.size(size.dp)

                )
        } else {
            Column(modifier = modifier.clip(RoundedCornerShape(size/4)).clickable{
                onImageClick()
            }.size(size.dp)
            ){
                Row() {
                    ProfilePicture(url = group.members[0].profile_picture, size = size/2, Modifier.border(1.dp, color = MaterialTheme.colors.background, shape = CircleShape) )
                            
                    ProfilePicture(url = group.members[1].profile_picture, size = size/2, Modifier.border(1.dp, color = MaterialTheme.colors.background, shape = CircleShape))
                     }

                Row() {
                    ProfilePicture(url = group.members[2].profile_picture, size = size/2, Modifier.border(1.dp, color = MaterialTheme.colors.background, shape = CircleShape) )

                    if (group.members.count()>3) {
                        ProfilePicture(
                            url = group.members[3].profile_picture,
                            size = size/2,
                            Modifier.border(
                                1.dp,
                                color = MaterialTheme.colors.background,
                                shape = CircleShape
                            )
                        )
                    }
                    else {
                        Spacer(modifier = Modifier.size((size/2).dp))
                    }

                }
            }
        }
    }
}