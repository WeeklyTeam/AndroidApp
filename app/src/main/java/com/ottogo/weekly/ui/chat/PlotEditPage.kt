package com.ottogo.weekly.ui.chat

import android.widget.EditText
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.components.CustomButton
import com.ottogo.weekly.ui.components.CustomTextField
import com.ottogo.weekly.ui.theme.Black60
import com.ottogo.weekly.ui.theme.LightGray
import com.ottogo.weekly.viewmodels.UserViewModel

@Composable
fun PlotEditPage(navController: NavController, userViewModel: UserViewModel) {
    
    PlotEditPageContent(navController)
    
}

@Composable
fun PlotEditPageContent(navController: NavController) {


    var titleInput by remember { mutableStateOf("") }
    var detailsInput by remember { mutableStateOf("") }


    Column(modifier = Modifier
        .fillMaxSize()) {

        Header(navController)

        Divider(color = LightGray, thickness = 1.dp)

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {

            Body(detailsInput = detailsInput, titleInput = titleInput,
                detailsChange = { detailsInput = it }, titleChange = { titleInput = it })

            CustomButton(buttonText = "Save", onClick = { })

        }
    }

}

@Composable
fun Body(detailsInput: String, titleInput: String, detailsChange: (String) -> Unit, titleChange: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()) {

        // TODO: Change "ProfilePicture" to an Emoji TextField
        ProfilePicture(profilePicture = "https://cdn.britannica.com/55/174255-050-526314B6/brown-Guernsey-cow.jpg")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Title", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(helper = "", hint = "What's the plan", input = titleInput, onChange = titleChange)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Date", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Jul. 10, 2001 at 3:00 pm", style = MaterialTheme.typography.body2)
            ChangeDateButton()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Details", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.height(200.dp).fillMaxWidth().border(border = BorderStroke(1.dp, LightGray), shape = RoundedCornerShape(16.dp))) {
            TextField(modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                value = detailsInput, onValueChange = detailsChange)
        }

    }
}

@Composable
fun Header(navController: NavController) {

    Column() {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                ActionIconButton(R.drawable.ic_arrow_left_s_line) { navController.popBackStack() }
                Spacer(modifier = Modifier.width(15.dp))
                Text(text = "Edit", style = MaterialTheme.typography.h2)
            }

            ActionIconButton(R.drawable.ic_delete_bin_line) { /*TODO: Delete plot functionality*/ }

        }
    }

}

@Composable
fun ChangeDateButton() {

    Button(colors = ButtonDefaults.buttonColors(backgroundColor = LightGray), shape = RoundedCornerShape(12.dp),
        elevation = null, contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        onClick = {

        }) {
        Text(text = "Change", style = MaterialTheme.typography.h4, color = Black60)
    }

}

@Composable
fun ActionIconButton(resourceId: Int, onClick: () -> Unit) {

    IconButton(onClick = onClick ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(id = resourceId),
            contentDescription = "back arrow",
        )
    }
    
}