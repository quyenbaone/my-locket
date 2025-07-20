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
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
)

private val LightColorScheme = lightColorScheme(
    primary = BlueOcean,           // Màu chính - xanh nước biển
    secondary = BlueLight,         // Màu phụ - xanh nhạt
    tertiary = BlueDark,           // Màu thứ ba - xanh đậm
    background = Background,       // Nền trắng
    surface = SurfaceColor,        // Màu surface
    onPrimary = TextOnPrimary,     // Chữ trắng trên nền xanh
    onSecondary = TextPrimary,     // Chữ đen trên nền xanh nhạt
    onTertiary = TextOnPrimary,    // Chữ trắng trên nền xanh đậm
    onBackground = TextPrimary,    // Chữ đen trên nền trắng
    onSurface = TextPrimary,       // Chữ đen trên surface
    outline = TextSecondary,       // Đường viền xám
)

@Composable
fun MyLocketTheme(
    darkTheme: Boolean = false,  // Mặc định sử dụng light theme
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
