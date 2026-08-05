package dev.mapboxkmp.mapbox

/**
 * A thin common-code facade over the native Mapbox Maps SDKs.
 *
 * - Android: wraps `com.mapbox.maps.MapView` from the versioned Maven artifact directly.
 * - iOS: wraps `MapboxMapController`, an `@objc` shim (see `mapbox/native/MapboxShim`) around
 *   `MapboxMaps.MapView`, imported via Kotlin's `swiftPMDependencies`.
 *
 * This is a v0 vertical slice — style loading and camera control only — proving the pipeline
 * end to end rather than mirroring the full native API surface. See the root README for scope
 * and the rationale for what did and did not make the cut.
 */
expect class MapboxMap() {
    /** Loads a style by URI (e.g. `"mapbox://styles/mapbox/streets-v12"`). */
    fun setStyleUri(uri: String)

    /** Moves the camera, optionally animating the transition. */
    fun setCamera(camera: CameraPosition, animated: Boolean = false)

    /** Registers a callback invoked every time a style finishes loading. */
    fun onStyleLoaded(callback: () -> Unit)
}
