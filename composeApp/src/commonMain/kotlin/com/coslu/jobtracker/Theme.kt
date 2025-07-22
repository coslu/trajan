package com.coslu.jobtracker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val lightScheme = lightColorScheme(
    primary = Color(0xFF546524),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6EB9B),
    onPrimaryContainer = Color(0xFF3D4C0D),
    secondary = Color(0xFF5B6147),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDFE6C4),
    onSecondaryContainer = Color(0xFF434931),
    tertiary = Color(0xFF39665F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBCECE2),
    onTertiaryContainer = Color(0xFF204E47),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    background = Color(0xFFFAFAEE),
    onBackground = Color(0xFF1B1C15),
    surface = Color(0xFFFAFAEE),
    onSurface = Color(0xFF1B1C15),
    surfaceVariant = Color(0xFFE3E4D3),
    onSurfaceVariant = Color(0xFF46483C),
    outline = Color(0xFF76786B),
    outlineVariant = Color(0xFFC6C8B8),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF303129),
    inverseOnSurface = Color(0xFFF2F1E5),
    inversePrimary = Color(0xFFBBCF82),
    surfaceDim = Color(0xFFDBDBCF),
    surfaceBright = Color(0xFFFAFAEE),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF5F4E8),
    surfaceContainer = Color(0xFFEFEEE2),
    surfaceContainerHigh = Color(0xFFE9E9DD),
    surfaceContainerHighest = Color(0xFFE3E3D7),
)

val darkScheme = darkColorScheme(
    primary = Color(0xFFBBCF82),
    onPrimary = Color(0xFF283500),
    primaryContainer = Color(0xFF3D4C0D),
    onPrimaryContainer = Color(0xFFD6EB9B),
    secondary = Color(0xFFC3CAA9),
    onSecondary = Color(0xFF2D331C),
    secondaryContainer = Color(0xFF434931),
    onSecondaryContainer = Color(0xFFDFE6C4),
    tertiary = Color(0xFFA1D0C6),
    onTertiary = Color(0xFF023731),
    tertiaryContainer = Color(0xFF204E47),
    onTertiaryContainer = Color(0xFFBCECE2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF12140D),
    onBackground = Color(0xFFE3E3D7),
    surface = Color(0xFF12140D),
    onSurface = Color(0xFFE3E3D7),
    surfaceVariant = Color(0xFF46483C),
    onSurfaceVariant = Color(0xFFC6C8B8),
    outline = Color(0xFF909284),
    outlineVariant = Color(0xFF46483C),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE3E3D7),
    inverseOnSurface = Color(0xFF303129),
    inversePrimary = Color(0xFF546524),
    surfaceDim = Color(0xFF12140D),
    surfaceBright = Color(0xFF383A32),
    surfaceContainerLowest = Color(0xFF0D0F08),
    surfaceContainerLow = Color(0xFF1B1C15),
    surfaceContainer = Color(0xFF1F2019),
    surfaceContainerHigh = Color(0xFF292B23),
    surfaceContainerHighest = Color(0xFF34362D),
)

@Composable
expect fun TrajanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit
)
