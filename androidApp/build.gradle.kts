import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    target {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    dependencies {
        implementation(projects.composeApp)
        implementation(libs.jetbrains.ui.tooling.preview)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.ui.util.android)
    }
}

android {
    namespace = "com.coslu.jobtracker"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.coslu.jobtracker"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.trajan.versionCode.get().toInt()
        versionName = libs.versions.trajan.versionName.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}