package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = JadePrimary,
    onPrimary = Color.Black,
    primaryContainer = JadeContainer,
    onPrimaryContainer = OnJadeContainer,
    secondary = GoldPrimary,
    onSecondary = Color.Black,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = CrimsonPrimary,
    onTertiary = Color.White,
    tertiaryContainer = CrimsonContainer,
    onTertiaryContainer = OnCrimsonContainer,
    background = InkBlack,
    onBackground = TextPrimary,
    surface = InkSurface,
    onSurface = TextPrimary,
    surfaceVariant = InkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = InkCardBorder,
    outlineVariant = Color(0xFF1E352B)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our martial arts custom palette
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

