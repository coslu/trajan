package com.coslu.jobtracker.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.coslu.jobtracker.PropertyColor
import com.coslu.jobtracker.colors
import com.coslu.jobtracker.getPropertyColor
import com.coslu.jobtracker.setPropertyColor
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.transparent
import org.jetbrains.compose.resources.painterResource

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun JobProperty(
    property: String,
    modifier: Modifier,
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val showFullName = remember { MutableTransitionState(false) }
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
                                .pointerHoverIcon(PointerIcon.Hand)
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
        val propertyColor = remember { getPropertyColor(property) }
        PopupBubble(
            dpOffset = DpOffset(25.dp, (-35).dp),
            visible = showFullName,
            text = property,
            backgroundColor = if (propertyColor != PropertyColor.Transparent) propertyColor.color else colors.surface,
            textColor = propertyColor.textColor
        )
        // This uses the BoxWithConstraints scope
        if (maxWidth < 120.dp) {
            SmallProperty(
                property,
                onClick = { showColorPicker = true },
                onLongClick = { showFullName.targetState = true }
            )
        } else if (property.isNotEmpty()) {
            BigProperty(
                property,
                clickable = true,
                onClick = { showColorPicker = true },
                onLongClick = { showFullName.targetState = true }
            )
        }
    }
}

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