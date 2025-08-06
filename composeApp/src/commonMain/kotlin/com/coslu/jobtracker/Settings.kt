package com.coslu.jobtracker

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.coslu.jobtracker.Job.Companion.list
import com.coslu.jobtracker.Job.Companion.statuses
import com.coslu.jobtracker.SortingMethod.Date
import com.coslu.jobtracker.SortingMethod.Location
import com.coslu.jobtracker.SortingMethod.Name
import com.coslu.jobtracker.SortingMethod.Status
import com.coslu.jobtracker.SortingMethod.Type
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = SettingsSerializer::class)
object Settings {
    private val state = mutableStateOf<SortingMethod>(Date(true))
    var sortingMethod
        get() = state.value
        set(value) {
            state.value = value
            jobs.sortWith(sortingMethod.comparator)
            saveSettings()
        }
    val locationFilters = mutableStateMapOf<String, Boolean>()
    val typeFilters = mutableStateMapOf<String, Boolean>()
    val statusFilters = mutableStateMapOf<String, Boolean>().apply {
        statuses.forEach { put(it, true) }
    }

    fun applyFilters() = jobs.run {
        clear()
        addAll(list.filter { typeFilters[it.type]!! && locationFilters[it.location]!! && statusFilters[it.status]!! })
        sortWith(sortingMethod.comparator)
        saveSettings()
    }
}

private class SettingsSerializer : KSerializer<Settings> {
    val filterSerializer = MapSerializer(String.serializer(), Boolean.serializer())

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("com.coslu.jobtracker.Settings") {
            element<String>("sortingMethod")
            element("typeFilters", mapSerialDescriptor<String, Boolean>())
            element("locationFilters", mapSerialDescriptor<String, Boolean>())
            element("statusFilters", mapSerialDescriptor<String, Boolean>())
        }

    override fun deserialize(decoder: Decoder): Settings {
        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    DECODE_DONE -> break@loop
                    0 -> Settings.sortingMethod = decodeStringElement(descriptor, 0).let {
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

                    1 -> Settings.typeFilters.putAll(
                        decodeSerializableElement(
                            descriptor,
                            1,
                            filterSerializer
                        )
                    )

                    2 -> Settings.locationFilters.putAll(
                        decodeSerializableElement(
                            descriptor,
                            2,
                            filterSerializer
                        )
                    )

                    3 -> Settings.statusFilters.putAll(
                        decodeSerializableElement(
                            descriptor,
                            3,
                            filterSerializer
                        )
                    )

                    else -> throw SerializationException("Unexpected index $index")
                }
            }
        }
        return Settings
    }

    override fun serialize(encoder: Encoder, value: Settings) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.sortingMethod.toString())
            encodeSerializableElement(descriptor, 1, filterSerializer, value.typeFilters)
            encodeSerializableElement(descriptor, 2, filterSerializer, value.locationFilters)
            encodeSerializableElement(descriptor, 3, filterSerializer, value.statusFilters)
        }
    }
}

expect fun saveSettings()

expect fun fetchSettings()
