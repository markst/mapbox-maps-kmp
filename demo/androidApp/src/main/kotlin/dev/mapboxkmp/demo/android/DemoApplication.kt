package dev.mapboxkmp.demo.android

import android.app.Application
import android.content.pm.PackageManager
import com.mapbox.common.MapboxOptions
import dev.mapboxkmp.mapbox.MapboxMap

private const val TOKEN_META_DATA_KEY = "dev.mapboxkmp.demo.MAPBOX_PUBLIC_TOKEN"
private const val TOKEN_PLACEHOLDER = "MISSING_MAPBOX_PUBLIC_TOKEN"

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MapboxMap.initialize(this)

        val token = packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            .metaData
            ?.getString(TOKEN_META_DATA_KEY)

        check(!token.isNullOrBlank() && token != TOKEN_PLACEHOLDER) {
            "No Mapbox public token configured. Set MAPBOX_PUBLIC_TOKEN in your user-level " +
                "~/.gradle/gradle.properties (or the MAPBOX_PUBLIC_TOKEN env var) — see the " +
                "root README's \"Credentials\" section. Refusing to start with a blank map."
        }

        MapboxOptions.accessToken = token
    }
}
