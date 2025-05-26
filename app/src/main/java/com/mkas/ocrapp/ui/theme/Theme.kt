package com.mkas.ocrapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color // Ensure Color is imported if used directly for onPrimary etc.

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimaryBlue, // Updated
    onPrimary = Color.White, // Added
    secondary = PurpleGrey80, // Kept for now
    tertiary = Pink80, // Kept for now
    surface = DarkSurface, // Updated
    onSurface = DarkOnSurface, // Updated
    onSurfaceVariant = DarkOnSurfaceVariant, // Updated
    outline = DarkOutline // Updated
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimaryBlue, // Updated
    onPrimary = Color.White, // Added
    secondary = PurpleGrey40, // Kept for now
    tertiary = Pink40, // Kept for now
    surface = LightSurface, // Updated
    onSurface = LightOnSurface, // Updated
    onSurfaceVariant = LightOnSurfaceVariant, // Updated
    outline = LightOutline // Updated

    /* Other default colors to override
    background = LightSurface, // Or Color(0xFFFFFBFE)
    surface = LightSurface, // Or Color(0xFFFFFBFE)
    // onPrimary = Color.White, // Already defined above
    onSecondary = Color.Black, // Example, review if PurpleGrey40 needs specific onSecondary
    onTertiary = Color.Black, // Example, review if Pink40 needs specific onTertiary
    // onBackground = LightOnSurface, // Or Color(0xFF1C1B1F)
    // onSurface = LightOnSurface, // Already defined above
    */
)

@Composable
fun OCRAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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