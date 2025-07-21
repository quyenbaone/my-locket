package com.mylocket.ui.theme

import androidx.compose.ui.graphics.Color

// Primary colors - Dễ nhìn hơn
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val BlueOcean = Color(0xFF2196F3)  // Xanh nước biển dễ nhìn hơn
val BlueLight = Color(0xFF64B5F6)  // Xanh nhạt
val BlueDark = Color(0xFF1976D2)   // Xanh đậm

// Dark theme colors - Thoải mái cho mắt
val DarkBackground = Color(0xFF0D1117)      // Nền tối GitHub-style
val DarkSurface = Color(0xFF161B22)         // Surface tối
val DarkSurfaceVariant = Color(0xFF21262D)  // Surface variant
val CardBackground = Color(0xFF1C2128)      // Background cho card

// Light theme colors - Ít chói hơn
val LightBackground = Color(0xFFF6F8FA)     // Nền sáng nhẹ nhàng
val LightSurface = Color(0xFFFFFFFF)        // Surface trắng
val LightSurfaceVariant = Color(0xFFF1F3F4) // Surface variant

// Text colors - Dễ đọc hơn
val TextPrimaryDark = Color(0xFFE6EDF3)     // Chữ sáng trên nền tối
val TextSecondaryDark = Color(0xFF8B949E)   // Chữ phụ trên nền tối
val TextPrimaryLight = Color(0xFF24292F)    // Chữ tối trên nền sáng
val TextSecondaryLight = Color(0xFF656D76)  // Chữ phụ trên nền sáng

// Accent colors - Dễ nhìn
val AccentGreen = Color(0xFF238636)         // Xanh lá accent
val AccentRed = Color(0xFFDA3633)           // Đỏ accent
val AccentOrange = Color(0xFFD1242F)        // Cam accent
val AccentPurple = Color(0xFF8957E5)        // Tím accent

// Border colors
val BorderDark = Color(0xFF30363D)          // Border tối
val BorderLight = Color(0xFFD0D7DE)         // Border sáng

// Legacy colors (giữ lại để tương thích)
val Background = LightBackground  // Alias
val SecondBackground = LightSurfaceVariant
val SurfaceColor = LightSurface
val TextPrimary = TextPrimaryLight
val TextSecondary = TextSecondaryLight
val TextOnPrimary = Color(0xFFFFFFFF)
val Amber = Color(0xFFFFB901)
val Charcoal = Color(0xFF222222)
val Grey = Color(0xFF3A3A3A)
