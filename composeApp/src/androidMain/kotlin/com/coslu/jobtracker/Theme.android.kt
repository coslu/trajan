package com.coslu.jobtracker

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun TrajanTheme(darkTheme: Boolean, content: @Composable() () -> Unit) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && Settings.useSystemColors.value -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> Settings.darkScheme.value
        else -> Settings.lightScheme.value
    }

    MaterialTheme(
        colorScheme = colorScheme, content = content
    )
}