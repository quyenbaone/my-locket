package com.mylocket.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BlueOcean,
    secondary = BlueLight,
    tertiary = BlueDark,
    background = DarkBackground,        // Nền tối dễ nhìn
    surface = DarkSurface,             // Surface tối
    surfaceVariant = DarkSurfaceVariant, // Surface variant
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = TextPrimaryDark,     // Chữ sáng trên nền tối
    onSurface = TextPrimaryDark,        // Chữ sáng trên surface
    onSurfaceVariant = TextSecondaryDark, // Chữ phụ
    outline = BorderDark,               // Border tối
    outlineVariant = BorderDark,
)

private val LightColorScheme = lightColorScheme(
    primary = BlueOcean,
    secondary = BlueLight,
    tertiary = BlueDark,
    background = LightBackground,       // Nền sáng dễ nhìn
    surface = LightSurface,            // Surface sáng
    surfaceVariant = LightSurfaceVariant, // Surface variant
    onPrimary = White,
    onSecondary = TextPrimaryLight,
    onTertiary = White,
    onBackground = TextPrimaryLight,    // Chữ tối trên nền sáng
    onSurface = TextPrimaryLight,       // Chữ tối trên surface
    onSurfaceVariant = TextSecondaryLight, // Chữ phụ
    outline = BorderLight,              // Border sáng
    outlineVariant = BorderLight,
)

@Composable
fun MyLocketTheme(
    darkTheme: Boolean = true,  // Mặc định sử dụng dark theme dễ nhìn hơn
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Tắt dynamic color để sử dụng màu tùy chỉnh
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
