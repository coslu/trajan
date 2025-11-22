package com.coslu.jobtracker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.coslu.jobtracker.Job
import com.coslu.jobtracker.toInt
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.actualize_date
import job_tracker.composeapp.generated.resources.actualize_date_description
import job_tracker.composeapp.generated.resources.add_job
import job_tracker.composeapp.generated.resources.additional_notes
import job_tracker.composeapp.generated.resources.application_status
import job_tracker.composeapp.generated.resources.arrow_dropdown_open
import job_tracker.composeapp.generated.resources.cancel
import job_tracker.composeapp.generated.resources.company_name
import job_tracker.composeapp.generated.resources.confirm_delete
import job_tracker.composeapp.generated.resources.delete
import job_tracker.composeapp.generated.resources.edit_job
import job_tracker.composeapp.generated.resources.help
import job_tracker.composeapp.generated.resources.location
import job_tracker.composeapp.generated.resources.new_job
import job_tracker.composeapp.generated.resources.pending_application
import job_tracker.composeapp.generated.resources.save
import job_tracker.composeapp.generated.resources.type_of_work
import job_tracker.composeapp.generated.resources.url_of_job_posting
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi

@Suppress("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun JobDialog(
    onDismissRequest: () -> Unit,
    job: Job? = null,
    showDeleteDialog: MutableTransitionState<Boolean>? = null
) {
    var name by remember { mutableStateOf(job?.name ?: "") }
    var url by remember { mutableStateOf(job?.url ?: "") }
    val location = remember { mutableStateOf(job?.location ?: "") }
    val type = remember { mutableStateOf(job?.type ?: "") }
    val defaultStatus = stringResource(Res.string.pending_application)
    var status by remember { mutableStateOf(job?.status ?: defaultStatus) }
    var notes by remember { mutableStateOf(job?.notes ?: "") }
    val buttonText =
        if (job != null) stringResource(Res.string.save) else stringResource(Res.string.add_job)
    val title =
        if (job != null) stringResource(Res.string.edit_job) else stringResource(Res.string.new_job)
    var expandStatusMenu by remember { mutableStateOf(false) }
    var actualizeDate by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            val modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).fillMaxWidth(0.8f)
                .minimumInteractiveComponentSize()
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
                    Text(
                        title,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (job != null) {
                        showDeleteDialog as MutableTransitionState<Boolean>
                        TooltipButton(
                            description = stringResource(Res.string.delete),
                            onClick = { showDeleteDialog.targetState = true },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Popup(
                                onDismissRequest = { showDeleteDialog.targetState = false },
                                offset = IntOffset(-384.dp.toInt(), 24.dp.toInt()),
                            ) {
                                AnimatedVisibility(
                                    showDeleteDialog,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Card(
                                        modifier = Modifier.padding(10.dp),
                                        shape = RoundedCornerShape(
                                            20,
                                            0,
                                            20,
                                            20
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        Column(Modifier.padding(20.dp)) {
                                            Text(
                                                stringResource(Res.string.confirm_delete),
                                                Modifier.padding(10.dp)
                                            )
                                            Row(Modifier.width(340.dp)) {
                                                Row(Modifier.weight(1f)) {
                                                    TextButton(
                                                        onClick = {
                                                            showDeleteDialog.targetState = false
                                                        },
                                                        modifier = Modifier.pointerHoverIcon(
                                                            PointerIcon.Hand
                                                        )
                                                    ) {
                                                        Text(stringResource(Res.string.cancel))
                                                    }
                                                }
                                                TextButton(
                                                    onClick = {
                                                        job.remove()
                                                        showDeleteDialog.targetState = false
                                                        onDismissRequest()
                                                    },
                                                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                                ) {
                                                    Text(stringResource(Res.string.delete))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Icon(
                                painterResource(Res.drawable.delete),
                                stringResource(Res.string.delete),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            item {
                TextField(
                    value = name,
                    label = { Text(stringResource(Res.string.company_name)) },
                    modifier = modifier,
                    onValueChange = { name = it },
                    singleLine = true
                )
            }
            item {
                TextField(
                    value = url,
                    label = { Text(stringResource(Res.string.url_of_job_posting)) },
                    modifier = modifier,
                    onValueChange = { url = it },
                    singleLine = true
                )
            }
            item {
                AutoCompleteTextField(
                    type,
                    modifier,
                    Job.types,
                    stringResource(Res.string.type_of_work)
                )
            }
            item {
                AutoCompleteTextField(
                    location,
                    modifier,
                    Job.locations,
                    stringResource(Res.string.location)
                )
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = expandStatusMenu,
                    onExpandedChange = { expandStatusMenu = !expandStatusMenu },
                ) {
                    TextField(
                        value = Job.localizeStatus(status),
                        label = { Text(stringResource(Res.string.application_status)) },
                        readOnly = true,
                        modifier = modifier.menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ).pointerHoverIcon(icon = PointerIcon.Hand, overrideDescendants = true),
                        onValueChange = {},
                        trailingIcon = {
                            Icon(painterResource(Res.drawable.arrow_dropdown_open), null)
                        }
                    )
                    ExposedDropdownMenu(expandStatusMenu, { expandStatusMenu = false }) {
                        Job.statuses.forEach {
                            DropdownMenuItem(
                                modifier = Modifier.padding(5.dp),
                                onClick = {
                                    status = it
                                    expandStatusMenu = false
                                },
                                text = { BigProperty(it) }
                            )
                        }
                    }
                }
            }
            item {
                TextField(
                    value = notes,
                    label = { Text(stringResource(Res.string.additional_notes)) },
                    modifier = modifier.heightIn(min = TextFieldDefaults.MinHeight * 1.6f),
                    onValueChange = { notes = it },
                    singleLine = false,
                )
            }
            if (job != null) {
                item {
                    val showActualizeDateHelp = remember { MutableTransitionState(false) }
                    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = actualizeDate,
                            onCheckedChange = { actualizeDate = it },
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        )
                        Text(
                            stringResource(Res.string.actualize_date),
                            Modifier.padding(end = 5.dp)
                        )
                        BoxWithConstraints {
                            TooltipButton(
                                description = stringResource(Res.string.help),
                                onClick = {
                                    showActualizeDateHelp.targetState = true
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    painterResource(Res.drawable.help),
                                    stringResource(Res.string.help),
                                    tint = LocalContentColor.current.copy(alpha = 0.5f)
                                )
                                PopupBubble(
                                    modifier = Modifier.width(300.dp),
                                    alignment = Alignment.BottomStart,
                                    dpOffset = DpOffset(
                                        20.dp,
                                        if (maxWidth > 290.dp) (-5).dp else (-25).dp
                                    ),
                                    visible = showActualizeDateHelp,
                                    text = stringResource(Res.string.actualize_date_description),
                                    tail = maxWidth > 290.dp
                                )
                            }
                        }
                    }
                }
            }
            item {
                Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                    Row(modifier = Modifier.weight(0.5f)) {
                        TextButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Text(stringResource(Res.string.cancel))
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
                                        name = name.trim(),
                                        url = url.trim(),
                                        type = type.value.trim(),
                                        location = location.value.trim(),
                                        status = status.trim(),
                                        notes = notes.trim(),
                                        actualizeDate = actualizeDate
                                    )
                                } else {
                                    Job(
                                        name = name.trim(),
                                        url = url.trim(),
                                        type = type.value.trim(),
                                        location = location.value.trim(),
                                        status = status.trim(),
                                        notes = notes
                                    ).add()
                                }
                                onDismissRequest()
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