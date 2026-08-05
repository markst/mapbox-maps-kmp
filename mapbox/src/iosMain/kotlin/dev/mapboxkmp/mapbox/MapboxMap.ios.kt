package dev.mapboxkmp.mapbox

import kotlinx.cinterop.ExperimentalForeignApi
import swiftPMImport.dev.mapboxkmp.mapbox.MapboxMapController

/**
 * iOS implementation. Subclasses the `@objc` shim directly (see `mapbox/native/MapboxShim`)
 * rather than wrapping it, so the resulting instance is itself the `UIViewController` that
 * platform-specific UI code (e.g. the demo's `UIKitViewController` wrapper) embeds.
 */
@OptIn(ExperimentalForeignApi::class)
actual class MapboxMap actual constructor() : MapboxMapController() {

    init {
        // `MapboxMapController.mapView` is populated lazily in `loadView()`, which UIKit only
        // calls once something asks for `view`. Callers (e.g. the demo's `configure` lambda)
        // call `setStyleUri`/`setCamera` right after construction, before this instance is ever
        // placed in a view hierarchy — force it now so `mapView` is never nil for them. Mirrors
        // androidMain, where `view` is constructed eagerly in the property initializer.
        loadViewIfNeeded()
    }

    actual fun setStyleUri(uri: String) {
        loadStyleUri(uri)
    }

    actual fun setCamera(camera: CameraPosition, animated: Boolean) {
        moveCamera(
            latitude = camera.latitude,
            longitude = camera.longitude,
            zoom = camera.zoom,
            bearing = camera.bearing,
            pitch = camera.pitch,
            animated = animated,
        )
    }

    actual fun onStyleLoaded(callback: () -> Unit) {
        subscribeStyleLoadedWithCallback(callback)
    }
}
