package com.coslu.jobtracker.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
                    textAlign = TextAlign.Start,
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
                        HorizontalDivider()
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
fun SearchView() {
    Text("Search View")
}

@Composable
fun ThemeView() {
    Text("Theme View")
}

@Composable
fun SynchronizationView() {
    Text("Synchronization View")
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