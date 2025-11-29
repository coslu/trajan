package com.coslu.jobtracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

@Composable
actual fun AppLocale(content: @Composable () -> Unit) {
    CommonAppLocale {
        Locale.setDefault(LocalLocale.current)
        LocalConfiguration.current.setLocale(LocalLocale.current)
        content()
    }
}