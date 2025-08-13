package com.coslu.jobtracker.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
actual fun SideSheet(
    showSideSheet: MutableTransitionState<Boolean>,
    modifier: Modifier,
    arrangeToEnd: Boolean,
    showDialog: MutableTransitionState<Boolean>?,
    navController: NavController?,
    content: @Composable (() -> Unit)
) {
    CommonSideSheet(showSideSheet, modifier, arrangeToEnd, showDialog, navController) {
        BackHandler { showSideSheet.targetState = false }
        content()
    }
}