# VoxaVis

Cross-platform Compose visualization library for vocal and pitch applications. Scrolling pitch canvases, offline charts, real-time indicators, and navigation components — everything you need to render what your users sing.

Part of the [MusicMuni](https://musicmuni.com) SDK family. Pairs naturally with [VoxaTrace](https://github.com/musicmuni/voxatrace) (pitch detection) but usable standalone with any pitch source.

[![Maven Central](https://img.shields.io/maven-central/v/com.musicmuni/voxavis)](https://central.sonatype.com/artifact/com.musicmuni/voxavis)
[![Swift Package Manager](https://img.shields.io/badge/SPM-compatible-brightgreen)](https://github.com/musicmuni/voxavis)

## What's Included

| Layer | Components |
| --- | --- |
| **Primitives** | `PitchGrid`, `PitchContour`, `PitchBall`, `PitchTrail`, `ReferenceLine`, `NoteBars`, `SegmentBands`, `NowLine` |
| **Layouts** | `PitchSpace`, `ScrollingPitchSpace` — coordinate-system layouts |
| **Features** | `SingingPractice`, `InstantPitchMonitor`, `ScrollingPitchMonitor`, `PracticeReview` — opinionated compositions |
| **Components** | Radar/bar/scatter charts, score cards, ring/tuning/beat/level/confidence meters |
| **Navigation** | `SegmentedSeekBar`, `LyricsOverlay` |

## Platforms

- **Android** — minSdk 24, distributed as AAR via Maven Central
- **iOS** — iOS 15+, distributed as XCFramework via SPM or CocoaPods
- **Multiplatform** — KMP artifacts for JVM, JS, WASM also published

## Installation

### Android (Gradle)

```kotlin
dependencies {
    implementation("com.musicmuni:voxavis:1.0.0")
}
```

### iOS (Swift Package Manager)

In Xcode: **File → Add Package Dependencies** → enter:

```
https://github.com/musicmuni/voxavis.git
```

### iOS (CocoaPods)

```ruby
pod 'VoxaVis', '~> 1.0.0'
```

## Quick Start

```kotlin
// Initialize the SDK once at app startup
VV.initialize(
    proxyEndpoint = "https://your-server.com/voxatrace/register",
    context = this,
)

// Then use any component
@Composable
fun MyScreen(pitchBuffer: CircularPitchBuffer, nowMs: Long) {
    InstantPitchMonitor(
        currentTimeMs = nowMs,
        performancePitch = pitchBuffer,
        pitchRange = 100f..900f,
    )
}
```

VoxaVis is a pure visualization library — it renders what you give it. It does NOT handle audio I/O or pitch detection. Pair it with [VoxaTrace](https://github.com/musicmuni/voxatrace) or any pitch source.

## License

Commercial License — see [LICENSE](LICENSE). Contact [support@musicmuni.com](mailto:support@musicmuni.com) for terms.
