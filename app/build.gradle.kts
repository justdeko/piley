plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.dk.piley"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dk.piley"
        minSdk = 26
        targetSdk = 34
        versionCode = 13
        versionName = "0.6.8"

        // default api base urls
        val url = project.findProperty("API_URL") as String? ?: "https://127.0.0.1/"
        buildConfigField("String", "LOCAL_API_BASE_URL", "\"$url\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            buildConfigField("String", "LOCAL_API_BASE_URL", "\"https://127.0.0.1/\"")
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.logging.interceptor)
    implementation(libs.timber)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.mockito.kotlin)

    implementation(libs.material)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
