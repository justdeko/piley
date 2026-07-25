import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.time.ExperimentalTime"
        )
    }
    android {
        namespace = "com.dk.piley"
        compileSdk = 37
        minSdk = 26
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
    }

    jvm("desktop")

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain = getByName("desktopMain")

        androidMain.dependencies {
            implementation(libs.material)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.material3.adaptive.navigation.suite)
            implementation(libs.adaptive)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.room.runtime)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.navigation.compose)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.material3.window.size)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.reorderable)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.network)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.compose.ui.backhandler)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.jmdns)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
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
            packageVersion = "1.9.5"
            // copyright with copyright symbol
            copyright = "© 2026 Denis Koljada. All rights reserved."
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
