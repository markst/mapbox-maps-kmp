plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "dev.mapboxkmp.demo.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.mapboxkmp.demo.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Read from local.properties / env at build time, never a tracked file. Fails loudly
        // (see build below) rather than shipping a build that renders a blank map.
        val mapboxPublicToken = providers.gradleProperty("MAPBOX_PUBLIC_TOKEN")
            .orElse(providers.environmentVariable("MAPBOX_PUBLIC_TOKEN"))
            .orNull
        manifestPlaceholders["MAPBOX_PUBLIC_TOKEN"] = mapboxPublicToken ?: "MISSING_MAPBOX_PUBLIC_TOKEN"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":demo:shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.compose.material3)
}
