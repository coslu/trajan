plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("plugin.serialization") version "2.2.0" apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}
