package com.coslu.jobtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.baseline_block_24
import job_tracker.composeapp.generated.resources.baseline_open_in_new_24
import org.jetbrains.compose.resources.painterResource

/**
 * This file defines UI components for handling Jobs
 */

@Composable
fun JobProperty(
    property: String,
    modifier: Modifier,
) {
    var showColorPicker by remember { mutableStateOf(false) }
    BoxWithConstraints(modifier = modifier.pointerHoverIcon(PointerIcon.Hand)) {
        DropdownMenu(showColorPicker, { showColorPicker = false }) {
            Column(Modifier.height(150.dp).width(300.dp)) {
                LazyVerticalGrid(GridCells.Fixed(6)) {
                    items(PropertyColor.entries.toTypedArray()) {
                        IconButton(
                            onClick = {
                                propertyColors[property] = it
                                savePropertyColors(propertyColors.toList())
                                showColorPicker = false
                            },
                            modifier = Modifier.padding(5.dp)
                                .background(it.color, shape = CircleShape).size(40.dp)
                        ) {
                            if (it.color.alpha == 0f)
                                Icon(
                                    painterResource(Res.drawable.baseline_block_24),
                                    null,
                                    tint = Color.LightGray
                                )
                        }
                    }
                }
            }

        }
        if (maxWidth < 150.dp)
            SmallProperty(property) { showColorPicker = true }
        else if (property.isNotEmpty())
            BigProperty(property, clickable = true) { showColorPicker = true }
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
        if (url.isBlank()) {
            TextButtonStyledText(
                text,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            TextButton(
                onClick = {
                    try {
                        uriHandler.openUri(url)
                    } catch (e: Exception) {
                        showSnackbar("The URL is invalid or can't be handled by the system")
                    }
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun JobDialog(
    onDismissRequest: () -> Unit,
    job: Job? = null,
    locations: Map<String, Int>,
    types: Map<String, Int>
) {
    var name by remember { mutableStateOf(job?.name ?: "") }
    var url by remember { mutableStateOf(job?.url ?: "") }
    val location = mutableStateOf(job?.location ?: "")
    val type = mutableStateOf(job?.type ?: "")
    var status by remember { mutableStateOf(job?.status ?: "Pending Application") }
    val buttonText = if (job != null) "Save" else "Add Job"
    val title = if (job != null) "Edit Job" else "New Job"
    var expandStatusMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = Modifier.fillMaxWidth()) {
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    buttons = {
                        AlertDialogButtons(
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        jobs.remove(job)
                                        saveJobList(jobs)
                                        onDismissRequest()
                                    }
                                ) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                TextButton({ showDeleteDialog = false }) {
                                    Text("Dismiss")
                                }
                            }
                        )
                    },
                    title = {
                        Text(
                            "Confirm Delete",
                            color = colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = { Text("Are you sure you want to delete this job?") }
                )
            }
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                val modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).fillMaxWidth(0.8f)
                    .minimumInteractiveComponentSize()
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
                        Text(
                            title,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                        if (job != null) {
                            IconButton({ showDeleteDialog = true }) {
                                Icon(Icons.Filled.Delete, "Delete Job", tint = colors.primary)
                            }
                        }
                    }
                }
                item {
                    TextField(
                        value = name,
                        label = { Text("Company Name") },
                        modifier = modifier,
                        onValueChange = { name = it },
                        singleLine = true
                    )
                }
                item {
                    TextField(
                        value = url,
                        label = { Text("URL of Job Posting") },
                        modifier = modifier,
                        onValueChange = { url = it },
                        singleLine = true
                    )
                }
                item {
                    AutoCompleteTextField(type, modifier, types, "Type of Work")
                }
                item {
                    AutoCompleteTextField(location, modifier, locations, "Location")
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandStatusMenu,
                        onExpandedChange = { expandStatusMenu = !expandStatusMenu },
                    ) {
                        TextField(
                            value = status,
                            label = { Text("Application Status") },
                            readOnly = true,
                            modifier = modifier,
                            onValueChange = {},
                            trailingIcon = {
                                Icon(Icons.Filled.ArrowDropDown, null)
                            }
                        )
                        ExposedDropdownMenu(expandStatusMenu, { expandStatusMenu = false }) {
                            statuses.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        status = it
                                        expandStatusMenu = false
                                    }
                                ) {
                                    BigProperty(it)
                                }
                            }
                        }
                    }
                }
                item {
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
                        Row(
                            modifier = modifier.weight(0.5f),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    if (job != null) {
                                        jobs[jobs.indexOf(job)] =
                                            Job(name, url, type.value, location.value, status, job.comment)
                                    } else {
                                        jobs.add(
                                            0,
                                            Job(name, url, type.value, location.value, status, "")
                                        )
                                    }
                                    saveJobList(jobs)
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
}

@Composable
fun BigProperty(property: String, clickable: Boolean = false, onClick: () -> Unit = {}) {
    val propertyColor = propertyColors[property] ?: PropertyColor.Transparent
    val shadowSize = if (propertyColor != PropertyColor.Transparent) 5.dp else 0.dp
    Row(modifier = Modifier.padding(start = 5.dp, end = 5.dp)) {
        var modifier = Modifier.shadow(shadowSize, RoundedCornerShape(30))
            .background(propertyColor.color, shape = RoundedCornerShape(30))
            .wrapContentWidth()
        if (clickable)
            modifier = modifier.clickable(onClick = onClick)
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                property,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = propertyColor.textColor
            )
        }
    }
}

@Composable
fun SmallProperty(property: String, onClick: () -> Unit = {}) {
    val propertyColor = propertyColors[property] ?: PropertyColor.Transparent
    val shadowSize = if (propertyColor != PropertyColor.Transparent) 5.dp else 0.dp
    if (property.isEmpty())
        Spacer(Modifier.size(45.dp))
    else {
        Row(modifier = Modifier.padding(start = 5.dp)) {
            Box(
                modifier = Modifier.shadow(shadowSize, RoundedCornerShape(50))
                    .background(propertyColor.color, RoundedCornerShape(50)).size(40.dp)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    property.first().toString(),
                    color = propertyColor.textColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AutoCompleteTextField(
    value: MutableState<String>,
    modifier: Modifier,
    autoCompleteMap: Map<String, Int>,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { value }
    var textFileValue by remember { mutableStateOf(TextFieldValue(text)) }
    val list = autoCompleteMap.toList().sortedByDescending { it.second }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            value = textFileValue,
            label = { Text(label) },
            modifier = modifier,
            onValueChange = {
                textFileValue = it
                text = it.text
                expanded = true
            },
            singleLine = true
        )
        DropdownMenu(
            expanded,
            { expanded = false },
            properties = PopupProperties(focusable = false)
        ) {
            list.filter {
                it.first.startsWith(
                    textFileValue.text,
                    ignoreCase = true
                ) && it.first.isNotEmpty()
            }.take(3).forEach {
                DropdownMenuItem(onClick = {
                    text = it.first
                    textFileValue = TextFieldValue(text, selection = TextRange(text.length))
                    expanded = false
                }) {
                    BigProperty(it.first)
                }
            }
        }
    }
}