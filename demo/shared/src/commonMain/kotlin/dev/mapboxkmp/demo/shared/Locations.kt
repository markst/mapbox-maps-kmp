package dev.mapboxkmp.demo.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import dev.mapboxkmp.mapbox.CameraPosition
import dev.mapboxkmp.mapbox.MapboxMap

/** A named place the demo can show on a map. */
data class Location(val name: String, val camera: CameraPosition)

val demoLocations: List<Location> = listOf(
    Location("London", CameraPosition(latitude = 51.5074, longitude = -0.1278, zoom = 11.0)),
    Location("New York", CameraPosition(latitude = 40.7128, longitude = -74.0060, zoom = 11.0)),
    Location("Tokyo", CameraPosition(latitude = 35.6762, longitude = 139.6503, zoom = 11.0)),
    Location("Sydney", CameraPosition(latitude = -33.8688, longitude = 151.2093, zoom = 11.0)),
    Location("Cape Town", CameraPosition(latitude = -33.9249, longitude = 18.4241, zoom = 11.0)),
    Location("Rio de Janeiro", CameraPosition(latitude = -22.9068, longitude = -43.1729, zoom = 11.0)),
    Location("Reykjavík", CameraPosition(latitude = 64.1466, longitude = -21.9426, zoom = 11.0)),
)

private const val STYLE_URI = "mapbox://styles/mapbox/streets-v12"

private fun MapboxMap.show(location: Location) {
    setStyleUri(STYLE_URI)
    setCamera(location.camera)
}

/** Scrollable list; every row embeds its own map centred on the row's location. */
@Composable
fun LocationsListScreen(
    onLocationClick: (Location) -> Unit,
    contentPadding: PaddingValues,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 12.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(demoLocations, key = { it.name }) { location ->
            LocationRow(location, onClick = { onLocationClick(location) })
        }
    }
}

@Composable
private fun LocationRow(location: Location, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                MapboxMapView(
                    modifier = Modifier.fillMaxSize(),
                    configure = { it.show(location) },
                )
                // The native map view consumes touches (and would fight list scrolling), so
                // a transparent Compose layer on top owns tap handling for the whole row.
                Box(modifier = Modifier.fillMaxSize().clickable(onClick = onClick))
            }
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
            )
        }
    }
}

/** Full-screen, interactive map for [location] with a back button overlaid in the corner. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LocationDetailScreen(location: Location, onBack: () -> Unit) {
    // System back (Android) and the edge-swipe gesture (iOS) behave like the on-screen button.
    BackHandler(onBack = onBack)

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMapView(
            modifier = Modifier.fillMaxSize(),
            configure = { it.show(location) },
        )
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(12.dp)
                .align(Alignment.TopStart),
        ) {
            FilledTonalButton(onClick = onBack) { Text("‹ Back") }
            Surface(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    }
}
