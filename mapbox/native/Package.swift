// swift-tools-version: 5.9
import PackageDescription

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
            path: "./MapboxShim"
        )
    ]
)
