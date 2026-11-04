package com.sideline.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palette — true black, brand orange, muted grays.
val Bg = Color(0xFF000000)
val Card = Color(0xFF121216)
val Line = Color(0xFF15181D)
val TextPrimary = Color(0xFFF2F3F5)
val TextDim = Color(0xFF6B7078)
val TextDimmer = Color(0xFF4C5059)
val Accent = Color(0xFFF6A01A)      // favorites + active
val AccentBlue = Color(0xFF2B7FFF)  // navigation
val WinWhite = Color(0xFFFFFFFF)
val LoseGray = Color(0xFF565A62)
val LiveRed = Color(0xFFFF5A4D)

private val SidelineColors = darkColorScheme(
    primary = Accent,
    background = Bg,
    surface = Card,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun SidelineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SidelineColors,
        typography = SidelineTypography,
        content = content,
    )
}
