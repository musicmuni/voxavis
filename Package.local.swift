// swift-tools-version:5.9
import PackageDescription

// LOCAL DEV: points at the locally-built xcframework placed by
// voxavis-source's ./scripts/build.sh ios.
// To use this file during development, rename it to Package.swift
// (save the original first). Restore before pushing.

let package = Package(
    name: "VoxaVis",
    platforms: [.iOS(.v15)],
    products: [
        .library(name: "VoxaVis", targets: ["VoxaVis"]),
    ],
    targets: [
        .binaryTarget(
            name: "VoxaVis",
            path: "demo-apps/ios/VoxaVisSample/Frameworks/voxavis.xcframework"
        ),
    ]
)
