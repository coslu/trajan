package com.coslu.jobtracker

import androidx.core.net.toUri
import io.github.vinceglb.filekit.AndroidFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.context
import io.github.vinceglb.filekit.path
import java.io.File
import java.util.zip.ZipOutputStream

actual fun PlatformFile.openZipOutputStream() =
    when (androidFile) {
        is AndroidFile.FileWrapper -> ZipOutputStream(File(path).outputStream())
        is AndroidFile.UriWrapper -> ZipOutputStream(
            FileKit.context.contentResolver.openOutputStream(
                path.toUri()
            )
        )
    }
