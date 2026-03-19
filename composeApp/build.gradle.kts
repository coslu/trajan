import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    android {
        namespace = "com.coslu.jobtracker"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        androidResources {
            enable = true
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.jetbrains.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.material3)
            implementation(libs.navigation.compose)
            implementation(libs.filekit.dialogs.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    configurations.all {
        exclude(group = "androidx.compose.ui", module = "ui-util")
    }
    application {
        mainClass = "com.coslu.jobtracker.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Trajan"
            packageVersion = libs.versions.trajan.versionName.get()
            description = "Tracking Assistant for Job Applications"
            vendor = "Coslu"
            licenseFile.set(project.file("../LICENSE"))
            val icons = project.file("src/commonMain/composeResources/drawable")
            windows {
                iconFile.set(icons.resolve("icon-windows.ico"))
                upgradeUuid = "0AC1BD47-4C11-4D95-8CB9-F0F06302A8EA"
            }
            linux {
                iconFile.set(icons.resolve("icon-linux.png"))
                modules("jdk.security.auth")
            }
        }
    }
}
