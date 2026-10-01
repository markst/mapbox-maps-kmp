// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_mapbox",
  platforms: [
    .iOS("15.0")
  ],
  products: [
    .library(
      name: "_mapbox",
      type: .none,
      targets: ["_mapbox"]
    )
  ],
  dependencies: [
    .package(
      path: "../../../../../mapbox/native"
    )
  ],
  targets: [
    .target(
      name: "_mapbox",
      dependencies: [
        .product(
          name: "MapboxShim",
          package: "native"
        )
      ]
    )
  ]
)
