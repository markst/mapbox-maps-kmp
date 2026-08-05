package dev.mapboxkmp.demo.shared

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * The demo's sole entry point from Swift. `demo/iosApp` calls this once from its `App` /
 * `UIApplicationDelegate` and presents the result as the root view controller — the rest of the
 * screen (including embedding the Mapbox shim, see `MapboxMapView.ios.kt`) is Kotlin/Compose.
 */
fun MainViewController(): UIViewController = ComposeUIViewController { DemoScreen() }
