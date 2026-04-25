package com.example.playlistmaker.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


    val LightColors = lightColorScheme(
        primary = Color(0xFF3772E7),
        onPrimary = Color.White,
        background = Color.White,
        onBackground = Color(0xFF1C1B1F),
        // … остальные слоты Material3
    )
    val DarkColors = darkColorScheme(
        primary = Color(0xFF9ECAFF),
        onPrimary = Color(0xFF003258),
        background = Color(0xFF121212),
        onBackground = Color(0xFFE6E1E5),
        // …
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