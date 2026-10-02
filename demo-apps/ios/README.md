# VoxaVis iOS demo

A SwiftUI app showing every VoxaVis component with generated data. Open
`VoxaVisSample/VoxaVisSample.xcodeproj` in Xcode 26 or later and run it on a
simulator or an iPhone running iOS 16 or later.

## Setup

1. **API key.** Copy `VoxaVisSample/VoxaVisSample/Config.swift.template` to
   `Config.swift` in the same folder and put your VoxaVis API key in it.
   `Config.swift` is gitignored; never commit it. Without a key the app still
   builds once `Config.swift` exists, and the screens that need a licence say so.
2. **VoxaVis.** The project depends on this repository's Swift package
   (`Package.swift` at the root), which downloads the released XCFramework.
   Xcode resolves it on first open.

The demo starts VoxaVis with `VV.initializeForServer(apiKey:)` so it runs
without a backend. A shipping app should use `VV.initialize(proxyEndpoint:)`,
which keeps the key on your server; see the
[README](../../README.md#licensing-and-initialization).

## What each screen shows

**Canvas** (`SingingPracticeView`, needs the licence)

| Screen | Shows |
| --- | --- |
| Singing Session | A sing-after lesson: the teacher's line, the learner answering it, and the reference, grid labels and note names switched on and off through `SingingPracticeConfig` |
| Takes & Count-in | One phrase drilled three times as `TakePlacement`s on one session clock, each opened by a count (`LeadInMarks`) over a beat cycle (`MetricLaneCycle`) |
| Playback Review | A scored lesson with `SegmentScrubberView` under it: the canvas drives the scrubber's thumb and a committed scrub seeks the canvas |
| Freestyle Pitch | Free singing over the scale, with the ball resting on Sa between runs (`restingPitchCents`) |
| Minimal Mode | Two grid lines and a singer |
| Config Builder | Playhead position, time scale, the follow-the-phrase viewport and the visibility switches, live |

**Charts**, **Indicators** and **Navigation** show each component's SwiftUI
view (`NoteAccuracyChartView`, `RingMeterView`, `SegmentScrubberView`,
`SegmentedSeekBarView`, `LyricsOverlayView` and the rest) with mock data. They
do not need the licence.

## How the canvas is driven

`Shared/CanvasDemoModel.swift` plays the part of your player:

- It sets `VoxaVisState.currentTimeMs` once per frame. The canvas reads it
  while drawing, so a playing lesson does not re-render the SwiftUI view.
- It feeds the learner's pitch into `state.performancePitch`
  (a `CircularPitchBuffer`) as it "arrives", as a pitch detector's callback
  would.

`Shared/DemoLesson.swift` builds the lesson (`Segment`, `ScoreNote`,
`GridLine`, `PitchContourData`) in code. Your app builds the same types from
its own lesson data.

## Building against an unreleased VoxaVis

For VoxaVis maintainers, from the source repository:

```bash
./scripts/build.sh ios                # builds the XCFramework into Frameworks/
./scripts/ios-demo-source.sh local    # point the project at it
./scripts/ios-demo-source.sh spm      # back to the released package before committing
```
