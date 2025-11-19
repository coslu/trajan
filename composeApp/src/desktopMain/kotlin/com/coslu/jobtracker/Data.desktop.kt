package com.coslu.jobtracker

import io.github.vinceglb.filekit.PlatformFile
import java.util.zip.ZipOutputStream

actual fun PlatformFile.openZipOutputStream() = ZipOutputStream(file.outputStream())