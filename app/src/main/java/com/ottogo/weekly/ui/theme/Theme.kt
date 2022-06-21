package com.ottogo.weekly.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val Black80: Color,
    val Black60: Color,
    val Black40: Color,
    val Black20: Color,
    val LightGray: Color,
    val LoveRed: Color,
    val Green: Color,
    val DarkGreen: Color,
    val LightGreen: Color

)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        Black80 = Color.Unspecified,
        Black60 = Color.Unspecified,
        Black40 = Color.Unspecified,
        Black20 = Color.Unspecified,
        LightGray = Color.Unspecified,
        LoveRed = Color.Unspecified,
        Green = Color.Unspecified,
        DarkGreen = Color.Unspecified,
        LightGreen = Color.Unspecified

    )
}


private val DarkColorPalette = darkColors(
    primary = Purple,
    primaryVariant = Purple,
    secondary = Purple
)

private val LightColorPalette = lightColors(
    primary = Purple,
    primaryVariant = Purple,
    secondary = Purple,

    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black,

)

@Composable
fun WeeklyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val extendedColors = ExtendedColors(
        Black80 = Black80,
        Black60 = Black60,
        Black40 = Black40,
        Black20 = Black20,
        LightGray = LightGray,
        LoveRed = LoveRed,
        Green = Green,
        LightGreen = LightGreen,
        DarkGreen = DarkGreen

    )
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object ExtendedTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}