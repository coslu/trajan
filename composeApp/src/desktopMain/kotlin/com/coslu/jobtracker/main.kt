package com.coslu.jobtracker

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.icon_linux
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Trajan",
        icon = painterResource(Res.drawable.icon_linux)
    ) {
        App()
    }
}

private val homeDir = Path(System.getProperty("user.home"))
private val dataDir =
    if (System.getProperty("os.name").lowercase().startsWith("windows"))
        homeDir.resolve("AppData/Roaming/Trajan")
    else
        homeDir.resolve(".local/share/Trajan")

actual fun fetchJobList(): List<Job> {
    return try {
        Json.decodeFromString<MutableList<Job>>(dataDir.resolve("jobs.json").readText())
    } catch (_: Exception) {
        listOf()
    }
}

actual fun saveJobList(list: List<Job>) {
    try {
        dataDir.resolve("jobs.json").createParentDirectories().writeText(Json.encodeToString(list))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

actual fun fetchPropertyColors(): List<Pair<String, PropertyColor>> {
    return try {
        Json.decodeFromString<List<Pair<String, PropertyColor>>>(
            dataDir.resolve("colors.json").readText()
        )
    } catch (e: Exception) {
        defaultStatusColors
    }
}

actual fun savePropertyColors(map: List<Pair<String, PropertyColor>>) {
    try {
        dataDir.resolve("colors.json").createParentDirectories().writeText(Json.encodeToString(map))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}