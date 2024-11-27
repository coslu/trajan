package com.coslu.jobtracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
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

@Composable
@Preview
fun App() {
    MaterialTheme(
        colors = colors
    ) {
        val list = remember { fetchJobList().toMutableStateList() }
        var showDialog by remember { mutableStateOf(false) }
        var selectedJob by remember { mutableStateOf<Job?>(null) }
        Scaffold {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (showDialog) {
                    JobDialog(onDismissRequest = { showDialog = false }, list, selectedJob)
                }
                LazyColumn(modifier = Modifier.weight(1f)) {
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
                                JobName(it.name, it.url, Modifier.width(180.dp))
                                JobProperty(it.type, Modifier.weight(1f, false), Color.Gray)
                                JobProperty(it.location, Modifier.weight(1f, false))
                                JobProperty(
                                    it.status.toString(),
                                    Modifier.weight(1f, false),
                                    Color.Green
                                )
                            }
                            IconButton(
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
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
