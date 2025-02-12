package com.coslu.jobtracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.baseline_comment_24
import job_tracker.composeapp.generated.resources.logo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


val colors = Colors(
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

lateinit var jobs: SnapshotStateList<Job> // separate list for lazy column allows delete animations
private lateinit var snackbarHostState: SnackbarHostState
private lateinit var coroutineScope: CoroutineScope
private lateinit var listState: LazyListState
private lateinit var propertyColors: SnapshotStateMap<String, PropertyColor>

@Composable
@Preview
fun App() {
    MaterialTheme(
        colors = colors
    ) {
        jobs = remember { fetchJobList(); Job.list.toMutableStateList() }
        propertyColors = remember { fetchPropertyColors().toMutableStateMap() }
        var showDialog by remember { mutableStateOf(false) }
        var selectedJob by remember { mutableStateOf<Job?>(null) }
        coroutineScope = rememberCoroutineScope()
        snackbarHostState = remember { SnackbarHostState() }
        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
            AnimatedVisibility(
                !jobs.any { it.visible.targetState },
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (showDialog)
                    JobDialog(onDismissRequest = { showDialog = false }, selectedJob)

                listState = rememberLazyListState()
                LazyColumn(modifier = Modifier.weight(1f), state = listState) {
                    item {
                        Box(modifier = Modifier.height(1.dp))
                    }
                    items(jobs) {
                        var showNotes by remember { mutableStateOf(false) }
                        AnimatedVisibility(
                            it.visible,
                            enter = expandIn(),
                            exit = shrinkOut()
                        ) {
                            Row(
                                modifier = Modifier.fillParentMaxWidth().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BoxWithConstraints {
                                        val smallWindow = maxWidth < 500.dp
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val nameModifier =
                                                if (smallWindow) Modifier.width(150.dp)
                                                else Modifier.weight(0.3f)
                                            JobName(it.name, it.url, nameModifier)
                                            Row(
                                                Modifier.weight(0.7f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                JobProperty(it.type, Modifier.weight(1f, false))
                                                JobProperty(
                                                    it.location,
                                                    Modifier.weight(1f, false)
                                                )
                                                JobProperty(
                                                    it.status,
                                                    Modifier.weight(1f, false)
                                                )
                                                if (it.notes.isNotEmpty()) {
                                                    IconButton({ showNotes = true }) {
                                                        if (showNotes) {
                                                            Popup(
                                                                onDismissRequest = {
                                                                    showNotes = false
                                                                },
                                                                offset = IntOffset(
                                                                    0,
                                                                    28.dp.toInt()
                                                                )
                                                            ) {
                                                                Card(
                                                                    Modifier.padding(10.dp),
                                                                    elevation = 8.dp,
                                                                    shape = RoundedCornerShape(
                                                                        0,
                                                                        20,
                                                                        20,
                                                                        20
                                                                    ),
                                                                    border = BorderStroke(
                                                                        1.dp,
                                                                        color = colors.onSurface
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        it.notes,
                                                                        Modifier.padding(10.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        Icon(
                                                            painterResource(Res.drawable.baseline_comment_24),
                                                            "Comment",
                                                            tint = colors.primary
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                                IconButton(
                                    onClick = {
                                        selectedJob = it
                                        showDialog = true
                                    },
                                ) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Edit",
                                        tint = colors.primary
                                    )
                                }
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier.wrapContentHeight().padding(top = 10.dp, bottom = 10.dp),
                    onClick = {
                        selectedJob = null
                        showDialog = true
                    }
                ) {
                    Icon(Icons.Filled.Add, null)
                    Text("Add Job", modifier = Modifier.padding(start = 10.dp))
                }
            }
        }
    }
}

fun showSnackbar(message: String) =
    coroutineScope.launch { snackbarHostState.showSnackbar(message) }

fun jumpToTop() = coroutineScope.launch { listState.scrollToItem(0) }

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
