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
        // Path dependency onto the pinned submodule, not a versioned remote package.
        // See root README "Submodules vs. versioning" for why.
        .package(path: "../../submodules/mapbox-maps-ios")
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
