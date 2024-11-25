package com.coslu.jobtracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.Debug
import java.awt.Desktop
import java.io.File
import java.io.FileNotFoundException
import java.net.URI

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "job-tracker",
    ) {
        App()
    }
}

actual fun fetchJobList(): List<Job> {
    return try {
        Json.decodeFromString<MutableList<Job>>(File("jobs.txt").readText())
    } catch (ex: FileNotFoundException) {
        listOf()
    }
}

actual fun saveJobList(list: List<Job>) {
    File("jobs.txt").writeText(Json.encodeToString(list))
}