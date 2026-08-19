package com.example.ui.theme

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
    primary = TealLight,
    onPrimary = Slate900,
    primaryContainer = TealDark,
    onPrimaryContainer = TealContainer,
    secondary = CoralLight,
    onSecondary = Slate900,
    secondaryContainer = CoralSecondary,
    onSecondaryContainer = CoralContainer,
    tertiary = SkyLight,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Slate200,
    outline = Slate700
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,
    secondary = CoralSecondary,
    onSecondary = Color.White,
    secondaryContainer = CoralContainer,
    onSecondaryContainer = OnCoralContainer,
    tertiary = SkyAccent,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = Slate900,
    onSurface = Slate900,
    onSurfaceVariant = Slate700,
    outline = Slate200
)

@Composable
fun PeuinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use Peuin's custom cohesive brand palette
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
