package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val iOSDarkColorScheme = darkColorScheme(
    primary = HeartsPink,
    secondary = PremiumLightGray,
    tertiary = PremiumWhite,
    background = PremiumBlack,
    surface = PremiumDarkGray,
    surfaceVariant = PremiumMediumGray,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = PremiumWhite,
    onBackground = PremiumWhite,
    onSurface = PremiumWhite,
    onSurfaceVariant = PremiumLightGray
)

private val iOSLightColorScheme = lightColorScheme(
    primary = HeartsPink,
    secondary = Color(0xFF8E8E93),
    tertiary = Color(0xFF1C1C1E),
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE5E5EA),
    onPrimary = Color.White,
    onSecondary = Color(0xFF1C1C1E),
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1C1E),
    onSurface = Color(0xFF1C1C1E),
    onSurfaceVariant = Color(0xFF8E8E93)
)

private val iOSIndigoColorScheme = darkColorScheme(
    primary = Color(0xFF5856D6), // iOS Indigo
    secondary = PremiumLightGray,
    tertiary = PremiumWhite,
    background = Color(0xFF0A0A16),
    surface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFF16213E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = PremiumWhite,
    onBackground = PremiumWhite,
    onSurface = PremiumWhite,
    onSurfaceVariant = PremiumLightGray
)

private val iOSForestColorScheme = darkColorScheme(
    primary = Color(0xFF30D158), // iOS Mint/Green
    secondary = PremiumLightGray,
    tertiary = PremiumWhite,
    background = Color(0xFF041410),
    surface = Color(0xFF0F2C24),
    surfaceVariant = Color(0xFF1C3D32),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = PremiumWhite,
    onBackground = PremiumWhite,
    onSurface = PremiumWhite,
    onSurfaceVariant = PremiumLightGray
)

val iOSShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun Hearts2HeartsTheme(
    themeName: String = "ios_dark",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeName) {
        "ios_light" -> iOSLightColorScheme
        "ios_indigo" -> iOSIndigoColorScheme
        "ios_forest" -> iOSForestColorScheme
        else -> iOSDarkColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = iOSShapes,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    themeName: String = "ios_dark",
    content: @Composable () -> Unit
) {
    Hearts2HeartsTheme(themeName = themeName, content = content)
}
