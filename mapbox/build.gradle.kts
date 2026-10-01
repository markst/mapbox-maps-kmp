@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    `maven-publish`
}

// Consumed as `dev.mapboxkmp:mapbox:0.1.0-SNAPSHOT` — see README "Integrating into your own
// app". The Kotlin Gradle plugin auto-creates a publication per target (metadata, android,
// iosArm64, iosSimulatorArm64) once `maven-publish` is applied; no explicit `publishing {}`
// block is needed for that part.
group = "dev.mapboxkmp"
version = "0.1.0-SNAPSHOT"

kotlin {
    compilerOptions {
        optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    android {
        namespace = "dev.mapboxkmp.mapbox"
        compileSdk = 36
        minSdk = 24

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    val xcf = XCFramework("Mapbox")
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Mapbox"
            xcf.add(this)
            // Static is the documented recommendation for swiftPMDependencies: a dynamic
            // framework leaves the synthetic linkage package's dylib sub-package unembedded,
            // producing a `Library not loaded: @rpath/...Dylib.framework` crash at launch.
            isStatic = true
        }
    }

    swiftPMDependencies {
        iosMinimumDeploymentTarget = "14.0"

        // @objc shim over the versioned mapbox-maps-ios remote package. See
        // mapbox/native/Package.swift and README "Versioned dependencies, not submodules".
        localSwiftPackage(
            directory = layout.projectDirectory.dir("native"),
            products = listOf("MapboxShim"),
        )
    }

    sourceSets {
        androidMain.dependencies {
            // `api`, not `implementation`: `MapboxMap.view` publicly exposes `MapView`, so
            // consumers (e.g. the demo) need `com.mapbox.maps.*` on their own classpath too.
            //
            // `-ndk27`, not the default artifact: identical API, but built with NDK 27 so the
            // 64-bit `.so`s (`libmapbox-maps.so`, `libmapbox-common.so`, `libc++_shared.so`)
            // carry `p_align=0x4000`. The default `com.mapbox.maps:android` ships 4 KB-aligned
            // binaries, which Google Play rejects for apps targeting Android 15+ since
            // 2025-11-01. Verified: `android-core-ndk27:11.27.1` arm64-v8a/x86_64 LOAD
            // segments are 16 KB-aligned; `android-core:11.27.1` are 4 KB.
            api(libs.mapbox.android.ndk27)
        }
        commonMain.dependencies {
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
