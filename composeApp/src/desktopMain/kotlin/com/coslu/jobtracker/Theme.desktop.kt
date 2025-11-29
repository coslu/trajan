package com.coslu.jobtracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun AppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) Settings.Color.current.value.darkScheme else Settings.Color.current.value.lightScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}