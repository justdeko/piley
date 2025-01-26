
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(libs.material)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(libs.adaptive)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.room.runtime)
            implementation(libs.kotlinx.datetime)
            implementation(compose.materialIconsExtended)
            implementation(libs.navigation.compose)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.material3.window.size)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "com.dk.piley"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dk.piley"
        minSdk = 26
        targetSdk = 35
        versionCode = 16
        versionName = "0.8.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    ksp(libs.androidx.room.compiler)
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.dk.piley.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            jvmArgs(
                "-Dapple.awt.application.appearance=system"
            )
            packageName = "piley"
            packageVersion = "1.8.1"
            // copyright with copyright symbol
            copyright = "© 2024 Denis Koljada. All rights reserved."
            // fixes datastore unsafe issue: https://github.com/JetBrains/compose-multiplatform/issues/2686
            modules("jdk.unsupported")
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            macOS {
                iconFile.set(project.file("icon.icns"))
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
