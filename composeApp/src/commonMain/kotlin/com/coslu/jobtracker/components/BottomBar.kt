package com.coslu.jobtracker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.coslu.jobtracker.Settings
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.add_job
import job_tracker.composeapp.generated.resources.clear
import job_tracker.composeapp.generated.resources.search
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

data class BottomBarAction(
    val stringRes: StringResource,
    val drawableRes: DrawableResource,
    val onClick: () -> Unit
)

@Composable
private fun BigBottomBar(actions: List<BottomBarAction>) {
    actions.forEachIndexed { index, action ->
        if (index == actions.size - 1) {
            // Last action is to the right of search bar with filled button
            SearchBar(Modifier.width(600.dp))
            Button(
                onClick = action.onClick,
                modifier = Modifier.padding(horizontal = 5.dp).pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(painterResource(action.drawableRes), null)
                Text(stringResource(Res.string.add_job), Modifier.padding(start = 10.dp))
            }
        } else {
            OutlinedButton(
                onClick = action.onClick,
                modifier = Modifier.padding(horizontal = 5.dp).pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(painterResource(action.drawableRes), null)
                Text(stringResource(action.stringRes), Modifier.padding(start = 10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmallBottomBar(actions: List<BottomBarAction>, maxWidth: Dp) {
    actions.forEachIndexed { index, action ->
        if (index == actions.size - 1) {
            // Last action is to the right of search bar with filled button
            SearchBar(Modifier.width(maxWidth * 0.5f))
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above,
                        5.dp
                    ),
                tooltip = { PlainTooltip { Text(stringResource(action.stringRes)) } },
                state = rememberTooltipState()
            ) {
                FloatingActionButton(
                    onClick = action.onClick,
                    modifier = Modifier.padding(5.dp).pointerHoverIcon(PointerIcon.Hand),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Icon(painterResource(action.drawableRes), stringResource(action.stringRes))
                }
            }
        } else {
            TooltipButton(stringResource(action.stringRes), action.onClick) {
                Icon(painterResource(action.drawableRes), stringResource(action.stringRes))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    var searchString by Settings.searchString
    TextField(
        modifier = modifier.padding(vertical = 2.dp, horizontal = 5.dp).height(56.dp),
        value = searchString,
        onValueChange = { value ->
            searchString = value
            Settings.applyFilters()
        },
        singleLine = true,
        leadingIcon = { Icon(painterResource(Res.drawable.search), null) },
        trailingIcon = {
            if (searchString.isNotEmpty()) {
                TooltipButton(
                    description = stringResource(Res.string.clear),
                    onClick = {
                        searchString = ""
                        Settings.applyFilters()
                    }
                ) {
                    Icon(painterResource(Res.drawable.clear), stringResource(Res.string.clear))
                }
            }
        },
        placeholder = { Text(stringResource(Res.string.search)) },
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun BottomBar(actions: List<BottomBarAction>) {
    BoxWithConstraints {
        val maxWidth = maxWidth
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (maxWidth >= 1200.dp)
                BigBottomBar(actions)
            else
                SmallBottomBar(actions, maxWidth)
        }
    }
}