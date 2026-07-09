package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Raw static colors for Theme configurations
val BaseBlack = Color(0xFF000000)
val BaseDarkGray = Color(0xFF1C1C1E)
val BaseMediumGray = Color(0xFF2C2C2E)
val BaseLightGray = Color(0xFF8E8E93)
val BaseWhite = Color(0xFFFFFFFF)

// Dynamic, theme-aware colors mapping to active MaterialTheme ColorScheme
val PremiumBlack: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val PremiumDarkGray: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

val PremiumMediumGray: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val PremiumLightGray: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val PremiumWhite: Color
    @Composable
    get() = MaterialTheme.colorScheme.onBackground

val HeartsPink = Color(0xFFFF4D6D)
val HeartsGold = Color(0xFFFFD166)

val StatusGreen = Color(0xFF34C759)
val DestructiveRed = Color(0xFFFF3B30)

// Legacy compatibility
val GlassBgStart: Color
    @Composable
    get() = PremiumBlack

val GlassBgEnd: Color
    @Composable
    get() = PremiumDarkGray

val GlassCardBg: Color
    @Composable
    get() = PremiumMediumGray

val GlassCardBorder: Color
    @Composable
    get() = PremiumDarkGray

val GlassPrimary: Color
    @Composable
    get() = PremiumWhite

val GlassSecondary: Color
    @Composable
    get() = PremiumLightGray

val GlassTertiary: Color
    @Composable
    get() = PremiumWhite

val GlassTextPrimary: Color
    @Composable
    get() = PremiumWhite

val GlassTextSecondary: Color
    @Composable
    get() = PremiumLightGray

val DeepViolet: Color
    @Composable
    get() = PremiumBlack

val SoftViolet: Color
    @Composable
    get() = PremiumDarkGray

val HeartsAccent: Color
    @Composable
    get() = PremiumWhite

val HeartsLavender: Color
    @Composable
    get() = PremiumLightGray

val HeartsTextPrimary: Color
    @Composable
    get() = PremiumWhite

val HeartsTextSecondary: Color
    @Composable
    get() = PremiumLightGray

val Purple80: Color
    @Composable
    get() = PremiumWhite

val PurpleGrey80: Color
    @Composable
    get() = PremiumLightGray

val Pink80: Color
    @Composable
    get() = PremiumWhite

val Purple40: Color
    @Composable
    get() = PremiumWhite

val PurpleGrey40: Color
    @Composable
    get() = PremiumDarkGray

val Pink40: Color
    @Composable
    get() = PremiumWhite

