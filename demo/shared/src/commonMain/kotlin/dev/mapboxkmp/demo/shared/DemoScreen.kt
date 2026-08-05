package dev.mapboxkmp.demo.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mapboxkmp.mapbox.CameraPosition
import dev.mapboxkmp.mapbox.MapboxMap

/**
 * The one screen this demo has: a full-bleed map plus a status line. It proves the whole
 * pipeline described in the root README end to end — a `MapboxMap` created from shared code,
 * embedded via a platform-specific [MapboxMapView], loading a style and reporting back when
 * it's done.
 */
@Composable
fun DemoScreen() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                var status by remember { mutableStateOf("Loading style…") }

                Box(modifier = Modifier.fillMaxSize()) {
                    MapboxMapView(
                        modifier = Modifier.fillMaxSize(),
                        configure = { map ->
                            map.onStyleLoaded { status = "Style loaded" }
                            map.setStyleUri("mapbox://styles/mapbox/streets-v12")
                            map.setCamera(
                                CameraPosition(latitude = 51.5074, longitude = -0.1278, zoom = 11.0)
                            )
                        },
                    )

                    Surface(
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    ) {
                        Text(text = status, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * Embeds the platform native map view. See `MapboxMap.android.kt` / `MapboxMap.ios.kt` in the
 * `:mapbox` module for what's behind it: `AndroidView` around a `MapView` on Android,
 * `UIKitViewController` around the `MapboxMapController` shim subclass on iOS.
 */
@Composable
expect fun MapboxMapView(modifier: Modifier = Modifier, configure: (MapboxMap) -> Unit = {})
