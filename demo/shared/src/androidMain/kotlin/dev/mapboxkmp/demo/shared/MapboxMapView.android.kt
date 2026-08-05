package dev.mapboxkmp.demo.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import dev.mapboxkmp.mapbox.MapboxMap

@Composable
actual fun MapboxMapView(modifier: Modifier, configure: (MapboxMap) -> Unit) {
    val map = remember { MapboxMap().also(configure) }
    AndroidView(modifier = modifier, factory = { map.view })
}
