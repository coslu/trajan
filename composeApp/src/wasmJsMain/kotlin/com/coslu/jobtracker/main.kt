package com.coslu.jobtracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.serialization.json.Json

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App()
    }
}

actual fun fetchJobList(): List<Job> {
    return mutableListOf() //TODO
}

actual fun saveJobList(list: List<Job>) {
    //TODO
}