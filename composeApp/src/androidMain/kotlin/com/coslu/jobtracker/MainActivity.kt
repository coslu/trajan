package com.coslu.jobtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.serialization.json.Json
import java.io.File

private lateinit var dir: File
private val json = Json { prettyPrint = true }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dir = filesDir
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

actual fun fetchJobList(): List<Job> {
    return try {
        json.decodeFromString<MutableList<Job>>(File(dir, "jobs.json").readText())
    } catch (_: Exception) {
        listOf()
    }
}

actual fun saveJobList(list: List<Job>) {
    try {
        File(dir, "jobs.json").writeText(json.encodeToString(list))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

actual fun fetchPropertyColors(): List<Pair<String, PropertyColor>> {
    return try {
        json.decodeFromString<List<Pair<String, PropertyColor>>>(
            File(dir, "colors.json").readText()
        )
    } catch (_: Exception) {
        defaultStatusColors
    }
}

actual fun savePropertyColors(map: List<Pair<String, PropertyColor>>) {
    try {
        File(dir, "colors.json").writeText(json.encodeToString(map))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

actual fun saveSettings() {
    try {
        File(dir, "settings.json").writeText(json.encodeToString(Settings))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

actual fun fetchSettings() {
    runCatching {
        json.decodeFromString<Settings>(File(dir, "settings.json").readText())
    }
}