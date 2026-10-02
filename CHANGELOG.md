# Changelog

All notable changes to VoxaVis will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [2.0.0] - Unreleased

A major release. Score colours are no longer built into the components: you
pass a `ScoreBands` that says which colour a score gets. The practice canvas
gains a session clock that several takes can be placed on, a count-in and a
rhythm lane, and a new scrubbable segment bar. Time reaches the canvas as a
reader, so a running session no longer recomposes the screen on every frame.
Built with Kotlin 2.4.20 and Compose Multiplatform 1.12.1.
Upgrade steps with before and after code: [MIGRATION.md](MIGRATION.md).

### Breaking

- `ScoreColorConfig` is removed. Score colours come from `ScoreBands`
  (`com.musicmuni.voxavis.theme`), a list of colours with either discrete
  stops or a continuous gradient. `ScoreBands.trafficLight()` is a ready-made
  red, amber, green gradient.
- `SegmentedSeekBarStyle` loses `scoreGoodColor`, `scoreAverageColor`,
  `scoreBadColor`, `scoreGoodFadedColor`, `scoreAverageFadedColor`,
  `scoreBadFadedColor`, `scoreThresholdGood` and `scoreThresholdAverage`. It
  takes `bands: ScoreBands` as its first parameter, and `fadedAlpha` (default
  0.42) replaces the three faded colours.
- `AccuracyData.deviationRemark: AccuracyLevel` is replaced by
  `score: Float`, from 0 to 100. `AccuracyLevel` is removed.
- `NoteAccuracyChartStyle` loses `goodColor`, `poorColor`,
  `goodNoteLabelTextStyle` and `poorNoteLabelTextStyle`. It takes
  `bands: ScoreBands` as its first parameter, and one `noteLabelTextStyle`
  is used on every dot.
- `NoteBarsStyle` takes a required `labelColor` as its second parameter. A
  call that passes `barHeight` or later parameters by position must name them.
- `SingingPracticeStyle` has three new required properties: `metricLane`,
  `leadInMarks` and `callout`. Code that builds the style through
  `SingingPracticeStyle.default()` and `.copy()` is unaffected; a direct
  constructor call must pass them.
- `SingingPractice` and `SegmentScrubber` take the playhead only as a reader,
  `currentTimeMs: () -> Long`. The `Long` versions are removed.
- Removed parameters and properties that were accepted and not read:
  `accuracy` on `SingingPractice`, `PitchBall` and `PitchTrail`;
  `currentTimeMs` on `NoteBars`; `PhaseProfile.trailAlpha`;
  `PitchBallStyle.pulseScaleMin`, `pulseScaleMax`, `pulsePeriodMs` and
  `idlePulsePeriodMs`; `ContourEmphasisStyle.sampleDp`; and on iOS,
  `VoxaVisState.currentAccuracy` and `currentAccuracyFlow`.
- iOS: no build for the simulator on Intel Macs. Compose Multiplatform 1.12
  does not support it, so the XCFramework holds devices and Apple Silicon
  simulators only, and the `iosX64` Kotlin target is gone. An app that builds
  for every simulator architecture (a generic simulator destination, CI, a
  release simulator build) sets `EXCLUDED_ARCHS[sdk=iphonesimulator*] = x86_64`.
- Android: apps must compile against SDK 37 (`compileSdk = 37`). Compose
  Multiplatform 1.12 brings AndroidX lifecycle 2.11, which requires it.
- `MarkerAnchor` has a new case, `NONE`. An exhaustive `when` over it needs a
  branch for it.
- iOS: `NoteAccuracyChartState` replaces `goodColor:` and `poorColor:` with
  `bandColors:`, `bandStops:` and `discreteBands:`.
- iOS: the practice canvas behind `VoxaVisState` (`MainViewController(state:)`)
  now requires `VV.initialize`, like the same canvas on Android and in
  Compose. It used to skip the licence check.
- Swift: initializers that gained parameters must be passed the new
  arguments, since Swift does not see Kotlin's defaults. This includes
  `Segment`, `GridLine`, `SingingPracticeConfig`, `SingingPracticeResources`
  and several `*Style` types. The Swift `create` factories (`Segment.create`,
  `GridLine.create`) keep defaults for the new arguments.
