package com.coslu.jobtracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

val LocalLocale = staticCompositionLocalOf { Locale.ENGLISH }

@Composable
fun CommonAppLocale(content: @Composable () -> Unit) {
    var currentLanguage by remember { Settings.Language.current }
    CompositionLocalProvider(LocalLocale provides currentLanguage.locale) {
        content()
    }
}

@Composable
expect fun AppLocale(content: @Composable () -> Unit)