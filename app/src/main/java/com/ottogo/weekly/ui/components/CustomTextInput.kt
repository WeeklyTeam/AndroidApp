package com.ottogo.weekly.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
    ) {

    var keyboardType: KeyboardOptions = KeyboardOptions.Default

    if(isNumberInput){
        keyboardType = KeyboardOptions(keyboardType = KeyboardType.Number)
    }
    else keyboardType = KeyboardOptions(keyboardType = KeyboardType.Password)
    TextField(value = input, onValueChange = onChange, keyboardOptions = keyboardType)

}