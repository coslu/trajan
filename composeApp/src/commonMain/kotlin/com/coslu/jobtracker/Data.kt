package com.coslu.jobtracker

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import kotlinx.serialization.json.Json
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

val dataDir get() = Path(FileKit.filesDir.path)

private val json = Json { prettyPrint = true }

fun fetchJobList(jsonString: String = dataDir.resolve("jobs.json").readText()): List<Job> {
    return try {
        json.decodeFromString<MutableList<Job>>(jsonString)
    } catch (_: Exception) {
        listOf()
    }
}

fun saveJobList() {
    try {
        dataDir.resolve("jobs.json").createParentDirectories()
            .writeText(json.encodeToString(Job.list))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

fun fetchPropertyColors(
    jsonString: String = dataDir.resolve("colors.json").readText()
): List<Pair<String, PropertyColor>> {
    return try {
        json.decodeFromString<List<Pair<String, PropertyColor>>>(jsonString)
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

fun fetchSettings(jsonString: String = dataDir.resolve("settings.json").readText()) {
    runCatching {
        json.decodeFromString<Settings>(jsonString)
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

expect fun PlatformFile.openZipOutputStream(): ZipOutputStream

expect fun PlatformFile.openZipInputStream(): ZipInputStream