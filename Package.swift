// swift-tools-version: 5.9
import PackageDescription

// Lets this repo be consumed directly as a Swift package (e.g. `.package(url: ".../mapbox-maps-kmp",
// from: "0.1.0")`), independent of the Gradle/KMP build. Points at the same `MapboxShim` sources as
// `mapbox/native/Package.swift` — that second manifest is what Kotlin's `localSwiftPackage` reads
// during the Gradle build (`swiftPMDependencies` needs the package rooted at `mapbox/native`
// specifically), so both are kept in sync rather than one depending on the other.
let package = Package(
    name: "MapboxShim",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "MapboxShim",
            targets: ["MapboxShim"]
        )
    ],
    dependencies: [
        // Versioned remote package, not a submodule. See root README "Versioned
        // dependencies, not submodules" for why. `exact:` (not `from:`) because the point is a
        // single, deliberately-bumped pin, not a floating semver range.
        .package(url: "https://github.com/mapbox/mapbox-maps-ios.git", exact: "11.26.0")
    ],
    targets: [
        .target(
            name: "MapboxShim",
            dependencies: [
                .product(name: "MapboxMaps", package: "mapbox-maps-ios")
            ],
            path: "mapbox/native/MapboxShim"
        )
    ]
)
