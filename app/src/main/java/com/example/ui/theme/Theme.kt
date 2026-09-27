package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AutomotiveDarkColorScheme =
  darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color.Black,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    tertiary = NeonGreen,
    onTertiary = Color.Black,
    background = DarkSurface,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = EmergencyRed,
    onError = Color.White,
    outline = BorderColor
  )

@Composable
fun MadanRcTheme(
  darkTheme: Boolean = true, // Automotive app optimized for dark mode cockpit
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = AutomotiveDarkColorScheme,
    typography = Typography,
    content = content
  )
}
