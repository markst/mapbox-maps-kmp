package dev.mapboxkmp.mapbox

import android.content.Context
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.camera

/**
 * Android implementation. `MapView` needs a `Context` to construct, but the `expect`
 * constructor is parameterless — so, like most Android singletons that need process-wide
 * context, the application `Context` is supplied once via [initialize] rather than per-instance.
 * Call this before constructing any [MapboxMap], typically from `Application.onCreate`.
 */
actual class MapboxMap actual constructor() {

    companion object {
        private lateinit var appContext: Context

        fun initialize(context: Context) {
            appContext = context.applicationContext
        }
    }

    /**
     * The underlying platform view. Not part of the `expect` surface — commonMain code has no
     * use for a `MapView`, but platform-specific UI code (e.g. the demo's `AndroidView`
     * wrapper) needs it to actually put a map on screen.
     */
    val view: MapView = MapView(appContext)

    private var styleLoadedCallback: (() -> Unit)? = null

    actual fun setStyleUri(uri: String) {
        view.mapboxMap.loadStyle(uri) {
            styleLoadedCallback?.invoke()
        }
    }

    actual fun setCamera(camera: CameraPosition, animated: Boolean) {
        val options = cameraOptions {
            center(Point.fromLngLat(camera.longitude, camera.latitude))
            zoom(camera.zoom)
            bearing(camera.bearing)
            pitch(camera.pitch)
        }
        if (animated) {
            view.camera.flyTo(options)
        } else {
            view.mapboxMap.setCamera(options)
        }
    }

    actual fun onStyleLoaded(callback: () -> Unit) {
        styleLoadedCallback = callback
    }
}
