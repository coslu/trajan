package com.coslu.jobtracker

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

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

actual fun fetchPropertyColors(): List<Pair<String, PropertyColor>> {
    return listOf() //TODO
}

actual fun savePropertyColors(map: List<Pair<String, PropertyColor>>) {
    //TODO
}