// swift-tools-version:5.9
import PackageDescription

let version = "1.0.0"
let releaseTag = "voxavis-v1.0.0"
let checksum = "a8a9cb7ee76317fd1f85740b6437e7d8ecbc704e08bf966972e20dfa6445cadb"

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
