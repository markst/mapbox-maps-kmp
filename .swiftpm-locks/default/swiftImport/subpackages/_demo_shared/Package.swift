// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_demo_shared",
  platforms: [
    .iOS("15.0")
  ],
  products: [
    .library(
      name: "_demo_shared",
      type: .none,
      targets: ["_demo_shared"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_demo_shared",
      dependencies: [
      ]
    )
  ]
)
