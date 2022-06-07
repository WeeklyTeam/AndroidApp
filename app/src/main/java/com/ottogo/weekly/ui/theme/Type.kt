package com.ottogo.weekly.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ottogo.weekly.R

val nunitoFamily = FontFamily(
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_bold, FontWeight.Bold)
)

val Typography = Typography(
        body1 = TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
        ),
        body2 = TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
        ),
        h5 = TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
        ),
        h4 = TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
        ),
        h3 = TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
        ),
        h2 = TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
        ),
        h1 = TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
        )

)