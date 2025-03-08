package com.coslu.jobtracker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

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
                }
        )
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
                            IconButton(
                                onClick = { showSideSheet.targetState = false },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            ) {
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