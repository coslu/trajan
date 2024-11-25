package com.coslu.jobtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.baseline_open_in_new_24
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    @SerialName("Pending Application")
    PENDING_APPLICATION,

    @SerialName("Awaiting Response")
    AWAITING_RESPONSE,

    @SerialName("Rejected")
    REJECTED,

    @SerialName("Meeting Scheduled")
    MEETING_SCHEDULED;

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
    val name: String,
    val url: String,
    val type: String,
    val location: String,
    val status: Status = Status.PENDING_APPLICATION
)

expect fun fetchJobList(): List<Job>

expect fun saveJobList(list: List<Job>)

@Composable
@Preview
fun App() {
    MaterialTheme(
        colors = Colors(
            primary = Color(0xFF546524),
            primaryVariant = Color(0xFFD7EB9B),
            secondary = Color(0xFF5B6147),
            secondaryVariant = Color(0xFF5B6147),
            background = Color(0xFFF6FBF3),
            surface = Color(0xFFFBFAEE),
            error = Color(0xFFBA1A1A),
            onPrimary = Color(0xFFFFFFFF),
            onSecondary = Color(0xFFFFFFFF),
            onBackground = Color(0xFF181D19),
            onSurface = Color(0xFF1B1C15),
            onError = Color(0xFFFFFFFF),
            isLight = true
        )
    ) {
        val list = remember { fetchJobList().toMutableStateList() }
        LazyColumn {
            items(list) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillParentMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                            .fillParentMaxWidth(0.9f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableTextItem(it.name, it.url, Modifier.width(150.dp))
                        TableBoxItem(it.type, Modifier.weight(1f, false), Color.Gray)
                        TableBoxItem(it.location, Modifier.weight(1f, false), Color.Blue)
                        TableBoxItem(it.status.toString(), Modifier.weight(1f, false), Color.Green)
                    }
                    IconButton(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                        onClick = {
                            //TODO
                        },
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color(0xFF546524))
                    }
                }
            }
        }
    }
}

@Composable
fun TableTextItem(text: String, url: String, modifier: Modifier, fontWeight: FontWeight? = null) {
    val uriHandler = LocalUriHandler.current
    val annotatedText = buildAnnotatedString {
        append("$text ")
        appendInlineContent("inlineContent", "[Open]")
    }
    val inlineContent = mapOf(
        Pair(
            "inlineContent",
            InlineTextContent(
                Placeholder(
                    width = 20.sp,
                    height = 20.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(painterResource(Res.drawable.baseline_open_in_new_24), "Go to Job")
            })
    )
    Row(modifier = modifier) {
        TextButton(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            onClick = {
                uriHandler.openUri(url)
            }
        ) {
            Text(
                annotatedText,
                inlineContent = inlineContent,
                fontWeight = fontWeight,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TableBoxItem(text: String, modifier: Modifier, color: Color) {
    BoxWithConstraints(modifier = modifier) {
        if (maxWidth < 150.dp) {
            Row(modifier = Modifier.padding(start = 5.dp)) {
                Box(
                    modifier = Modifier.shadow(5.dp, RoundedCornerShape(50))
                        .background(color, RoundedCornerShape(50)).size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text.first().toString(),
                    )
                }
            }
        } else {
            Row(modifier = Modifier.padding(start = 10.dp)) {
                Box(
                    modifier = Modifier.shadow(5.dp, RoundedCornerShape(30))
                        .background(color, shape = RoundedCornerShape(30))
                        .wrapContentWidth(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

