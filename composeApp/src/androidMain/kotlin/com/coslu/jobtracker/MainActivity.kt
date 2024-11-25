package com.coslu.jobtracker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException


lateinit var dir: File

actual fun fetchJobList(): List<Job> {
    return try {
        Json.decodeFromString<MutableList<Job>>(File(dir, "jobs.txt").readText())
    } catch (ex: FileNotFoundException) {
        listOf()
    }
}

actual fun saveJobList(list: List<Job>) {
    File(dir,"jobs.txt").writeText(Json.encodeToString(list))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dir = filesDir
        setContent {
            App()
        }
    }

    fun start(intent: Intent) {

    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}