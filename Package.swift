// swift-tools-version:5.9
// This file is regenerated on each release by scripts/release-sdk.sh
// The values below are placeholders — replaced when a release is published.
import PackageDescription

let version = "0.0.0"
let releaseTag = "voxavis-v0.0.0"
let checksum = "0000000000000000000000000000000000000000000000000000000000000000"

let package = Package(
    name: "VoxaVis",
    platforms: [.iOS(.v15)],
    products: [
        .library(name: "VoxaVis", targets: ["VoxaVis"]),
    ],
    targets: [
        .binaryTarget(
            name: "VoxaVis",
            url: "https://github.com/musicmuni/voxavis/releases/download/\(releaseTag)/voxavis.xcframework.zip",
            checksum: checksum
        ),
    ]
)
