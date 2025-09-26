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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.coslu.jobtracker.Settings.sortingMethod
import com.coslu.jobtracker.components.BottomBar
import com.coslu.jobtracker.components.BottomBarAction
import com.coslu.jobtracker.components.JobDialog
import com.coslu.jobtracker.components.JobName
import com.coslu.jobtracker.components.JobProperty
import com.coslu.jobtracker.components.PopupBubble
import com.coslu.jobtracker.components.SettingsNavHost
import com.coslu.jobtracker.components.SideSheet
import com.coslu.jobtracker.components.SortAndFilter
import com.coslu.jobtracker.components.TooltipButton
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.add
import job_tracker.composeapp.generated.resources.add_job
import job_tracker.composeapp.generated.resources.edit
import job_tracker.composeapp.generated.resources.logo
import job_tracker.composeapp.generated.resources.notes
import job_tracker.composeapp.generated.resources.settings
import job_tracker.composeapp.generated.resources.show_notes
import job_tracker.composeapp.generated.resources.sort_filter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

lateinit var jobs: SnapshotStateList<Job> // separate list for lazy column allows delete animations

private lateinit var snackbarHostState: SnackbarHostState
private lateinit var coroutineScope: CoroutineScope
private lateinit var listState: LazyListState
private lateinit var propertyColors: SnapshotStateMap<String, PropertyColor>

@Suppress("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun App() {
    TrajanTheme {
        jobs = remember {
            Job.list.sortedWith(sortingMethod.comparator).toMutableStateList()
        }
        LaunchedEffect(Unit) { fetchSettings(); Settings.applyFilters() }
        propertyColors = remember { fetchPropertyColors().toMutableStateMap() }
        val showJobDialog = remember { MutableTransitionState(false) }
        val showFilters = remember { MutableTransitionState(false) }
        val showSettings = remember { MutableTransitionState(false) }
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
                                            TooltipButton(
                                                description = stringResource(Res.string.show_notes),
                                                onClick = {
                                                    showNotes.targetState = !showNotes.currentState
                                                },
                                            ) {
                                                PopupBubble(
                                                    dpOffset = DpOffset(34.dp, 20.dp),
                                                    visible = showNotes,
                                                    text = it.notes,
                                                )
                                                Icon(
                                                    painterResource(Res.drawable.notes),
                                                    stringResource(Res.string.show_notes),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                    TooltipButton(
                                        description = stringResource(Res.string.edit),
                                        onClick = {
                                            selectedJob = it
                                            showJobDialog.targetState = true
                                        }
                                    ) {
                                        Icon(
                                            painterResource(Res.drawable.edit),
                                            stringResource(Res.string.show_notes)
                                        )
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
                BottomBar(
                    actions = arrayOf(
                        BottomBarAction(
                            stringResource(Res.string.settings),
                            Res.drawable.settings
                        ) {
                            showSettings.targetState = true
                        },
                        BottomBarAction(
                            stringResource(Res.string.sort_filter),
                            Res.drawable.sort_filter
                        ) {
                            showFilters.targetState = true
                        },
                        BottomBarAction(stringResource(Res.string.add_job), Res.drawable.add) {
                            selectedJob = null
                            showJobDialog.targetState = true
                        }
                    )
                )
            }
            val settingsNavController = rememberNavController()
            SideSheet(
                showSideSheet = showSettings,
                modifier = Modifier.padding(contentPadding),
                navController = settingsNavController
            ) {
                SettingsNavHost(settingsNavController)
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
fun Dp.toInt() = LocalDensity.current.run { roundToPx() }