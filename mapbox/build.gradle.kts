@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
}

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

        // @objc shim over the mapbox-maps-ios submodule. See mapbox/native/Package.swift
        // and README "Submodules vs. versioning" for why this is a path dependency onto
        // a pinned submodule rather than a versioned remote package.
        localSwiftPackage(
            directory = layout.projectDirectory.dir("native"),
            products = listOf("MapboxShim"),
        )
    }

    sourceSets {
        androidMain.dependencies {
            // `api`, not `implementation`: `MapboxMap.view` publicly exposes `MapView`, so
            // consumers (e.g. the demo) need `com.mapbox.maps.*` on their own classpath too.
            api(libs.mapbox.android)
        }
        commonMain.dependencies {
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
