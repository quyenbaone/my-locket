package com.mylocket.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BlueOcean,
    secondary = BlueLight,
    tertiary = BlueDark,
    background = Black,                 // Full black background
    surface = Black,                   // Full black surface
    surfaceVariant = Color(0xFF1A1A1A), // Very dark gray for variants
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,              // White text on black background
    onSurface = White,                 // White text on black surface
    onSurfaceVariant = Color(0xFFB0B0B0), // Light gray for secondary text
    outline = Color(0xFF404040),       // Dark gray borders
    outlineVariant = Color(0xFF303030),
    error = AccentRed,
    onError = White,
)



@Composable
fun MyLocketTheme(
    darkTheme: Boolean = true,  // Always use dark theme
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Disable dynamic color to use custom colors
    content: @Composable () -> Unit
) {
    // Always use dark theme - ignore system setting and dynamic colors
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
