package com.coslu.jobtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.baseline_open_in_new_24
import org.jetbrains.compose.resources.painterResource

@Composable
fun JobProperty(text: String, modifier: Modifier, color: Color = Color(0)) {
    val shadowSize = if (color.alpha != 0f) 5.dp else 0.dp
    BoxWithConstraints(modifier = modifier) {
        if (maxWidth < 150.dp) {
            Row(modifier = Modifier.padding(start = 5.dp)) {
                Box(
                    modifier = Modifier.shadow(shadowSize, RoundedCornerShape(50))
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
                    modifier = Modifier.shadow(shadowSize, RoundedCornerShape(30))
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
                Icon(painterResource(Res.drawable.baseline_open_in_new_24), "Go to Job")
            })
    )
    Row(modifier = modifier) {
        TextButton(
            onClick = {
                uriHandler.openUri(url)
            }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun JobDialog(onDismissRequest: () -> Unit, list: SnapshotStateList<Job>, job: Job? = null) {
    var name by remember { mutableStateOf(job?.name ?: "") }
    var url by remember { mutableStateOf(job?.url ?: "") }
    var location by remember { mutableStateOf(job?.location ?: "") }
    var type by remember { mutableStateOf(job?.type ?: "") }
    var status by remember { mutableStateOf(job?.status ?: Status.PENDING_APPLICATION) }
    val buttonText = if (job != null) "Save Changes" else "Add Job"
    val title = if (job != null) "Edit Job" else "New Job"
    var expandStatusMenu by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).fillMaxWidth(0.8f)
                Text(title, modifier = modifier.padding(top = 10.dp), textAlign = TextAlign.Start, color = colors.primary, fontWeight = FontWeight.Bold)
                TextField(
                    value = name,
                    label = { Text("Company Name") },
                    modifier = modifier,
                    onValueChange = { name = it },
                    singleLine = true
                )
                TextField(
                    value = url,
                    label = { Text("URL of Job Posting") },
                    modifier = modifier,
                    onValueChange = { url = it },
                    singleLine = true
                )
                TextField(
                    value = type,
                    label = { Text("Type/Title of Work") },
                    modifier = modifier,
                    onValueChange = { type = it },
                    singleLine = true
                )
                TextField(
                    value = location,
                    label = { Text("Job Location") },
                    modifier = modifier,
                    onValueChange = { location = it },
                    singleLine = true
                )
                ExposedDropdownMenuBox(
                    expanded = expandStatusMenu,
                    onExpandedChange = {expandStatusMenu = !expandStatusMenu},
                ) {
                    TextField(
                        value = status.statusText,
                        label = { Text("Application Status") },
                        readOnly = true,
                        modifier = modifier,
                        onValueChange = {},
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, null)
                        }
                    )
                    ExposedDropdownMenu(expandStatusMenu, {expandStatusMenu = false}) {
                        Status.entries.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    status = it
                                    expandStatusMenu = false
                                }
                            ) {
                                Text(it.statusText)
                            }
                        }
                    }
                }
                Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                    Row(modifier = Modifier.weight(0.5f)) {
                        TextButton(
                            onClick = {
                                onDismissRequest()
                            },
                        ) {
                            Text("Dismiss")
                        }
                    }
                    Row(modifier = modifier.weight(0.5f), horizontalArrangement = Arrangement.End) {
                        TextButton(
                            onClick = {
                                if (job != null) {
                                    //TODO
                                } else {
                                    list.add(0, Job(name, url, type, location, status))
                                }
                                saveJobList(list)
                                onDismissRequest()
                            },
                        ) {
                            Text(buttonText)
                        }
                    }
                }
            }
        }
    }
}