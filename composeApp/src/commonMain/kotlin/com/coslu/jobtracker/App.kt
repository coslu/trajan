package com.coslu.jobtracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.coslu.jobtracker.Settings.sortingMethod
import com.coslu.jobtracker.components.JobDialog
import com.coslu.jobtracker.components.JobName
import com.coslu.jobtracker.components.JobProperty
import com.coslu.jobtracker.components.PopupBubble
import com.coslu.jobtracker.components.SideSheet
import com.coslu.jobtracker.components.SortAndFilter
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.add
import job_tracker.composeapp.generated.resources.edit
import job_tracker.composeapp.generated.resources.logo
import job_tracker.composeapp.generated.resources.notes
import job_tracker.composeapp.generated.resources.sort_filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


/*val colors = Colors(
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
)*/

lateinit var jobs: SnapshotStateList<Job> // separate list for lazy column allows delete animations

private lateinit var snackbarHostState: SnackbarHostState
private lateinit var coroutineScope: CoroutineScope
private lateinit var listState: LazyListState
private lateinit var propertyColors: SnapshotStateMap<String, PropertyColor>

@OptIn(ExperimentalTime::class)
@Composable
@Preview
fun App() {
    TrajanTheme {
        jobs = remember {
            Job.list.sortedWith(sortingMethod.comparator).toMutableStateList()
        }
        LaunchedEffect(Unit) { fetchSettings(); Settings.applyFilters() }
        propertyColors = remember { fetchPropertyColors().toMutableStateMap() }
        val showJobDialog = remember { MutableTransitionState(false) }
        val showFilters = remember { MutableTransitionState(false) }
        var selectedJob by remember { mutableStateOf<Job?>(null) }
        coroutineScope = rememberCoroutineScope()
        snackbarHostState = remember { SnackbarHostState() }
        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { contentPadding ->
            AnimatedVisibility(
                !jobs.any { it.visible.targetState },
                Modifier.padding(contentPadding),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painterResource(Res.drawable.logo),
                        null,
                        modifier = Modifier.padding(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = 40.dp
                        ),
                        alpha = 0.4f
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                listState = rememberLazyListState()
                LazyColumn(modifier = Modifier.weight(1f), state = listState) {
                    item {
                        Box(modifier = Modifier.height(1.dp))
                    }
                    items(jobs) {
                        val showNotes =
                            remember { MutableTransitionState(false).apply { targetState = false } }
                        AnimatedVisibility(
                            it.visible,
                            enter = expandIn(),
                            exit = shrinkOut(),
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    JobName(it.name, it.url, Modifier.weight(1f))
                                    Text(
                                        Instant.fromEpochMilliseconds(it.date)
                                            .toLocalDateTime(TimeZone.currentSystemDefault())
                                            .format(
                                                LocalDateTime.Format {
                                                    day()
                                                    char('.')
                                                    monthNumber()
                                                    char('.')
                                                    year()
                                                }
                                            ),
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                                Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
                                    Row(Modifier.weight(1f).padding(start = 40.dp)) {
                                        JobProperty(it.type, Modifier.weight(1f, false))
                                        JobProperty(it.location, Modifier.weight(1f, false))
                                        JobProperty(it.status, Modifier.weight(1f, false))
                                        if (it.notes.isNotEmpty()) {
                                            IconButton(
                                                onClick = {
                                                    showNotes.targetState =
                                                        !showNotes.currentState
                                                },
                                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                            ) {
                                                PopupBubble(
                                                    dpOffset = DpOffset(34.dp, 20.dp),
                                                    visible = showNotes,
                                                    text = it.notes,
                                                )
                                                Icon(
                                                    painterResource(Res.drawable.notes),
                                                    "Show additional notes",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            selectedJob = it
                                            showJobDialog.targetState = true
                                        },
                                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                    ) {
                                        Icon(
                                            painterResource(Res.drawable.edit),
                                            contentDescription = "Edit",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
                Row(Modifier.padding(vertical = 10.dp)) {
                    if (Job.list.isNotEmpty()) {
                        Button(
                            onClick = { showFilters.targetState = true },
                            modifier = Modifier.padding(end = 20.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                        ) {
                            Icon(painterResource(Res.drawable.sort_filter), null)
                            Text("Sort & Filter", modifier = Modifier.padding(start = 10.dp))
                        }
                    }
                    Button(
                        onClick = { selectedJob = null; showJobDialog.targetState = true },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(painterResource(Res.drawable.add), null)
                        Text("Add Job", modifier = Modifier.padding(start = 10.dp))
                    }
                }
            }
            SideSheet(showFilters, Modifier.padding(contentPadding)) {
                SortAndFilter()
            }
            val showDeleteDialog = remember { MutableTransitionState(false) }
            SideSheet(showJobDialog, Modifier.padding(contentPadding), true, showDeleteDialog) {
                JobDialog(
                    onDismissRequest = { showJobDialog.targetState = false },
                    selectedJob,
                    showDeleteDialog
                )
            }
        }
    }
}

fun showSnackbar(message: String) =
    coroutineScope.launch { snackbarHostState.showSnackbar(message) }

fun jumpToItem(index: Int) = coroutineScope.launch { listState.scrollToItem(index) }

fun getPropertyColor(
    property: String,
    default: PropertyColor = PropertyColor.Transparent
): PropertyColor {
    return propertyColors[property] ?: default
}

fun setPropertyColor(property: String, color: PropertyColor) {
    propertyColors[property] = color
    savePropertyColors(propertyColors.toList())
}

@Composable
fun Dp.toInt(): Int {
    return LocalDensity.current.run { roundToPx() }
}