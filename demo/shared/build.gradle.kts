import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    android {
        namespace = "dev.mapboxkmp.demo.shared"
        compileSdk = 36
        minSdk = 24

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    val xcf = XCFramework("DemoShared")
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "DemoShared"
            xcf.add(this)
            // See :mapbox's build.gradle.kts for why this is static, not dynamic.
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // `api`: `MapboxMapView`'s signature exposes `MapboxMap` publicly, and the
            // androidApp module needs `MapboxMap`/`MapboxOptions` directly too (see
            // `DemoApplication`).
            api(project(":mapbox"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}
