package com.coslu.jobtracker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.coslu.jobtracker.Settings.applyFilters
import com.coslu.jobtracker.Settings.locationFilters
import com.coslu.jobtracker.Settings.sortingMethod
import com.coslu.jobtracker.Settings.statusFilters
import com.coslu.jobtracker.Settings.typeFilters
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.arrow_right
import job_tracker.composeapp.generated.resources.date
import job_tracker.composeapp.generated.resources.filter
import job_tracker.composeapp.generated.resources.location
import job_tracker.composeapp.generated.resources.name
import job_tracker.composeapp.generated.resources.open_link
import job_tracker.composeapp.generated.resources.sort
import job_tracker.composeapp.generated.resources.status
import job_tracker.composeapp.generated.resources.transparent
import job_tracker.composeapp.generated.resources.type
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
    var showFullName by remember { mutableStateOf(false) }
    BoxWithConstraints(modifier = modifier.pointerHoverIcon(PointerIcon.Hand)) {
        DropdownMenu(showColorPicker, { showColorPicker = false }) {
            Column(Modifier.height(150.dp).width(300.dp)) {
                LazyVerticalGrid(GridCells.Fixed(6)) {
                    items(PropertyColor.entries.toTypedArray()) {
                        IconButton(
                            onClick = {
                                setPropertyColor(property, it)
                                showColorPicker = false
                            },
                            modifier = Modifier.padding(5.dp)
                                .background(it.color, shape = CircleShape).size(40.dp)
                        ) {
                            if (it.color.alpha == 0f)
                                Icon(
                                    painterResource(Res.drawable.transparent),
                                    null,
                                    tint = Color.LightGray
                                )
                        }
                    }
                }
            }
        }
        Popup(
            offset = IntOffset(25.dp.toInt(), -40.dp.toInt()),
            onDismissRequest = {
                showFullName = false
            },
        ) {
            val propertyColor = getPropertyColor(property)
            AnimatedVisibility(showFullName, enter = fadeIn(), exit = fadeOut()) {
                Card(
                    shape = RoundedCornerShape(
                        20,
                        20,
                        20,
                        0
                    ),
                    elevation = 5.dp,
                    backgroundColor = if (propertyColor != PropertyColor.Transparent) propertyColor.color else colors.surface,
                    border = BorderStroke(1.dp, propertyColor.textColor)
                ) {
                    Text(
                        property,
                        color = propertyColor.textColor,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
        if (maxWidth < 120.dp) {
            SmallProperty(
                property,
                onClick = { showColorPicker = true },
                onLongClick = { showFullName = true }
            )
        } else if (property.isNotEmpty()) {
            BigProperty(
                property,
                clickable = true,
                onClick = { showColorPicker = true },
                onLongClick = { showFullName = true }
            )
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
                Icon(painterResource(Res.drawable.open_link), "Go to Job")
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
                        showSnackbar("The URL is invalid or cannot be handled by the system.")
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
                                ) {
                                    Text("Dismiss")
                                }
                            }
                            Row(
                                modifier = modifier.weight(0.5f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton({
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
                                }) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BigProperty(
    property: String,
    clickable: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val propertyColor = getPropertyColor(property)
    val shadowSize = if (propertyColor != PropertyColor.Transparent) 5.dp else 0.dp
    Row(modifier = Modifier.padding(5.dp)) {
        var modifier = Modifier.shadow(shadowSize, RoundedCornerShape(30))
            .background(propertyColor.color, shape = RoundedCornerShape(30))
            .wrapContentWidth()
        if (clickable)
            modifier = modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallProperty(property: String, onClick: () -> Unit = {}, onLongClick: () -> Unit = {}) {
    val propertyColor = getPropertyColor(property)
    val shadowSize = if (propertyColor != PropertyColor.Transparent) 5.dp else 0.dp
    if (property.isEmpty())
        Spacer(Modifier.size(45.dp))
    else {
        Row(modifier = Modifier.padding(start = 5.dp)) {
            Box(
                modifier = Modifier.shadow(shadowSize, RoundedCornerShape(50))
                    .background(propertyColor.color, RoundedCornerShape(50)).size(40.dp)
                    .combinedClickable(onClick = onClick, onLongClick = onLongClick),
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
                if (it.text != text)
                    expanded = true
                text = it.text
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
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp),
                    onClick = {
                        text = it.first
                        textFileValue = TextFieldValue(text, selection = TextRange(text.length))
                        expanded = false
                    }
                ) {
                    BigProperty(it.first)
                }
            }
        }
    }
}

@Composable
fun SideSheet(
    showSideSheet: MutableTransitionState<Boolean>,
    arrangeToEnd: Boolean = false,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(showSideSheet, enter = fadeIn(), exit = fadeOut()) {
        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f))
                .clickable(interactionSource = null, indication = null) {
                    showSideSheet.targetState = false
                })
    }
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = if (arrangeToEnd) Arrangement.End else Arrangement.Start
    ) {
        AnimatedVisibility(
            showSideSheet,
            enter = slideInHorizontally { if (arrangeToEnd) it else -it },
            exit = slideOutHorizontally { if (arrangeToEnd) it else -it }) {
            BoxWithConstraints {
                Card(
                    Modifier.fillMaxHeight()
                        .width(if (maxWidth * 0.8f > 600.dp) 600.dp else maxWidth * 0.8f)
                ) {
                    Column {
                        Row {
                            IconButton({ showSideSheet.targetState = false }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                        content()
                    }
                }
            }
        }
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
                    { sortingMethod = SortingMethod.Date(sortingMethod.descending) }
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
                    { sortingMethod = SortingMethod.Name(sortingMethod.descending) }
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
                    { sortingMethod = SortingMethod.Type(sortingMethod.descending) }
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
                            SortingMethod.Location(sortingMethod.descending)
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
                            SortingMethod.Status(sortingMethod.descending)
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
                Text("Types")
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
                Text("Locations")
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