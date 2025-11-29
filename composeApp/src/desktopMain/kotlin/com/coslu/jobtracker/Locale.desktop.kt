package com.coslu.jobtracker

import androidx.compose.runtime.Composable
import java.util.Locale

@Composable
actual fun AppLocale(content: @Composable () -> Unit) {
    CommonAppLocale {
        Locale.setDefault(LocalLocale.current)
        content()
    }
}
