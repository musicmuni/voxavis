---
sidebar_position: 3
title: iOS quick start
---

# iOS quick start

The same practice screen from Swift. Each Compose component comes as a
SwiftUI view driven by a state object; you set properties on the state and the
view follows.

## 1. Initialize once

```swift
import voxavis

@main
struct MyApp: App {
    init() {
        do {
            try VV.initialize(
                proxyEndpoint: "https://your-server.com/voxatrace/register",
                proxyAuthProvider: { "Bearer \(Session.token)" }
            )
        } catch {
            print("VoxaVis licence error: \(error)")
        }
    }

    var body: some Scene { WindowGroup { ContentView() } }
}
```

See [Licensing](../guides/licensing.md).

## 2. Describe the lesson

```swift
let lesson = SingingPracticeResources.create(
    mode: .singafter,
    trackLengthMs: 30_000,
    segments: [
        Segment.create(startTimeMs: 0, endTimeMs: 4_000, type: .reference, lyrics: "Sa Re Ga"),
        Segment.create(startTimeMs: 4_000, endTimeMs: 8_000, type: .performance, lyrics: "Sa Re Ga"),
    ],
    gridLines: [
        GridLine.create(cents: 0, label: "Sa", isHighlighted: true, priority: GridLine.companion.PRIORITY_ANCHOR),
        GridLine.create(cents: 200, label: "Re"),
        GridLine.create(cents: 400, label: "Ga"),
    ],
    referencePitch: PitchContourData.create(points: teacherPoints) // [PitchPoint]
)
```

The `create` factories keep Kotlin's defaults, which Swift does not see on the
plain initializers.

## 3. The state and the view

```swift
let state = VoxaVisState(initialResources: lesson)
state.config = SingingPracticeConfig.create(minPitchCents: -300, maxPitchCents: 1500)
state.performancePitch = CircularPitchBuffer(capacity: 1000)

struct PracticeScreen: View {
    let state: VoxaVisState

    var body: some View {
        SingingPracticeView(state: state)
            .frame(height: 260)
    }
}
```

## 4. Feed the singer and the clock

```swift
// From your pitch detector's callback:
state.performancePitch?.addBlocking(timestampMs: nowMs, freqHz: hz, cents: cents)

// Once per displayed frame (a CADisplayLink), from your player:
state.currentTimeMs = player.positionMs
```

The canvas reads `currentTimeMs` while drawing, so setting it every frame moves
the picture without re-rendering your SwiftUI view. See
[The clock](../concepts/the-clock.md).

## Other components

Charts, meters and navigation work the same way: a state object and a view,
for example `NoteAccuracyChartState.create(notes:)` with
`NoteAccuracyChartView(state:)`, or `SegmentScrubberState` with
`SegmentScrubberView(state:)`. They do not need the licence.

## Demo app

`demo-apps/ios` in the [repository](https://github.com/musicmuni/voxavis) has a
screen for every component.
