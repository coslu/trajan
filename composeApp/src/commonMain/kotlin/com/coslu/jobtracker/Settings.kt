package com.coslu.jobtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coslu.jobtracker.Job.Companion.list
import com.coslu.jobtracker.Job.Companion.statuses
import com.coslu.jobtracker.SortingMethod.Date
import com.coslu.jobtracker.SortingMethod.Location
import com.coslu.jobtracker.SortingMethod.Name
import com.coslu.jobtracker.SortingMethod.Status
import com.coslu.jobtracker.SortingMethod.Type
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.dark_mode
import job_tracker.composeapp.generated.resources.light_mode
import job_tracker.composeapp.generated.resources.system_theme
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import java.util.Locale

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
    val searchString = mutableStateOf("")
    val searchInTypes = mutableStateOf(true)
    val searchInLocations = mutableStateOf(true)
    val searchInNotes = mutableStateOf(true)

    sealed class Option {
        abstract val name: String
        abstract val id: String

        @Composable
        open fun Icon(): Unit? = null
    }

    object Theme {
        class ThemeOption(
            override val name: String,
            override val id: String,
            val iconDrawable: DrawableResource
        ) :
            Option() {
            @Composable
            override fun Icon() {
                Icon(painterResource(iconDrawable), null)
            }
        }

        val System = ThemeOption("System", "System", Res.drawable.system_theme)
        val Light = ThemeOption("Light", "Light", Res.drawable.light_mode)
        val Dark = ThemeOption("Dark", "Dark", Res.drawable.dark_mode)
        val options = listOf(System, Light, Dark)
        val current = mutableStateOf(System)

        @Composable
        fun isDark() = current.value == System && isSystemInDarkTheme() || current.value == Dark
    }

    object Color {
        class ColorOption(
            override val name: String,
            override val id: String,
            val lightScheme: ColorScheme,
            val darkScheme: ColorScheme
        ) : Option() {
            @Composable
            override fun Icon() {
                Box(Modifier.background(iconColor(), CircleShape).size(24.dp))
            }

            @Composable
            fun iconColor() = if (Theme.isDark()) darkScheme.primary else lightScheme.primary
        }

        val Green = ColorOption("Trajan Green", "Green", greenLightScheme, greenDarkScheme)
        val Blue = ColorOption("Blue", "Blue", blueLightScheme, blueDarkScheme)
        val Purple = ColorOption("Purple", "Purple", purpleLightScheme, purpleDarkScheme)
        val Yellow = ColorOption("Yellow", "Yellow", yellowLightScheme, yellowDarkScheme)
        val Red = ColorOption("Red", "Red", redLightScheme, redDarkScheme)
        val Gray = ColorOption("Gray", "Gray", grayLightScheme, grayDarkScheme)
        val options = listOf(Green, Blue, Purple, Yellow, Red, Gray)
        val current = mutableStateOf(Green)
        val useSystemColors = mutableStateOf(true)
    }

    object Language {
        class LanguageOption(override val name: String, override val id: String, val locale: Locale) : Option()

        val English = LanguageOption("English", "English", Locale.ENGLISH)
        val Turkish = LanguageOption("Türkçe", "Turkish", Locale.forLanguageTag("tr-TR"))
        val options = listOf(English, Turkish)
        val current = mutableStateOf(English)
    }

    fun applyFilters() = jobs.run {
        clear()
        addAll(list.filter { typeFilters[it.type]!! && locationFilters[it.location]!! && statusFilters[it.status]!! })
        sortWith(sortingMethod.comparator)
        searchString.value.split(" ").forEach { searchWord ->
            jobs.removeAll {
                !(it.name.contains(searchWord, true)
                        || searchInTypes.value && it.type.contains(searchWord, true)
                        || searchInLocations.value && it.location.contains(searchWord, true)
                        || searchInNotes.value && it.notes.contains(searchWord, true))
            }
        }
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
            element<Boolean>("searchInTypes")
            element<Boolean>("searchInLocations")
            element<Boolean>("searchInNotes")
            element<String>("theme")
            element<Boolean>("useSystemColors")
            element<String>("color")
            element<String>("language")
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

                    4 -> Settings.searchInTypes.value = decodeBooleanElement(descriptor, 4)
                    5 -> Settings.searchInLocations.value = decodeBooleanElement(descriptor, 5)
                    6 -> Settings.searchInNotes.value = decodeBooleanElement(descriptor, 6)
                    7 -> {
                        val theme = decodeStringElement(descriptor, 7)
                        Settings.Theme.current.value =
                            Settings.Theme.options.first { it.id == theme }
                    }

                    8 -> Settings.Color.useSystemColors.value = decodeBooleanElement(descriptor, 8)
                    9 -> {
                        val color = decodeStringElement(descriptor, 9)
                        Settings.Color.current.value =
                            Settings.Color.options.first { it.id == color }
                    }
                    10 -> {
                        val language = decodeStringElement(descriptor, 10)
                        Settings.Language.current.value =
                            Settings.Language.options.first { it.id == language }
                    }

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
            encodeBooleanElement(descriptor, 4, value.searchInTypes.value)
            encodeBooleanElement(descriptor, 5, value.searchInLocations.value)
            encodeBooleanElement(descriptor, 6, value.searchInNotes.value)
            encodeStringElement(descriptor, 7, Settings.Theme.current.value.id)
            encodeBooleanElement(descriptor, 8, Settings.Color.useSystemColors.value)
            encodeStringElement(descriptor, 9, Settings.Color.current.value.id)
            encodeStringElement(descriptor, 10, Settings.Language.current.value.id)
        }
    }
}

expect fun saveSettings()

expect fun fetchSettings()
