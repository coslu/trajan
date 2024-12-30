package com.coslu.jobtracker

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

val statuses = listOf("Pending Application", "Awaiting Response", "Rejected", "Meeting Scheduled")

@Serializable(with = JobSerializer::class)
data class Job(
    var name: String = "",
    var url: String = "",
    var type: String = "",
    var location: String = "",
    var status: String = "Pending Application",
    var comment: String = ""
)

private class JobSerializer : KSerializer<Job> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("com.coslu.job") {
        element<String>("name")
        element<String>("url")
        element<String>("type")
        element<String>("location")
        element<String>("status")
        element<String>("comment")
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
                    5 -> job.comment = decodeStringElement(descriptor, 5)
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
            encodeStringElement(descriptor, 5, value.comment)
        }
    }
}

expect fun fetchJobList(): List<Job>

expect fun saveJobList(list: List<Job>)

expect fun fetchPropertyColors(): List<Pair<String, PropertyColor>>

expect fun savePropertyColors(map: List<Pair<String, PropertyColor>>)