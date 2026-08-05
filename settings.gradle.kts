@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        // Mapbox Android SDK. As of this writing the release Maven repo serves artifacts
        // without authentication, but Mapbox's docs describe a DOWNLOADS:READ secret token
        // as required, and that may be re-enforced. Attach credentials only when a token is
        // actually configured (never from a tracked file) so an unset/blank password can't
        // turn a working anonymous request into a 401 — see README "Credentials" section.
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            val mapboxDownloadsToken = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN")
                .orElse(providers.environmentVariable("MAPBOX_DOWNLOADS_TOKEN"))
                .orNull
                ?.takeIf { it.isNotBlank() }
            if (mapboxDownloadsToken != null) {
                authentication { create<BasicAuthentication>("basic") }
                credentials {
                    username = "mapbox"
                    password = mapboxDownloadsToken
                }
            }
        }
    }
}

rootProject.name = "mapbox-maps-kmp"

include(":mapbox")
include(":demo:shared")
include(":demo:androidApp")
