package com.example.playlistmaker.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//dark
//<color name="dark">#1A1B22</color>
//<color name="grey">#E6E8EB</color> = darker_grey light

//light
//<color name="grey">#AEAFB4</color>


val black = Color(0xFF000000)
val white = Color(0xFFFFFFFF)
val dark = Color(0xFF1A1B22)
val red = Color(0xFFF56B6C)
val yellow = Color(0xFFFCD452)
val blue = Color(0xFF3772E7)
val grey_dark = Color(0xFFAEAFB4)
val grey_medium = Color(0xFFE6E8EB)

    val LightColors = lightColorScheme(
        background = white,
        onBackground = dark,
        onSecondary = grey_dark,
        onTertiary = grey_dark

    )
    val DarkColors = darkColorScheme(
        background = dark,
        onBackground = white,
        onSecondary = white,
        onTertiary = grey_medium
    )

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}