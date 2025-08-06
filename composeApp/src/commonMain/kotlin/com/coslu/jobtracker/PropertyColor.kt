package com.coslu.jobtracker

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private val textOnLight = Color(0xFF1B1C15)
private val textOnDark = Color(0xFFFFFFFF)

@Serializable(with = PropertyColorSerializer::class)
enum class PropertyColor(val color: Color, val textColor: Color) {
    Transparent(Color(0), Color.Unspecified),
    BlueGray(Color(0xFF89A8B2), textOnDark),
    LightBlue(Color(0xFFA2D2DF), textOnLight),
    DarkBlue(Color(0xFF295F98), textOnDark),
    Teal(Color(0xFF86C8BC), textOnLight),
    LightGreen(Color(0xFFD6F8B8), textOnLight),
    DarkGreen(Color(0xFF587850), textOnDark),
    LightPurple(Color(0xFFBA94D1), textOnDark),
    DarkPurple(Color(0xFF7F669D), textOnDark),
    Brown(Color(0xFF8B7E74), textOnDark),
    Cream(Color(0xFFC7BCA1), textOnLight),
    Orange(Color(0xFFFAAB78), textOnLight),
    Yellow(Color(0xFFFFE9AE), textOnLight),
    Pink(Color(0xFFFFB3B3), textOnLight),
    Maroon(Color(0xFFC37B89), textOnDark),
    Red(Color(0xFFD35D6E), textOnDark),
    LightGray(Color(0xFFD1D1D1), textOnLight),
    DarkGray(Color(0xFF797A7E), textOnDark)
}

private class PropertyColorSerializer : KSerializer<PropertyColor> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.coslu.jobtracker.PropertyColor", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PropertyColor {
        return PropertyColor.valueOf(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: PropertyColor) {
        encoder.encodeString(value.name)
    }
}

val defaultStatusColors = listOf(
    Pair("Pending Application", PropertyColor.LightBlue),
    Pair("Awaiting Response", PropertyColor.LightGray),
    Pair("Rejected", PropertyColor.Red),
    Pair("Meeting Scheduled", PropertyColor.LightGreen),
)