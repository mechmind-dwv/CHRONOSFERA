package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CosmicDark = Color(0xFF0F172A)
val CosmicSurface = Color(0xFF1E293B)
val CosmicPrimary = Color(0xFF38BDF8)
val CosmicSecondary = Color(0xFF22C55E)
val CosmicTertiary = Color(0xFFF59E0B)
val CosmicText = Color(0xFFF8FAFC)

private val CosmicColorScheme = darkColorScheme(
    primary = CosmicPrimary,
    secondary = CosmicSecondary,
    tertiary = CosmicTertiary,
    background = CosmicDark,
    surface = CosmicSurface,
    onPrimary = CosmicDark,
    onSecondary = CosmicDark,
    onBackground = CosmicText,
    onSurface = CosmicText
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CosmicColorScheme,
        typography = Typography,
        content = content
    )
}

