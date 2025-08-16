package com.coslu.jobtracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun TrajanTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) Settings.darkScheme.value else Settings.lightScheme.value
    MaterialTheme(colorScheme = colorScheme, content = content)
}