- Binary compatibility: many functions and data classes gained parameters
  with defaults. Kotlin source that compiled against 1.0.0 compiles unchanged
  apart from the items above, but code compiled against 1.0.0 must be
  recompiled.

### Added

- **Takes on one session clock.** `SingingPractice(takes = ...)` accepts a list
  of `TakePlacement`, each putting a stretch of a recording at a point on the
  canvas's clock. A drill can repeat without the screen cutting: the take
  that is ending scrolls off while the next one arrives.
- **Count-in and rhythm lane.** `SingingPractice(leadIn = ...)` draws a count
  (`LeadInMarks`, `LeadInMark`, `LeadInMarkKind`) ahead of the singing, and
  `metricLane = ...` draws the beats of a cycle (`MetricLaneCycle`,
  `BeatRole`) under it. Both can also be set per take.
- **A pitch callout.** `SingingPractice(callout = PitchCalloutData(...))` shows
  how far the sung pitch is from the target.
- **Overlays.** `SingingPractice(pitchOverlay = { ... })` lets you draw your own
  content at a pitch, with `PitchOverlayScope.yFor(cents)` giving the height,
  and `speechContent` shows your content in the gap before a take's count.
- **Clock readers.** `SingingPractice` and `SegmentScrubber` take
  `currentTimeMs: () -> Long`. The canvas calls the reader when it draws, so a
  playing session moves by one transform per frame instead of recomposing.
- **`SegmentScrubber`**, a segment bar you can drag along, with a magnified
  lens under the finger (`SegmentLensStyle`), an optional chip over one
  segment (`SegmentChip`), marks for segments that are queued to come back,
  and a spotlight on one segment.
- **Vertical window for the practice canvas.** `SingingPracticeConfig.viewport`
  takes `PracticeViewport.FitAll` (the default) or
  `PracticeViewport.FollowPhrase`, which sizes the window to the material and
  moves it from phrase to phrase. `PracticeWindowReport` tells you what it
  chose.
- **Vertical window for the scrolling monitor.**
  `ScrollingPitchMonitor(viewport = ...)` takes `PitchViewport.Static` or
  `PitchViewport.Following`, which pans to follow the singer.
- **What the lesson is versus what is shown.**
  `SingingPracticeResources.referenceForm` (`ReferenceForm.Contour`, `Notes`
  or `None`) says what the learner follows, and
  `SingingPracticeConfig.showReference` says whether it is drawn. Hiding the
  reference no longer changes the vertical window or redraws the lesson as
  notes.
- **Held notes are emphasised on the line itself**:
  `PitchContour(emphasisSpans = ...)`, `ContourEmphasisStyle` and
  `TrailEmphasisStyle`.
- `SingingPracticeConfig` gains `restingPitchCents` (where the ball waits
  before the first note), `sessionStarted` (nothing is lit until the session
  starts) and `preferredFrameRate`.
