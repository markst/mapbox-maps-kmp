package dev.mapboxkmp.demo.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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

private enum class Tab(val label: String, val glyph: String) {
    Map("Map", "🗺"),
    Locations("Locations", "📍"),
}

/**
 * Demo root: a bottom tab bar with a single-map tab and a list-of-maps tab. Tapping a list row
 * pushes a full-screen map on top of the tab bar; back (button, system back, or iOS edge swipe)
 * returns to the list. Navigation is plain Compose state — two destinations don't warrant a
 * navigation library dependency.
 */
@Composable
fun DemoScreen() {
    MaterialTheme {
        var tab by remember { mutableStateOf(Tab.Map) }
        var selected by remember { mutableStateOf<Location?>(null) }
        // Hoisted so the list keeps its scroll position across a detail push/pop.
        val listState = rememberLazyListState()

        val detail = selected
        if (detail != null) {
            Surface(modifier = Modifier.fillMaxSize()) {
                LocationDetailScreen(location = detail, onBack = { selected = null })
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar {
                        Tab.entries.forEach { entry ->
                            NavigationBarItem(
                                selected = tab == entry,
                                onClick = { tab = entry },
                                icon = { Text(entry.glyph) },
                                label = { Text(entry.label) },
                            )
                        }
                    }
                },
            ) { padding ->
                when (tab) {
                    Tab.Map -> MapTab(modifier = Modifier.padding(padding))
                    Tab.Locations -> LocationsListScreen(
                        onLocationClick = { selected = it },
                        contentPadding = padding,
                        listState = listState,
                    )
                }
            }
        }
    }
}

/**
 * A full-bleed map plus a status line. It proves the whole pipeline described in the root
 * README end to end — a `MapboxMap` created from shared code, embedded via a platform-specific
 * [MapboxMapView], loading a style and reporting back when it's done.
 */
@Composable
private fun MapTab(modifier: Modifier = Modifier) {
    var status by remember { mutableStateOf("Loading style…") }

    Box(modifier = modifier.fillMaxSize()) {
        MapboxMapView(
            modifier = Modifier.fillMaxSize(),
            configure = { map ->
                map.onStyleLoaded { status = "Style loaded" }
                map.setStyleUri("mapbox://styles/mapbox/streets-v12")
                map.setCamera(
                    CameraPosition(latitude = 51.5074, longitude = -0.1278, zoom = 1.0)
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

/**
 * Embeds the platform native map view. See `MapboxMap.android.kt` / `MapboxMap.ios.kt` in the
 * `:mapbox` module for what's behind it: `AndroidView` around a `MapView` on Android,
 * `UIKitViewController` around the `MapboxMapController` shim subclass on iOS.
 */
@Composable
expect fun MapboxMapView(modifier: Modifier = Modifier, configure: (MapboxMap) -> Unit = {})
