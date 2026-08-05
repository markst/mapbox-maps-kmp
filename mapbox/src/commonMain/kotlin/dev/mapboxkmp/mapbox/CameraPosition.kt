package dev.mapboxkmp.mapbox

/**
 * A minimal, platform-agnostic camera position. Mirrors the handful of fields both
 * `com.mapbox.maps.CameraOptions` (Android) and `MapboxMaps.CameraOptions` (iOS) share.
 */
data class CameraPosition(
    val latitude: Double,
    val longitude: Double,
    val zoom: Double = 12.0,
    val bearing: Double = 0.0,
    val pitch: Double = 0.0,
)
