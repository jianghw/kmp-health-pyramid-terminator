package com.terminator.android.ui.theme

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

private val ElderlyFriendlyLightColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E20),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF002204),
    secondary = Color(0xFF37474F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFD8DC),
    onSecondaryContainer = Color(0xFF121C22),
    tertiary = Color(0xFF00695C),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF80CBC4),
    onTertiaryContainer = Color(0xFF00201C),
    error = Color(0xFFC62828),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1C1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF44483E),
    outline = Color(0xFF74796D),
    outlineVariant = Color(0xFFC4C8BA),
)

private val ElderlyFriendlyDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003909),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF1B262C),
    secondaryContainer = Color(0xFF37474F),
    onSecondaryContainer = Color(0xFFCFD8DC),
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF00695C),
    onTertiaryContainer = Color(0xFF80CBC4),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1C1A),
    onBackground = Color(0xFFE2E3DE),
    surface = Color(0xFF1A1C1A),
    onSurface = Color(0xFFE2E3DE),
    surfaceVariant = Color(0xFF44483E),
    onSurfaceVariant = Color(0xFFC4C8BA),
    outline = Color(0xFF8E9286),
    outlineVariant = Color(0xFF44483E),
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF003300),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF1A1A1A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF333333),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF003333),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF00695C),
    onTertiaryContainer = Color.White,
    error = Color(0xFF8B0000),
    onError = Color.White,
    errorContainer = Color(0xFFC62828),
    onErrorContainer = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF1A1A1A),
    outline = Color.Black,
    outlineVariant = Color(0xFF333333),
)

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color(0xFFB9F6CA),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF81C784),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFFE0E0E0),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB0BEC5),
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFFB2DFDB),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF80CBC4),
    onTertiaryContainer = Color.Black,
    error = Color(0xFFFFCDD2),
    onError = Color.Black,
    errorContainer = Color(0xFFFFB4AB),
    onErrorContainer = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF333333),
    onSurfaceVariant = Color(0xFFE0E0E0),
    outline = Color.White,
    outlineVariant = Color(0xFFCCCCCC),
)

@Composable
fun TerminatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast && darkTheme -> HighContrastDarkColorScheme
        highContrast -> HighContrastLightColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ElderlyFriendlyDarkColorScheme
        else -> ElderlyFriendlyLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ElderlyTypography,
        content = content
    )
}
