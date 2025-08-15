package com.coslu.jobtracker.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.arrow_enter_right
import job_tracker.composeapp.generated.resources.search
import job_tracker.composeapp.generated.resources.synchronization
import job_tracker.composeapp.generated.resources.theme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Menu) {
        composable<Menu> {
            Column {
                Text(
                    "Settings",
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                LazyColumn {
                    items(SettingsCategory.entries) {
                        Row(
                            Modifier.fillMaxWidth().pointerHoverIcon(PointerIcon.Hand)
                                .clickable { navController.navigate(it.name) }.padding(20.dp)
                        ) {
                            Icon(painterResource(it.drawableRes), null)
                            Text(it.categoryName, Modifier.padding(start = 10.dp).weight(1f))
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
fun SwitchSetting(text: String, setting: MutableState<Boolean>) {
    Row(
        Modifier.padding(horizontal = 20.dp).height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, Modifier.weight(1f))
        Switch(
            checked = setting.value,
            onCheckedChange = { setting.value = it; saveSettings() },
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
        )
    }
}

@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(20.dp),
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SearchView() {
    LazyColumn {
        item { TitleText("Search Settings") }
        item { SwitchSetting("Search in types", Settings.searchInTypes) }
        item { SwitchSetting("Search in locations", Settings.searchInLocations) }
        item { SwitchSetting("Search in notes", Settings.searchInNotes) }
    }
}

@Composable
fun ThemeView() {
    LazyColumn {
        item { TitleText("Theme Settings") }
    }
}

@Composable
fun SynchronizationView() {
    LazyColumn {
        item { TitleText("Synchronization Settings") }
    }
}

@Serializable
object Menu

private enum class SettingsCategory(
    val categoryName: String,
    val drawableRes: DrawableResource,
    val content: @Composable () -> Unit
) {
    SEARCH("Search", Res.drawable.search, { SearchView() }),
    THEME("Theme", Res.drawable.theme, { ThemeView() }),
    SYNCHRONIZATION("Synchronization", Res.drawable.synchronization, { SynchronizationView() })
}