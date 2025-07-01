package com.coslu.jobtracker.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.coslu.jobtracker.Job
import com.coslu.jobtracker.Settings.applyFilters
import com.coslu.jobtracker.Settings.locationFilters
import com.coslu.jobtracker.Settings.sortingMethod
import com.coslu.jobtracker.Settings.statusFilters
import com.coslu.jobtracker.Settings.typeFilters
import com.coslu.jobtracker.SortingMethod
import com.coslu.jobtracker.colors
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.arrow_right
import job_tracker.composeapp.generated.resources.date
import job_tracker.composeapp.generated.resources.filter
import job_tracker.composeapp.generated.resources.location
import job_tracker.composeapp.generated.resources.name
import job_tracker.composeapp.generated.resources.sort
import job_tracker.composeapp.generated.resources.status
import job_tracker.composeapp.generated.resources.type
import org.jetbrains.compose.resources.painterResource

@Composable
fun FilterControl(item: String, filterMap: MutableMap<String, Boolean>) {
    Row {
        val interactionSource = remember { MutableInteractionSource() }
        Checkbox(
            filterMap[item] ?: true,
            modifier = Modifier.pointerInput(null) {
                detectTapGestures(
                    onPress = {
                        filterMap[item] = !filterMap[item]!!
                        applyFilters()
                        val press = PressInteraction.Press(it)
                        interactionSource.emit(press)
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    },
                    onDoubleTap = {
                        filterMap.keys.forEach {
                            filterMap[it] = false
                        }
                        filterMap[item] = true
                        applyFilters()
                    }
                )
            }.minimumInteractiveComponentSize()
                .hoverable(interactionSource)
                .indication(interactionSource, ripple(bounded = false, radius = 24.dp)),
            onCheckedChange = null
        )
        BigProperty(item.ifBlank { "-" })
    }
}

@Composable
fun SortAndFilter() {
    var openLocations by remember { mutableStateOf(false) }
    var openTypes by remember { mutableStateOf(false) }
    var openStatuses by remember { mutableStateOf(false) }
    LazyVerticalGrid(GridCells.Adaptive(180.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                Modifier.padding(horizontal = 10.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(Res.drawable.sort),
                    null,
                    tint = colors.primary
                )
                Text("Sort", Modifier.padding(horizontal = 10.dp), color = colors.primary)
                Divider(
                    Modifier.padding(horizontal = 10.dp),
                    color = colors.primary,
                    thickness = 1.5.dp
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    sortingMethod.descending,
                    onCheckedChange = {
                        sortingMethod = when (sortingMethod) {
                            is SortingMethod.Date -> SortingMethod.Date(it)
                            is SortingMethod.Name -> SortingMethod.Name(it)
                            is SortingMethod.Type -> SortingMethod.Type(it)
                            is SortingMethod.Location -> SortingMethod.Location(it)
                            is SortingMethod.Status -> SortingMethod.Status(it)
                        }
                    }
                )
                Text("Descending")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    sortingMethod is SortingMethod.Date,
                    { sortingMethod = SortingMethod.Date(true) }
                )
                Icon(
                    painterResource(Res.drawable.date),
                    null,
                    Modifier.padding(end = 10.dp)
                )
                Text("Date")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    sortingMethod is SortingMethod.Name,
                    { sortingMethod = SortingMethod.Name(false) }
                )
                Icon(
                    painterResource(Res.drawable.name),
                    null,
                    Modifier.padding(end = 10.dp)
                )
                Text("Name")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    sortingMethod is SortingMethod.Type,
                    { sortingMethod = SortingMethod.Type(false) }
                )
                Icon(
                    painterResource(Res.drawable.type),
                    null,
                    Modifier.padding(end = 10.dp),
                )
                Text("Type")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    sortingMethod is SortingMethod.Location,
                    {
                        sortingMethod =
                            SortingMethod.Location(false)
                    }
                )
                Icon(
                    painterResource(Res.drawable.location),
                    null,
                    Modifier.padding(end = 10.dp)
                )
                Text("Location")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    sortingMethod is SortingMethod.Status,
                    {
                        sortingMethod =
                            SortingMethod.Status(false)
                    }
                )
                Icon(
                    painterResource(Res.drawable.status),
                    null,
                    Modifier.padding(end = 10.dp)
                )
                Text("Status")
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                Modifier.padding(horizontal = 10.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(Res.drawable.filter),
                    null,
                    tint = colors.primary
                )
                Text("Filter", Modifier.padding(horizontal = 10.dp), color = colors.primary)
                Divider(
                    Modifier.padding(horizontal = 10.dp),
                    color = colors.primary,
                    thickness = 1.5.dp
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                Modifier.fillMaxWidth().clickable(interactionSource = null, indication = null) {
                    openTypes = !openTypes
                }.pointerHoverIcon(PointerIcon.Hand).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    openTypes,
                    transitionSpec = { fadeIn().togetherWith(fadeOut()) }) {
                    if (openTypes)
                        Icon(Icons.Filled.ArrowDropDown, null)
                    else
                        Icon(painterResource(Res.drawable.arrow_right), null)
                }
                Checkbox(
                    typeFilters.all { it.value },
                    onCheckedChange = { checked ->
                        typeFilters.keys.forEach { typeFilters[it] = checked }
                        applyFilters()
                    }
                )
                Text("Type")
            }
        }
        items(Job.types.keys.toList().sorted()) { item ->
            AnimatedVisibility(openTypes) {
                FilterControl(item, typeFilters)
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                Modifier.fillMaxWidth().clickable(interactionSource = null, indication = null) {
                    openLocations = !openLocations
                }.pointerHoverIcon(PointerIcon.Hand).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    openLocations,
                    transitionSpec = { fadeIn().togetherWith(fadeOut()) }) {
                    if (openLocations)
                        Icon(Icons.Filled.ArrowDropDown, null)
                    else
                        Icon(painterResource(Res.drawable.arrow_right), null)
                }
                Checkbox(
                    locationFilters.all { it.value },
                    onCheckedChange = { checked ->
                        locationFilters.keys.forEach { locationFilters[it] = checked }
                        applyFilters()
                    }
                )
                Text("Location")
            }
        }
        items(Job.locations.keys.toList().sorted()) { item ->
            AnimatedVisibility(openLocations) {
                FilterControl(item, locationFilters)
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                Modifier.fillMaxWidth().clickable(interactionSource = null, indication = null) {
                    openStatuses = !openStatuses
                }.pointerHoverIcon(PointerIcon.Hand).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    openStatuses,
                    transitionSpec = { fadeIn().togetherWith(fadeOut()) }) {
                    if (openStatuses)
                        Icon(Icons.Filled.ArrowDropDown, null)
                    else
                        Icon(painterResource(Res.drawable.arrow_right), null)
                }
                Checkbox(
                    statusFilters.all { it.value },
                    onCheckedChange = { checked ->
                        statusFilters.keys.forEach { statusFilters[it] = checked }
                        applyFilters()
                    }
                )
                Text("Status")
            }
        }
        items(Job.statuses) { item ->
            AnimatedVisibility(openStatuses) {
                FilterControl(item, statusFilters)
            }
        }
    }
}