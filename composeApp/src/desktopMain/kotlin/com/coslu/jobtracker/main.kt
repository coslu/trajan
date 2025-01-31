package com.coslu.jobtracker

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.icon_linux
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val homeDir = Path(System.getProperty("user.home"))
private val dataDir =
    if (System.getProperty("os.name").lowercase().startsWith("windows"))
        homeDir.resolve("AppData/Roaming/Trajan")
    else
        homeDir.resolve(".local/share/Trajan")

private var windowState = fetchWindowState()
private var position = windowState.position
private var size = windowState.size

fun main() = application {
    LaunchedEffect(windowState.position, windowState.size) {
        if (windowState.placement != WindowPlacement.Maximized) {
            position = windowState.position
            size = windowState.size
        }
    }
    Window(
        state = windowState,
        onCloseRequest = { saveWindowState(); exitApplication() },
        title = "Trajan",
        icon = painterResource(Res.drawable.icon_linux)
    ) {
        App()
    }
}

private class WindowStateSerializer : KSerializer<WindowState> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("androidx.compose.ui.window.WindowState") {
            element<String>("placement")
            element<Float>("x")
            element<Float>("y")
            element<Float>("width")
            element<Float>("height")
        }

    override fun deserialize(decoder: Decoder): WindowState {
        var placement = WindowPlacement.Maximized
        var x = 0.dp
        var y = 0.dp
        var width = 800.dp
        var height = 600.dp
        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    DECODE_DONE -> break@loop
                    0 -> if (decodeStringElement(descriptor, 0) == "Floating")
                        placement = WindowPlacement.Floating

                    1 -> x = decodeFloatElement(descriptor, 1).dp
                    2 -> y = decodeFloatElement(descriptor, 2).dp
                    3 -> width = decodeFloatElement(descriptor, 3).dp
                    4 -> height = decodeFloatElement(descriptor, 4).dp
                    else -> throw SerializationException("Unexpected index $index")
                }
            }
        }
        return WindowState(
            placement = placement,
            position = WindowPosition(x, y),
            size = DpSize(width, height)
        )
    }

    override fun serialize(encoder: Encoder, value: WindowState) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.placement.name)
            encodeFloatElement(descriptor, 1, value.position.x.value)
            encodeFloatElement(descriptor, 2, value.position.y.value)
            encodeFloatElement(descriptor, 3, value.size.width.value)
            encodeFloatElement(descriptor, 4, value.size.height.value)
        }
    }
}

private fun fetchWindowState(): WindowState {
    return try {
        Json.decodeFromString(WindowStateSerializer(), dataDir.resolve("settings.json").readText())
    } catch (e: Exception) {
        WindowState(WindowPlacement.Maximized)
    }
}

private fun saveWindowState() {
    if (windowState.placement == WindowPlacement.Maximized) {
        windowState.position = position
        windowState.size = size
    }
    try {
        dataDir.resolve("settings.json").createParentDirectories()
            .writeText(Json.encodeToString(WindowStateSerializer(), windowState))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}

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