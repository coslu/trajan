package com.coslu.jobtracker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.coslu.jobtracker.Job
import com.coslu.jobtracker.colors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun JobDialog(
    onDismissRequest: () -> Unit,
    job: Job? = null,
) {
    var name by remember { mutableStateOf(job?.name ?: "") }
    var url by remember { mutableStateOf(job?.url ?: "") }
    val location = mutableStateOf(job?.location ?: "")
    val type = mutableStateOf(job?.type ?: "")
    var status by remember { mutableStateOf(job?.status ?: "Pending Application") }
    var notes by remember { mutableStateOf(job?.notes ?: "") }
    val buttonText = if (job != null) "Save" else "Add Job"
    val title = if (job != null) "Edit Job" else "New Job"
    var expandStatusMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val transitionState = remember { MutableTransitionState(false).apply { targetState = true } }
    val closeWithAnimation = { transitionState.targetState = false }
    Dialog(onDismissRequest = closeWithAnimation) {
        LaunchedEffect(transitionState.currentState) {
            if (!transitionState.currentState && !transitionState.targetState) {
                onDismissRequest()
            }
        }
        AnimatedVisibility(
            transitionState,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        buttons = {
                            AlertDialogButtons(
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            job?.remove()
                                            showDeleteDialog = false
                                            onDismissRequest()
                                        },
                                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                    ) {
                                        Text("Delete")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showDeleteDialog = false },
                                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                    ) {
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
                                IconButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                ) {
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
                        AutoCompleteTextField(type, modifier, Job.types, "Type of Work")
                    }
                    item {
                        AutoCompleteTextField(location, modifier, Job.locations, "Location")
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
                                Job.statuses.forEach {
                                    DropdownMenuItem(
                                        modifier = Modifier.padding(5.dp),
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
                        TextField(
                            value = notes,
                            label = { Text("Additional Notes") },
                            modifier = modifier.heightIn(min = TextFieldDefaults.MinHeight * 1.6f),
                            onValueChange = { notes = it },
                            singleLine = false,
                        )
                    }
                    item {
                        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                            Row(modifier = Modifier.weight(0.5f)) {
                                TextButton(
                                    onClick = {
                                        closeWithAnimation()
                                    },
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
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
                                            job.edit(
                                                name,
                                                url,
                                                type.value,
                                                location.value,
                                                status,
                                                notes
                                            )
                                            closeWithAnimation()
                                        } else {
                                            Job(
                                                name,
                                                url,
                                                type.value,
                                                location.value,
                                                status,
                                                notes
                                            ).add()
                                            onDismissRequest()
                                        }
                                    },
                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
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
}

@Composable
fun AlertDialogButtons(
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(0.5f)) {
            dismissButton()
        }
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.End
        ) {
            confirmButton()
        }
    }
}