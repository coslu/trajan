package com.coslu.jobtracker

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

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
        Json.decodeFromString<MutableList<Job>>(File("jobs.json").readText())
    } catch (ex: Exception) {
        listOf()
    }
}

actual fun saveJobList(list: List<Job>) {
    File("jobs.json").writeText(Json.encodeToString(list))
}

actual fun fetchPropertyColors(): List<Pair<String, PropertyColor>> {
    return try {
        Json.decodeFromString<List<Pair<String, PropertyColor>>>(File("colors.json").readText())
    } catch (ex: Exception) {
        defaultStatusColors
    }
}

actual fun savePropertyColors(map: List<Pair<String, PropertyColor>>) {
    File("colors.json").writeText(Json.encodeToString(map))
}