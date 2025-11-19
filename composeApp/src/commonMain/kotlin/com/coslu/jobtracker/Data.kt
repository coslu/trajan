package com.coslu.jobtracker

import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

lateinit var dataDir: Path

private val json = Json { prettyPrint = true }

fun fetchJobList(): List<Job> {
    return try {
        json.decodeFromString<MutableList<Job>>(dataDir.resolve("jobs.json").readText())
    } catch (_: Exception) {
        listOf()
    }
}

fun saveJobList(list: List<Job>) {
    try {
        dataDir.resolve("jobs.json").createParentDirectories().writeText(json.encodeToString(list))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

fun fetchPropertyColors(): List<Pair<String, PropertyColor>> {
    return try {
        json.decodeFromString<List<Pair<String, PropertyColor>>>(
            dataDir.resolve("colors.json").readText()
        )
    } catch (_: Exception) {
        defaultStatusColors
    }
}

fun savePropertyColors(map: List<Pair<String, PropertyColor>>) {
    try {
        dataDir.resolve("colors.json").createParentDirectories().writeText(json.encodeToString(map))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

fun fetchSettings() {
    runCatching {
        json.decodeFromString<Settings>(dataDir.resolve("settings.json").readText())
    }
}

fun saveSettings() {
    try {
        dataDir.resolve("settings.json").createParentDirectories()
            .writeText(json.encodeToString(Settings))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}