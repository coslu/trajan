package com.coslu.jobtracker.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coslu.jobtracker.Settings
import com.coslu.jobtracker.saveSettings
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.arrow_dropdown_open
import job_tracker.composeapp.generated.resources.arrow_enter_right
import job_tracker.composeapp.generated.resources.search
import job_tracker.composeapp.generated.resources.search_in_locations
import job_tracker.composeapp.generated.resources.search_in_notes
import job_tracker.composeapp.generated.resources.search_in_types
import job_tracker.composeapp.generated.resources.search_settings
import job_tracker.composeapp.generated.resources.settings
import job_tracker.composeapp.generated.resources.synchronization
import job_tracker.composeapp.generated.resources.synchronization_settings
import job_tracker.composeapp.generated.resources.theme
import job_tracker.composeapp.generated.resources.theme_settings
import job_tracker.composeapp.generated.resources.use_system_colors
import job_tracker.composeapp.generated.resources.use_system_colors_subtitle
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Menu) {
        composable<Menu> {
            Column {
                TitleText(stringResource(Res.string.settings), Modifier.padding(20.dp))
                LazyColumn {
                    items(SettingsCategory.entries) {
                        Row(
                            Modifier.fillMaxWidth().pointerHoverIcon(PointerIcon.Hand)
                                .clickable { navController.navigate(it.name) }.padding(20.dp)
                        ) {
                            Icon(painterResource(it.drawableRes), null)
                            Text(it.categoryName(), Modifier.padding(start = 10.dp).weight(1f))
                            Icon(painterResource(Res.drawable.arrow_enter_right), null)
                        }
                    }
                }
            }
        }
        SettingsCategory.entries.forEach { category ->
            composable(category.name) {
                category.content()
            }
        }
    }
}

@Composable
fun SwitchSetting(title: String, setting: MutableState<Boolean>, subtitle: String? = null) {
    Row(
        Modifier.heightIn(min = 64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title)
            if (subtitle != null)
                Text(subtitle, color = LocalContentColor.current.copy(alpha = 0.6f))
        }
        Switch(
            checked = setting.value,
            onCheckedChange = { setting.value = it; saveSettings() },
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Settings.Option> DropdownSetting(
    text: String,
    options: List<T>,
    setting: MutableState<T>
) {
    var expanded by remember { mutableStateOf(false) }
    var current by setting
    BoxWithConstraints {
        Row(
            Modifier.padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (this@BoxWithConstraints.maxWidth > 500.dp)
                Text(text, Modifier.weight(1f))
            ExposedDropdownMenuBox(expanded, { expanded = it }) {
                TextField(
                    value = current.name,
                    onValueChange = {},
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .pointerHoverIcon(icon = PointerIcon.Hand, overrideDescendants = true),
                    readOnly = true,
                    label = if (this@BoxWithConstraints.maxWidth > 500.dp) null else {
                        @Composable { Text(text) }
                    },
                    leadingIcon = { current.Icon() },
                    trailingIcon = { Icon(painterResource(Res.drawable.arrow_dropdown_open), null) }
                )
                ExposedDropdownMenu(expanded, { expanded = false }) {
                    options.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                current = it
                                expanded = false
                            },
                            leadingIcon = { it.Icon() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SearchView() {
    LazyColumn(Modifier.padding(horizontal = 20.dp)) {
        item {
            TitleText(
                stringResource(Res.string.search_settings),
                Modifier.padding(vertical = 20.dp)
            )
        }
        item { SwitchSetting(stringResource(Res.string.search_in_types), Settings.searchInTypes) }
        item {
            SwitchSetting(
                stringResource(Res.string.search_in_locations),
                Settings.searchInLocations
            )
        }
        item { SwitchSetting(stringResource(Res.string.search_in_notes), Settings.searchInNotes) }
    }
}

@Composable
fun ThemeView() {
    LazyVerticalGrid(GridCells.Adaptive(140.dp), Modifier.padding(horizontal = 20.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            TitleText(stringResource(Res.string.theme_settings), Modifier.padding(vertical = 20.dp))
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            DropdownSetting("Preferred theme:", Settings.Theme.options, Settings.Theme.current)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                SwitchSetting(
                    stringResource(Res.string.use_system_colors),
                    Settings.Color.useSystemColors,
                    stringResource(Res.string.use_system_colors_subtitle)
                )
                Spacer(Modifier.height(20.dp))
            }
        }
        items(Settings.Color.options) {
            val modifier = if (Settings.Color.current.value == it) Modifier.border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10)
            ) else Modifier
            Box(
                modifier.clip(RoundedCornerShape(10))
                    .clickable { Settings.Color.current.value = it }) {
                Column(
                    Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    it.Icon()
                    Spacer(Modifier.height(5.dp))
                    Text(it.name, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun SynchronizationView() {
    LazyColumn(Modifier.padding(horizontal = 20.dp)) {
        item {
            TitleText(
                stringResource(Res.string.synchronization_settings),
                Modifier.padding(vertical = 20.dp)
            )
        }
    }
}

@Serializable
object Menu

private enum class SettingsCategory(
    val categoryName: @Composable () -> String,
    val drawableRes: DrawableResource,
    val content: @Composable () -> Unit
) {
    SEARCH({ stringResource(Res.string.search) }, Res.drawable.search, { SearchView() }),
    THEME({ stringResource(Res.string.theme) }, Res.drawable.theme, { ThemeView() }),
    SYNCHRONIZATION(
        { stringResource(Res.string.synchronization) },
        Res.drawable.synchronization,
        { SynchronizationView() })
}