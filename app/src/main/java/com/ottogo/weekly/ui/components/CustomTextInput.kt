package com.ottogo.weekly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ottogo.weekly.ui.theme.ExtendedTheme
import com.ottogo.weekly.ui.theme.Typography

/*
*
* Built by: Sriya
*
* Copy the layout of the TextField component in the figma file not the font sizes and styles of the text
* as well as the spacing and layout of the elements
* make sure the Textfield is able to adapt to different types of inputs by changing the text fields input type
* the types you will need to be able to support (excluding the default) are number fields and password inputs
* Do your best! Please don't hesitate to ask any questions
*
* */
@Composable
fun CustomTextField(
    helper: String,
    hint: String,
    input: String,
    isNumberInput: Boolean = false,
    isPasswordInput: Boolean = false,
    onChange: (String) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions(),
    done: Boolean = false,
    modifier: Modifier = Modifier
) {

    var keyboardType: KeyboardOptions = KeyboardOptions.Default

    keyboardType = if(isNumberInput){
        KeyboardOptions(keyboardType = KeyboardType.Number,
            imeAction = if (done) ImeAction.Done else ImeAction.Next
        )
    }
    else if (isPasswordInput) {
        KeyboardOptions(keyboardType = KeyboardType.Password,
            imeAction = if (done) ImeAction.Done else ImeAction.Next
        )
    }else {
        KeyboardOptions(keyboardType = KeyboardType.Text,
            imeAction = if (done) ImeAction.Done else ImeAction.Next
        )
    }
    
    Column(modifier = modifier) {
        Text(helper, style = Typography.h4)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = input,
            onValueChange = onChange, Modifier.fillMaxWidth(),
            keyboardOptions = keyboardType, colors = TextFieldDefaults.textFieldColors(
                backgroundColor = ExtendedTheme.colors.LightGray,
                focusedIndicatorColor =  Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent),
            visualTransformation = if (isPasswordInput) PasswordVisualTransformation() else VisualTransformation.None,
            placeholder = { Text(hint) },
            shape = CircleShape,
            singleLine = true,
            keyboardActions = keyboardActions)

    }
}