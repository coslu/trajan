package com.coslu.jobtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.coslu.jobtracker.SortingMethod.Date
import com.coslu.jobtracker.SortingMethod.Location
import com.coslu.jobtracker.SortingMethod.Name
import com.coslu.jobtracker.SortingMethod.Status
import com.coslu.jobtracker.SortingMethod.Type
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private lateinit var dir: File

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

actual fun fetchJobList(): List<Job> {
    return try {
        Json.decodeFromString<MutableList<Job>>(File(dir, "jobs.json").readText())
    } catch (ex: Exception) {
        listOf()
    }
}

actual fun saveJobList(list: List<Job>) {
    try {
        File(dir, "jobs.json").writeText(Json.encodeToString(list))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

actual fun fetchPropertyColors(): List<Pair<String, PropertyColor>> {
    return try {
        Json.decodeFromString<List<Pair<String, PropertyColor>>>(
            File(dir, "colors.json").readText()
        )
    } catch (ex: Exception) {
        defaultStatusColors
    }
}

actual fun savePropertyColors(map: List<Pair<String, PropertyColor>>) {
    try {
        File(dir, "colors.json").writeText(Json.encodeToString(map))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

actual fun saveSortingMethod(sortingMethod: SortingMethod) {
    try {
        File(dir, "sort.txt").writeText(sortingMethod.toString())
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

actual fun fetchSortingMethod(): SortingMethod {
    return try {
        File(dir, "sort.txt").readText().let {
            val descending = it.contains("true")
            when (it[0]) {
                'D' -> Date(descending)
                'N' -> Name(descending)
                'T' -> Type(descending)
                'L' -> Location(descending)
                'S' -> Status(descending)
                else -> Date(true)
            }
        }
    } catch (e: Exception) {
        Date(true)
    }
}