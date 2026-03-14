package com.example.madproject.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TableGreen,
    onPrimary = TextOnDark,
    secondary = GoldAccent,
    onSecondary = TextPrimary,
    tertiary = SoftGreenSurfaceDark,
    onTertiary = TextOnDark,
    background = TableGreenDark,
    onBackground = TextOnDark,
    surface = SoftGreenSurfaceDark,
    onSurface = TextOnDark,
    surfaceVariant = ColorSchemeTokensDark.surfaceVariant,
    onSurfaceVariant = ColorSchemeTokensDark.onSurfaceVariant,
    error = ErrorRed,
    onError = TextOnDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = TableGreen,
    onPrimary = TextOnDark,
    secondary = GoldAccent,
    onSecondary = TextPrimary,
    tertiary = SoftGreenSurface,
    onTertiary = TextPrimary,
    background = CreamBackground,
    onBackground = TextPrimary,
    surface = CreamSurface,
    onSurface = TextPrimary,
    surfaceVariant = SoftGreenSurface,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextOnDark,
    outline = OutlineLight
)

private object ColorSchemeTokensDark {
    val surfaceVariant = androidx.compose.ui.graphics.Color(0xFF244635)
    val onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFD6E6DC)
}

@Composable
fun MADProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}