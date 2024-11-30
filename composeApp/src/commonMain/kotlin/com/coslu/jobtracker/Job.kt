package com.coslu.jobtracker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Status(val statusText: String) {
    @SerialName("Pending Application")
    PENDING_APPLICATION("Pending Application"),

    @SerialName("Awaiting Response")
    AWAITING_RESPONSE("Awaiting Response"),

    @SerialName("Rejected")
    REJECTED("Rejected"),

    @SerialName("Meeting Scheduled")
    MEETING_SCHEDULED("Meeting Scheduled");

    override fun toString(): String {
        return when (this) {
            PENDING_APPLICATION -> "Pending Application"
            AWAITING_RESPONSE -> "Awaiting Response"
            REJECTED -> "Rejected"
            MEETING_SCHEDULED -> "Meeting Scheduled"
        }
    }
}

@Serializable
data class Job(
    var name: String,
    var url: String,
    var type: String,
    var location: String,
    var status: Status = Status.PENDING_APPLICATION
)

expect fun fetchJobList(): List<Job>

expect fun saveJobList(list: List<Job>)

expect fun fetchPropertyColors(): List<Pair<String, PropertyColor>>

expect fun savePropertyColors(map: List<Pair<String, PropertyColor>>)