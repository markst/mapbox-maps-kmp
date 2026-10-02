package dev.mapboxkmp.demo.shared

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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

        // iOS-style push/pop: the pushed screen slides in from the right over the screen
        // below, which drifts left by a third of the width (UINavigationController parallax).
        // Pop is the exact reverse.
        //
        // Not `AnimatedContent`/`NavHost`: those dispose the outgoing screen, and the screen
        // below here is full of native map views. Disposing it would blank every list map on
        // pop (they'd be recreated mid-animation), and native views don't honour Compose
        // z-index, so a freshly re-attached list would draw *over* the sliding detail screen.
        // Instead the tab screen stays composed under the detail screen, and the detail
        // screen — always attached later — is naturally on top.
        val progress = remember { Animatable(0f) } // 0 = tabs showing, 1 = detail showing
        var displayed by remember { mutableStateOf<Location?>(null) } // outlives `selected` for the pop animation
        LaunchedEffect(selected) {
            val spec = tween<Float>(durationMillis = 350, easing = FastOutSlowInEasing)
            val target = selected
            if (target != null) {
                displayed = target
                progress.animateTo(1f, spec)
            } else {
                progress.animateTo(0f, spec)
                displayed = null
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationX = -size.width / 3f * progress.value },
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

            displayed?.let { location ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { translationX = size.width * (1f - progress.value) },
                ) {
                    LocationDetailScreen(location = location, onBack = { selected = null })
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
