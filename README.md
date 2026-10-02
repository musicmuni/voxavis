# VoxaVis

Compose Multiplatform visualization library for singing and pitch apps, on
Android and iOS. Scrolling practice canvases, live pitch monitors, charts,
meters, and segment and lyrics navigation: VoxaVis draws what your users sing.

VoxaVis renders what you give it. It does not capture audio, detect pitch or
score singing. Pair it with [VoxaTrace](https://github.com/musicmuni/voxatrace)
or any pitch source.

[![Maven Central](https://img.shields.io/maven-central/v/com.musicmuni/voxavis)](https://central.sonatype.com/artifact/com.musicmuni/voxavis)
[![Swift Package Manager](https://img.shields.io/badge/SPM-compatible-brightgreen)](https://github.com/musicmuni/voxavis)

Documentation: [musicmuni.github.io/voxavis](https://musicmuni.github.io/voxavis).
Upgrading from 1.x? See [MIGRATION.md](MIGRATION.md). What changed in each
release: [CHANGELOG.md](CHANGELOG.md).

## What's included

| Group | Components |
| --- | --- |
| **Features** | `SingingPractice` (the practice canvas: teacher's line, the learner's pitch, phrases, takes, count-in and rhythm lane), `InstantPitchMonitor`, `ScrollingPitchMonitor`, `PracticeReview` |
| **Primitives** | `PitchGrid`, `PitchContour`, `PitchBall`, `PitchTrail`, `ReferenceLine`, `NoteBars`, `SegmentBands`, `NowLine` |
| **Layouts** | `PitchSpace`, `ScrollingPitchSpace`: coordinate systems the primitives draw in |
| **Components** | Note accuracy, radar, vocal range, scatter and score trend charts; score cards and metrics lists; ring, tuning, beat, level and confidence meters |
| **Navigation** | `SegmentScrubber`, `SegmentedSeekBar`, `LyricsOverlay` |
| **Theme** | `VoxaVisTheme`, `VoxaVisColors`, `ScoreBands` |

Every component reads `MaterialTheme.colorScheme`, so an app with its own
Material 3 theme needs nothing else. Apps without one wrap VoxaVis in
`VoxaVisTheme`.

## Platforms

- **Android**: minSdk 24, compileSdk 37. AAR on Maven Central.
- **iOS**: iOS 15+, on devices and Apple Silicon simulators. XCFramework
  through Swift Package Manager or CocoaPods, with SwiftUI views for each
  component.

## Installation

### Android and Kotlin Multiplatform (Gradle)

```kotlin
dependencies {
    implementation("com.musicmuni:voxavis:2.0.0")
}
```

### iOS (Swift Package Manager)

In Xcode: **File > Add Package Dependencies**, enter
`https://github.com/musicmuni/voxavis`, or in `Package.swift`:

```swift
.package(url: "https://github.com/musicmuni/voxavis", from: "2.0.0")
```

### iOS (CocoaPods)

```ruby
pod 'VoxaVis', :podspec => 'https://raw.githubusercontent.com/musicmuni/voxavis/2.0.0/VoxaVis.podspec'
```

## Licensing and initialization

VoxaVis needs a licence. Call `VV.initialize` once at app start, before the
first feature component (`SingingPractice`, `InstantPitchMonitor`,
`ScrollingPitchMonitor`, `PracticeReview`) is shown; those throw
`VoxaVisNotInitializedException` without it. Charts, meters, navigation and
primitives do not need it.

The recommended setup keeps your API key on your server. Your backend exposes
one endpoint that registers a device with the licence server using your key
and returns the device token; the app passes that endpoint to `initialize`.
It is the same proxy contract VoxaTrace uses, so one endpoint can serve both
SDKs; what the endpoint receives and returns is in VoxaTrace's
[authentication guide](https://voxatrace.ai/guides/authentication). Contact
[support@musicmuni.com](mailto:support@musicmuni.com) for a key.

Android:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        VV.initialize(
            proxyEndpoint = "https://your-server.com/voxatrace/register",
            context = this,
            // Optional: the Authorization header your endpoint expects.
            proxyAuth = { "Bearer ${currentUserToken()}" },
        )
    }
}
```

`proxyAuth` is a suspend function, for a login token you fetch
asynchronously. `proxyAuthProvider` is the same for a token you already hold.
Pass one or neither. Your app needs the `INTERNET` permission.

iOS:

```swift
do {
    try VV.initialize(
        proxyEndpoint: "https://your-server.com/voxatrace/register",
        proxyAuthProvider: { "Bearer \(currentUserToken())" }
    )
} catch {
    print("VoxaVis licence error: \(error)")
}
```

Only a 403 from your endpoint switches the SDK off. A device that already
holds a token never waits on the network at start-up.

## Quick start

A practice screen: the teacher's line, the learner's live pitch, and a
playhead your player drives.

```kotlin
@Composable
fun PracticeScreen(
    lesson: SingingPracticeResources,
    pitchBuffer: CircularPitchBuffer, // fill it from your pitch detector
    positionMs: MutableLongState,     // write your player's position once per frame
) {
    val clock = remember(positionMs) { { positionMs.longValue } }

    SingingPractice(
        resources = lesson,
        currentTimeMs = clock,
        performancePitch = pitchBuffer,
    )
}
```

The playhead is a reader, called while drawing. Keep it in a `remember`: a
playing session then moves by one transform per frame instead of recomposing
the screen.

The learner's line is drawn only inside `SegmentType.PERFORMANCE` segments:
what the microphone hears while the teacher sings is not shown. For free
singing with no lesson, pass one performance segment that covers the track.

Describe the lesson once:

```kotlin
val lesson = SingingPracticeResources.create(
    mode = SessionMode.Singafter,
    trackLengthMs = 30_000L,
    segments = phrases,          // List<Segment>: who sings when
    referencePitch = teacherLine, // PitchContourData, in cents
    gridLines = scaleLines,       // List<GridLine>: the notes of the scale
)
```

A live pitch monitor needs only the buffer:

```kotlin
InstantPitchMonitor(
    currentTimeMs = nowMs,
    performancePitch = pitchBuffer,
    pitchRange = 100f..900f,
)
```

## Demo apps

[`demo-apps/android`](demo-apps/android) and [`demo-apps/ios`](demo-apps/ios)
show every component with mock data. Each has a README with setup steps.

## License

Commercial License. See [LICENSE](LICENSE). Contact
[support@musicmuni.com](mailto:support@musicmuni.com) for terms.
