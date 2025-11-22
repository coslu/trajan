package com.coslu.jobtracker

import io.github.vinceglb.filekit.PlatformFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

actual fun PlatformFile.openZipOutputStream() = ZipOutputStream(file.outputStream())

actual fun PlatformFile.openZipInputStream() = ZipInputStream(file.inputStream())