package com.coslu.jobtracker.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun TrajanTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) darkScheme else lightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}