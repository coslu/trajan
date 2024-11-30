package com.coslu.jobtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


lateinit var dir: File

actual fun fetchJobList(): List<Job> {
    return try {
        Json.decodeFromString<MutableList<Job>>(File(dir, "jobs.txt").readText())
    } catch (ex: Exception) {
        listOf()
    }
}

actual fun saveJobList(list: List<Job>) {
    File(dir,"jobs.txt").writeText(Json.encodeToString(list))
}

actual fun fetchPropertyColors(): List<Pair<String, PropertyColor>> {
    return try {
        Json.decodeFromString<List<Pair<String, PropertyColor>>>(File(dir,"jobs.txt").readText())
    } catch (ex: Exception) {
        defaultStatusColors
    }
}

actual fun savePropertyColors(map: List<Pair<String, PropertyColor>>) {
    File(dir,"colors.json").writeText(Json.encodeToString(map))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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