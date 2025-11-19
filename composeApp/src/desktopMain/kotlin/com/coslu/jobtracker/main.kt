package com.coslu.jobtracker

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.icon_linux
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

private val json = Json { prettyPrint = true }

private var windowState = fetchWindowState()
private var position = windowState.position
private var size = windowState.size

fun main() {
    FileKit.init("Trajan")
    application {
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
        json.decodeFromString(
            WindowStateSerializer(),
            dataDir.resolve("window_state.json").readText()
        )
    } catch (_: Exception) {
        WindowState(WindowPlacement.Maximized)
    }
}

private fun saveWindowState() {
    if (windowState.placement == WindowPlacement.Maximized) {
        windowState.position = position
        windowState.size = size
    }
    try {
        dataDir.resolve("window_state.json").createParentDirectories()
            .writeText(json.encodeToString(WindowStateSerializer(), windowState))
    } catch (e: Exception) {
        showSnackbar("Error when saving file: '${e.message}'")
    }
}