- `ScrollingPitchMonitor(restingPitchCents = ...)`.
- `Segment.previousScore` (last session's score) and `Segment.sungUnscored`
  (sung, but carrying no verdict).
- `GridLine.priority`, with `GridLine.PRIORITY_DEFAULT` and
  `GridLine.PRIORITY_ANCHOR`: decides which labels stay when two would
  overlap.
- `PitchGridStyle.labelAnchor` (`LabelAnchor`) and `labelEdgeInset`.
- `SegmentedSeekBar(layout = ...)` with `SegmentBarLayout`, and
  `MarkerAnchor.NONE` for no playhead marker.
- `LyricsOverlay(leadingLines = ...)`.
- `NoteAccuracyChart(perfectLabel = ...)`, and `NoteAccuracyChartDefaults`
  with `heightFor(rows)` and `rowsThatFit(height)`.
- `rememberNameableLineCount(canvasHeightDp)`: how many grid labels a canvas
  of that height can show without two colliding.
- `PitchBallStyle.idleGlowAlpha`.
- `CircularPitchBuffer.latestBlocking()`.
- `VV.initialize(proxyAuth = ...)`: an async hook for the Authorization
  header, for apps whose login token is fetched asynchronously. Pass it or
  `proxyAuthProvider`, not both.
- iOS:
  - `VV.initialize(proxyEndpoint:debugLogging:proxyAuthProvider:)`,
    `VV.initializeWithAttestation(apiKey:)`, `VV.initializeForServer(apiKey:)`
    and `VV.isInitialized` in Swift, without `.companion`.
  - `SingingPracticeView`, the SwiftUI view for the practice canvas
    (`VoxaVisState`).
  - `VoxaVisState.takes`, `metricLane`, `leadIn` and `callout`.
  - `SegmentScrubberView` and `SegmentScrubberState`, with Swift callbacks
    `onSeekCommitted(_:)` and `onScrubPreview(_:)`.
  - Swift factories with defaults: `SingingPracticeConfig.create`,
    `SingingPracticeResources.create`, `TakePlacement.create`,
    `MetricLaneCycle.create`, `LeadInMark.create` and
    `NoteAccuracyChartState.create`. Swift could not build a
    `SingingPracticeConfig` at all before, since its pitch range is a Kotlin
    range type.

### Changed

- Licensing in proxy mode:
  - Only a 403 from your proxy switches the SDK off. A 401 (for example,
    nobody signed in yet) is treated like a network failure.
  - A device that already holds a token never waits on the network at
    start-up. The token is re-checked, and renewed in its last week, in the
    background. A token fresh from your proxy is still checked before
    `initialize` returns.
- The learner's line is shown only where it was sung, at full strength in
  every phase. It no longer fades out when a take ends.
- The pitch ball no longer pulses while idle; it glows still. Nothing in the
  library animates while nothing is changing.
- `PhaseProfile.listen()` draws the grid at full strength (was 0.15), and
  `PhaseProfile.sing()` draws the reference at 0.6 (was 0.3).
- `NoteBarsStyle` defaults: bars are 16 dp tall (was 8), labels are 14 sp
  bold (were 8 sp), at full opacity (was 0.8), in `labelColor`.
- `NoteAccuracyChart` colours each dot from its score across the style's
  `bands`, and places dots on a ±100 scale (±100 sits on the neighbouring
  note; the edge was ±50).
- `PitchContourData.findPitchCentsAtTime` returns null more than
  `OUT_OF_SPAN_TOLERANCE_MS` (100 ms) before the first point or after the last,
  where it used to return the nearest point.
- iOS: `VoxaVisState.currentTimeMs` is read while the canvas draws, so
  setting it every frame no longer recomposes the canvas.
- Built with Kotlin 2.4.20 (was 2.3.10), Compose Multiplatform 1.12.1 (was
  1.10.1) and, for licensing requests, Ktor 3.5.0 (was 2.3.12). An app on
  Ktor 3 no longer mixes two Ktor major versions.
- Compose runtime, foundation, UI and Material 3 are `api` dependencies, so an
  app that depends on VoxaVis compiles against the Compose types in its API
  without declaring them itself.

### Fixed

- A practice canvas whose clock has not moved yet (a lesson shown before
  Start, or paused) draws its lesson. The teacher's line, note bars and phrases
  fade in over a third of a second on screen frames; they used to fade in on
  the lesson's clock, and stayed invisible while it stood still.
- Text under `VoxaVisTheme` uses Material's type scale (sizes, line heights).
  The theme used to set only fonts and weights, so every label drew at one
  default size. Text in apps using `VoxaVisTheme`, and in every iOS view,
  changes size.
- `NoteAccuracyChart` no longer crashes when its label style leaves the font
  size unset, as the typography `VoxaVisTheme` builds does.
- `LevelMeter` honours its loud and clipping thresholds in the LED style.
- The Android library no longer ships launcher icons, which could replace
  the host app's own.
- The `barPositionRatio` documentation had the direction backwards: it is how
  much of the width lies ahead of the playhead, so 0 puts the line at the
  right edge.

## [1.0.0] - 2026-04-14

Initial public release: practice and monitoring features
(`SingingPractice`, `InstantPitchMonitor`, `ScrollingPitchMonitor`,
`PracticeReview`), pitch primitives and layouts, charts, meters and cards,
`SegmentedSeekBar` and `LyricsOverlay`, `VoxaVisTheme`, and SwiftUI views for
iOS.

[2.0.0]: https://github.com/musicmuni/voxavis/compare/voxavis-v1.0.0...voxavis-v2.0.0
[1.0.0]: https://github.com/musicmuni/voxavis/releases/tag/voxavis-v1.0.0
