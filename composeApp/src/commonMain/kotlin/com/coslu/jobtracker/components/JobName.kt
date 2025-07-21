package com.coslu.jobtracker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.coslu.jobtracker.showSnackbar
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.open_link
import org.jetbrains.compose.resources.painterResource

@Composable
fun JobName(text: String, url: String, modifier: Modifier) {
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
                Icon(painterResource(Res.drawable.open_link), "Go to Job")
            })
    )
    Row(modifier = modifier) {
        if (url.isBlank()) {
            Row(
                Modifier
                    .defaultMinSize(
                        minWidth = ButtonDefaults.MinWidth,
                        minHeight = ButtonDefaults.MinHeight
                    )
                    .padding(ButtonDefaults.TextButtonContentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        } else {
            TextButton(
                onClick = {
                    try {
                        uriHandler.openUri(url)
                    } catch (_: Exception) {
//                        showSnackbar("The URL is invalid or cannot be handled by the system.")
                    }
                },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text(
                    annotatedText,
                    inlineContent = inlineContent,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}