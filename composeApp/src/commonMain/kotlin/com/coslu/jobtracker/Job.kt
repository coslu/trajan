package com.coslu.jobtracker

import androidx.compose.animation.core.MutableTransitionState
import com.coslu.jobtracker.Settings.applyFilters
import com.coslu.jobtracker.Settings.locationFilters
import com.coslu.jobtracker.Settings.sortingMethod
import com.coslu.jobtracker.Settings.typeFilters
import kotlinx.datetime.Clock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = JobSerializer::class)
class Job(
    var name: String = "",
    var url: String = "",
    var type: String = "",
    var location: String = "",
    var status: String = "Pending Application",
    var notes: String = "",
    val date: Long = Clock.System.now().toEpochMilliseconds()
) {
    companion object {
        val locations = mutableMapOf<String, Int>()
        val types = mutableMapOf<String, Int>()
        val statuses = listOf(
            "Awaiting Response",
            "Meeting Scheduled",
            "Pending Application",
            "Rejected"
        )
        val list = fetchJobList().toMutableList().onEach {
            it.addPropertiesToDictionary()
        }
        private var count = 0
    }

    var visible: MutableTransitionState<Boolean> = MutableTransitionState(true)
    private val id = count++

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Job

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    private fun addPropertiesToDictionary() {
        locations[location] = locations.getOrDefault(location, 0) + 1
        locationFilters.getOrPut(location) { true }
        types[type] = types.getOrDefault(type, 0) + 1
        typeFilters.getOrPut(type) { true }
    }

    private fun removePropertiesFromDictionary() {
        locations[location] = locations.getValue(location) - 1
        if (locations[location] == 0) {
            locations.remove(location)
            locationFilters.remove(location)
        }
        types[type] = types.getValue(type) - 1
        if (types[type] == 0) {
            types.remove(type)
            typeFilters.remove(type)
        }
    }

    fun add() {
        visible = MutableTransitionState(false).apply { targetState = true }
        list.add(this)
        saveJobList(list)
        addPropertiesToDictionary()
        applyFilters()
        if (jobs.contains(this))
            jumpToItem(jobs.indexOf(this))
    }

    fun remove() {
        list.remove(this)
        saveJobList(list)
        removePropertiesFromDictionary()
        visible.targetState = false
    }

    fun edit(
        name: String,
        url: String,
        type: String,
        location: String,
        status: String,
        notes: String
    ) {
        removePropertiesFromDictionary()
        this.name = name
        this.url = url
        this.type = type
        this.location = location
        this.status = status
        this.notes = notes
        saveJobList(list)
        addPropertiesToDictionary()
        // we do the following to update lazy column
        val index = jobs.indexOf(this)
        jobs.removeAt(index)
        jobs.add(index, this)
        jobs.sortWith(sortingMethod.comparator)
    }
}

private class JobSerializer : KSerializer<Job> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("com.coslu.job") {
        element<String>("name")
        element<String>("url")
        element<String>("type")
        element<String>("location")
        element<String>("status")
        element<String>("notes")
    }

    override fun deserialize(decoder: Decoder): Job {
        val job = Job()
        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    DECODE_DONE -> break@loop
                    0 -> job.name = decodeStringElement(descriptor, 0)
                    1 -> job.url = decodeStringElement(descriptor, 1)
                    2 -> job.type = decodeStringElement(descriptor, 2)
                    3 -> job.location = decodeStringElement(descriptor, 3)
                    4 -> job.status = decodeStringElement(descriptor, 4)
                    5 -> job.notes = decodeStringElement(descriptor, 5)
                    else -> throw SerializationException("Unexpected index $index")
                }
            }
        }
        return job
    }

    override fun serialize(encoder: Encoder, value: Job) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeStringElement(descriptor, 1, value.url)
            encodeStringElement(descriptor, 2, value.type)
            encodeStringElement(descriptor, 3, value.location)
            encodeStringElement(descriptor, 4, value.status)
            encodeStringElement(descriptor, 5, value.notes)
        }
    }
}

expect fun fetchJobList(): List<Job>

expect fun saveJobList(list: List<Job>)

expect fun fetchPropertyColors(): List<Pair<String, PropertyColor>>

expect fun savePropertyColors(map: List<Pair<String, PropertyColor>>)