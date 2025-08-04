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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.arrow_back
import org.jetbrains.compose.resources.painterResource

@Composable
expect fun SideSheet(
    showSideSheet: MutableTransitionState<Boolean>,
    modifier: Modifier = Modifier,
    arrangeToEnd: Boolean = false,
    showDialog: MutableTransitionState<Boolean>? = null,
    content: @Composable () -> Unit
)

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun CommonSideSheet(
    showSideSheet: MutableTransitionState<Boolean>,
    modifier: Modifier = Modifier,
    arrangeToEnd: Boolean = false,
    showDialog: MutableTransitionState<Boolean>? = null,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(showSideSheet, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f))
                .clickable(interactionSource = null, indication = null) {
                    showSideSheet.targetState = false
                }
        )
    }
    Row(
        modifier.fillMaxSize(),
        horizontalArrangement = if (arrangeToEnd) Arrangement.End else Arrangement.Start
    ) {
        AnimatedVisibility(
            showSideSheet,
            enter = slideInHorizontally { if (arrangeToEnd) it else -it },
            exit = slideOutHorizontally { if (arrangeToEnd) it else -it }) {
            BoxWithConstraints {
                Card(
                    modifier = Modifier.fillMaxHeight()
                        .width(if (maxWidth * 0.8f > 600.dp) 600.dp else maxWidth * 0.8f),
                    shape = if (arrangeToEnd) RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp)
                    else RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp)
                ) {
                    Column {
                        Row {
                            IconButton(
                                onClick = { showSideSheet.targetState = false },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            ) {
                                Icon(painterResource(Res.drawable.arrow_back), "Back")
                            }
                        }
                        content()
                    }
                }
            }
        }
    }
    if (showDialog != null) {
        Popup(
            onDismissRequest = { showDialog.targetState = false }
        ) {
            AnimatedVisibility(
                showDialog,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    Modifier.fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = { showDialog.targetState = false })
                )
            }
        }
    }
}