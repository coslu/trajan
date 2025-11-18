package com.coslu.jobtracker.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coslu.jobtracker.Settings
import com.coslu.jobtracker.saveSettings
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.path
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.app_language
import job_tracker.composeapp.generated.resources.arrow_dropdown_open
import job_tracker.composeapp.generated.resources.arrow_enter_right
import job_tracker.composeapp.generated.resources.export
import job_tracker.composeapp.generated.resources.export_jobs
import job_tracker.composeapp.generated.resources.export_settings
import job_tracker.composeapp.generated.resources.export_to_file
import job_tracker.composeapp.generated.resources.import
import job_tracker.composeapp.generated.resources.import_from_file
import job_tracker.composeapp.generated.resources.language
import job_tracker.composeapp.generated.resources.language_settings
import job_tracker.composeapp.generated.resources.search
import job_tracker.composeapp.generated.resources.search_in_locations
import job_tracker.composeapp.generated.resources.search_in_notes
import job_tracker.composeapp.generated.resources.search_in_types
import job_tracker.composeapp.generated.resources.search_settings
import job_tracker.composeapp.generated.resources.settings
import job_tracker.composeapp.generated.resources.synchronization
import job_tracker.composeapp.generated.resources.synchronization_settings
import job_tracker.composeapp.generated.resources.theme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun SettingsNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Menu) {
        composable<Menu> {
            Column {
                TitleText(stringResource(Res.string.settings), Modifier.padding(horizontal = 20.dp))
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
                    leadingIcon = current.icon,
                    trailingIcon = { Icon(painterResource(Res.drawable.arrow_dropdown_open), null) }
                )
                ExposedDropdownMenu(expanded, { expanded = false }) {
                    options.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                current = it
                                saveSettings()
                                expanded = false
                            },
                            leadingIcon = it.icon
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
        modifier = modifier.padding(vertical = 20.dp),
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SearchView(modifier: Modifier) {
    LazyColumn(modifier) {
        item {
            TitleText(
                stringResource(Res.string.search_settings),
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
expect fun ThemeView(modifier: Modifier)

@OptIn(ExperimentalTime::class)
@Composable
fun SynchronizationView(modifier: Modifier) {
    LazyColumn(modifier) {
        item {
            TitleText(
                stringResource(Res.string.synchronization_settings),
            )
        }
        item {
            SwitchSetting(stringResource(Res.string.export_jobs), Settings.exportJobs)
        }
        item {
            SwitchSetting(stringResource(Res.string.export_settings), Settings.exportSettings)
        }
        item {
            val launcher = rememberFileSaverLauncher {
                val filesToZip = mutableListOf<String>().apply {
                    if (Settings.exportJobs.value)
                        addAll(listOf("jobs.json", "colors.json"))
                    if (Settings.exportSettings.value)
                        add("settings.json")
                }
                if (it != null)
                    exportToFile(it.path, filesToZip, "Error exporting file")
            }
            OutlinedButton(
                onClick = {
                    val defaultName = "Trajan-" + Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).format(
                            LocalDateTime.Format {
                                year(); char('-'); monthNumber(); char('-'); day()
                                char('-'); hour(); char('-'); minute(); char('-'); second()
                            }
                        )
                    launcher.launch(defaultName, "zip")
                },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(painterResource(Res.drawable.export), null)
                Text(stringResource(Res.string.export_to_file), Modifier.padding(start = 10.dp))
            }
        }
        item {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    .pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(painterResource(Res.drawable.import), null)
                Text(stringResource(Res.string.import_from_file), Modifier.padding(start = 10.dp))
            }
        }
    }
}

expect fun exportToFile(path: String, filesToZip: List<String>, errorMessage: String)

@Composable
fun LanguageView(modifier: Modifier) {
    LazyColumn(modifier) {
        item {
            TitleText(
                stringResource(Res.string.language_settings)
            )
        }
        item {
            DropdownSetting(
                stringResource(Res.string.app_language),
                Settings.Language.options,
                Settings.Language.current
            )
        }
    }
}

@Serializable
object Menu

private val contentModifier = Modifier.padding(horizontal = 20.dp)

private enum class SettingsCategory(
    val categoryName: @Composable () -> String,
    val drawableRes: DrawableResource,
    val content: @Composable () -> Unit
) {
    SEARCH(
        { stringResource(Res.string.search) },
        Res.drawable.search,
        { SearchView(contentModifier) }),
    THEME({ stringResource(Res.string.theme) }, Res.drawable.theme, { ThemeView(contentModifier) }),
    SYNCHRONIZATION(
        { stringResource(Res.string.synchronization) },
        Res.drawable.synchronization,
        { SynchronizationView(contentModifier) }),
    LANGUAGE(
        { stringResource(Res.string.language) },
        Res.drawable.language,
        { LanguageView(contentModifier) })
}