package com.coslu.jobtracker.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.coslu.jobtracker.toInt

@Composable
fun PopupBubble(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopStart,
    dpOffset: DpOffset,
    visible: MutableTransitionState<Boolean>,
    text: String,
    tail: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Popup(
        offset = IntOffset(dpOffset.x.toInt(), dpOffset.y.toInt()),
        alignment = alignment,
        onDismissRequest = { visible.targetState = false },
    ) {
        AnimatedVisibility(visible, enter = fadeIn(), exit = fadeOut()) {
            Card(
                modifier = modifier.padding(5.dp),
                shape = if (tail) {
                    when (alignment) {
                        Alignment.TopStart -> RoundedCornerShape(0, 20, 20, 20)
                        Alignment.TopEnd -> RoundedCornerShape(20, 0, 20, 20)
                        Alignment.BottomStart -> RoundedCornerShape(20, 20, 20, 0)
                        Alignment.BottomEnd -> RoundedCornerShape(20, 20, 0, 20)
                        else -> RoundedCornerShape(20)
                    }
                } else RoundedCornerShape(20),
                colors = CardDefaults.cardColors().copy(containerColor = backgroundColor),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                Text(
                    text,
                    color = textColor,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}