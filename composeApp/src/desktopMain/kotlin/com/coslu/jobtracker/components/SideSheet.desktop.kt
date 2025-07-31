package com.coslu.jobtracker.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun SideSheet(
    showSideSheet: MutableTransitionState<Boolean>,
    modifier: Modifier,
    arrangeToEnd: Boolean,
    showDialog: MutableTransitionState<Boolean>?,
    content: @Composable (() -> Unit)
) = CommonSideSheet(showSideSheet, modifier, arrangeToEnd, showDialog, content)