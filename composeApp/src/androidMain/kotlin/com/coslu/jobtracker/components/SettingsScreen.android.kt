package com.coslu.jobtracker.components

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coslu.jobtracker.Settings
import com.coslu.jobtracker.dataDir
import com.coslu.jobtracker.saveSettings
import com.coslu.jobtracker.showSnackbar
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.PlatformFile
import job_tracker.composeapp.generated.resources.Res
import job_tracker.composeapp.generated.resources.preferred_color
import job_tracker.composeapp.generated.resources.preferred_theme
import job_tracker.composeapp.generated.resources.theme_settings
import job_tracker.composeapp.generated.resources.use_system_colors
import job_tracker.composeapp.generated.resources.use_system_colors_subtitle
import org.jetbrains.compose.resources.stringResource
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.inputStream
import kotlin.io.path.name

@Composable
actual fun ThemeView(modifier: Modifier) {
    val colorsEnabled =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || !Settings.Color.useSystemColors.value
    val disabledColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val textColor = if (colorsEnabled) Color.Unspecified else disabledColor
    LazyVerticalGrid(GridCells.Adaptive(140.dp), modifier) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            TitleText(stringResource(Res.string.theme_settings))
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            DropdownSetting(
                stringResource(Res.string.preferred_theme),
                Settings.Theme.options,
                Settings.Theme.current
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                stringResource(Res.string.preferred_color),
                Modifier.padding(bottom = 20.dp),
                color = textColor
            )
        }
        items(Settings.Color.options) {
            val modifier = if (colorsEnabled && Settings.Color.current.value == it) Modifier.border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10)
            ) else Modifier
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(10))
                    .clickable(enabled = colorsEnabled) {
                        Settings.Color.current.value = it; saveSettings()
                    }
            ) {
                Column(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (colorsEnabled) it.icon() else Box(
                        Modifier
                            .background(
                                disabledColor,
                                CircleShape
                            )
                            .size(24.dp)
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(it.name, textAlign = TextAlign.Center, color = textColor)
                }
            }
        }
    }
}

actual fun exportToFile(path: String, filesToZip: List<String>, errorMessage: String) {
    try {
        ZipOutputStream((PlatformFile(path).androidFile as AndroidFile.FileWrapper).file.outputStream()).use { zipOutputStream ->
            filesToZip.forEach { fileName ->
                val path = dataDir.resolve(fileName)
                zipOutputStream.putNextEntry(ZipEntry(path.name))
                path.inputStream().use { it.copyTo(zipOutputStream) }
                zipOutputStream.closeEntry()
            }
        }
    } catch (ex: Exception) {
        Log.e("Export", ex.message, ex)
        showSnackbar(errorMessage)
    }
}