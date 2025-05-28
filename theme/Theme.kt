package com.example.moderncalculator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Modern color scheme inspired by gaming sites
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),      // Cyan neon
    onPrimary = Color(0xFF003D40),
    primaryContainer = Color(0xFF00767D),
    onPrimaryContainer = Color(0xFF70F3FF),

    secondary = Color(0xFFFF00E5),    // Magenta neon
    onSecondary = Color(0xFF4D0040),
    secondaryContainer = Color(0xFF702D60),
    onSecondaryContainer = Color(0xFFFFD7F3),

    tertiary = Color(0xFFFFE500),     // Yellow neon
    onTertiary = Color(0xFF3F3000),
    tertiaryContainer = Color(0xFF5C4600),
    onTertiaryContainer = Color(0xFFFFEFC5),

    error = Color(0xFFFF4444),
    onError = Color(0xFF690000),
    errorContainer = Color(0xFF930000),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF0A0E1A),   // Deep dark blue
    onBackground = Color(0xFFE0E3F0),

    surface = Color(0xFF141825),      // Slightly lighter dark
    onSurface = Color(0xFFE0E3F0),
    surfaceVariant = Color(0xFF1E2332),
    onSurfaceVariant = Color(0xFFC0C7DC),

    outline = Color(0xFF8A91A6),
    outlineVariant = Color(0xFF3F4657),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE0E3F0),
    inverseOnSurface = Color(0xFF2D3142),
    inversePrimary = Color(0xFF006970),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006970),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF70F3FF),
    onPrimaryContainer = Color(0xFF002022),

    secondary = Color(0xFF8E366B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD7ED),
    onSecondaryContainer = Color(0xFF3A0026),

    tertiary = Color(0xFF765C00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE08D),
    onTertiaryContainer = Color(0xFF241A00),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF191C23),

    surface = Color(0xFFF8F9FF),
    onSurface = Color(0xFF191C23),
    surfaceVariant = Color(0xFFDDE3F0),
    onSurfaceVariant = Color(0xFF414753),

    outline = Color(0xFF717785),
    outlineVariant = Color(0xFFC1C7D7),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2E3138),
    inverseOnSurface = Color(0xFFEFF1F9),
    inversePrimary = Color(0xFF4DD9E9),
)

@Composable
fun ModernCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as androidx.activity.ComponentActivity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}