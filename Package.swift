// swift-tools-version:5.9
import PackageDescription

let version = "2.0.0"
let releaseTag = "voxavis-v2.0.0"
let checksum = "1765c32438bbc76c0d07cb72eeedd9b36879be964837dcb806bfdee2e0fc6732"

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
