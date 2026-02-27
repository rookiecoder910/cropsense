package com.example.cropsense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CropSenseDarkScheme = darkColorScheme(
    primary = Green80,
    onPrimary = GreenDark,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenBright,

    secondary = Green60,
    onSecondary = GreenSecondaryDark,

    background = DarkBackground,
    onBackground = LightOnBackground,

    surface = DarkSurface,
    onSurface = LightOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    error = ErrorLight,
    onError = ErrorDark,
    errorContainer = Color(0xFF3B0A0A),
    onErrorContainer = ErrorLight,

    outline = OutlineDark,
)

@Composable
fun CropSenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CropSenseDarkScheme,
        typography = CropSenseTypography,
        content = content
    )
}