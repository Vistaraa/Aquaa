package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = AquaDarkOchre,
    onPrimary = AquaDarkCanvasBg,
    primaryContainer = AquaDarkOchreContainer,
    onPrimaryContainer = AquaDarkCharcoal,
    secondary = AquaDarkTeal,
    onSecondary = AquaDarkCanvasBg,
    secondaryContainer = AquaDarkTealContainer,
    onSecondaryContainer = AquaDarkCharcoal,
    background = AquaDarkCanvasBg,
    onBackground = AquaDarkCharcoal,
    surface = AquaDarkSurface,
    onSurface = AquaDarkCharcoal,
    surfaceVariant = AquaDarkHoverBg,
    onSurfaceVariant = AquaDarkMutedCharcoal,
    outline = AquaDarkBorderMuted
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AquaOchre,
    onPrimary = AquaSurfaceWhite,
    primaryContainer = AquaOchreContainer,
    onPrimaryContainer = AquaCharcoal,
    secondary = AquaTeal,
    onSecondary = AquaSurfaceWhite,
    secondaryContainer = AquaTealContainer,
    onSecondaryContainer = AquaCharcoal,
    background = AquaCanvasBg,
    onBackground = AquaCharcoal,
    surface = AquaSurfaceWhite,
    onSurface = AquaCharcoal,
    surfaceVariant = AquaHoverBg,
    onSurfaceVariant = AquaMutedCharcoal,
    outline = AquaBorderMuted
  )

@Composable
fun AquaConnectTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set to false to preserve water brand identity
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

