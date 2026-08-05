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
        // Path dependency onto the pinned submodule, not a versioned remote package.
        // See root README "Submodules vs. versioning" for why.
        .package(path: "submodules/mapbox-maps-ios")
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
