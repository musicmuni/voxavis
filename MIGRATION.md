# Migrating from 1.x to 2.0

VoxaVis 2.0 changes how scores are coloured, reshapes a few style classes, and
moves the practice canvas to a clock it reads rather than a value it is handed.
Most apps need the changes in sections 1 to 3; the rest apply only if you use
those types directly.

Update the dependency and recompile everything that calls VoxaVis: functions
and data classes gained parameters with defaults, so code compiled against
1.0.0 is not binary compatible even where the source is unchanged.

```kotlin
implementation("com.musicmuni:voxavis:2.0.0")
```

VoxaVis 2.0 is built with Kotlin 2.4.20 and Compose Multiplatform 1.12.1, and
brings Compose with it: an app on an older Compose is moved to 1.12.1.

- **Android:** compile against SDK 37 (`compileSdk = 37`). Compose 1.12 brings
  AndroidX lifecycle 2.11, which refuses an app compiled against less.
- **iOS:** the Intel-Mac simulator is no longer supported. If your app builds
  for every simulator architecture (a generic simulator destination, CI, a
  release simulator build), add `EXCLUDED_ARCHS[sdk=iphonesimulator*] = x86_64`
  to its build settings. Devices and Apple Silicon simulators are unaffected. An Android app needs Kotlin 2.3 or later; a Kotlin
Multiplatform app that builds for iOS should use Kotlin 2.4.20 or later.

The full list of changes is in [CHANGELOG.md](CHANGELOG.md).

## 1. Score colours come from `ScoreBands`

`ScoreColorConfig` is gone. A component that colours scores now takes a
`ScoreBands`: a list of colours from low score to high, and either discrete
stops or a continuous gradient.

```kotlin
import com.musicmuni.voxavis.theme.ScoreBands
import com.musicmuni.voxavis.theme.ScoreBandMode

// Discrete: colors.size - 1 ascending stops in 0..1.
// A score below 0.6 is red, from 0.6 amber, from 0.8 green.
val bands = ScoreBands(
    colors = listOf(red, amber, green),
    mode = ScoreBandMode.Discrete,
    stops = listOf(0.6f, 0.8f),
)

// Gradient: colours spread evenly over 0..1 unless you pass one stop per colour.
val ramp = ScoreBands(colors = listOf(red, amber, green), mode = ScoreBandMode.Gradient)

// Or the ready-made red, amber, green gradient.
val traffic = ScoreBands.trafficLight()
```

### `SegmentedSeekBarStyle`

Before:

```kotlin
SegmentedSeekBarStyle.default().copy(
    scoreGoodColor = green,
    scoreAverageColor = amber,
    scoreBadColor = red,
    scoreGoodFadedColor = green.copy(alpha = 0.4f),
    scoreAverageFadedColor = amber.copy(alpha = 0.4f),
    scoreBadFadedColor = red.copy(alpha = 0.4f),
    scoreThresholdGood = 0.8f,
    scoreThresholdAverage = 0.6f,
)
```

After:

```kotlin
SegmentedSeekBarStyle.default().copy(
    bands = ScoreBands(
        colors = listOf(red, amber, green),
        mode = ScoreBandMode.Discrete,
        stops = listOf(0.6f, 0.8f),
    ),
    fadedAlpha = 0.4f,
)
```

The thresholds become the stops, in the same order as the colours. The three
faded colours become one `fadedAlpha`: a segment with no score this session
but a best score from an earlier one is drawn in its band's colour at that
alpha (default 0.42). If you only used `SegmentedSeekBarStyle.default()`, you
need no change; its default bands match the 1.0.0 defaults.

## 2. `AccuracyData` carries a score

`AccuracyData.deviationRemark: AccuracyLevel` is replaced by `score: Float`,
from 0 to 100, and `AccuracyLevel` is removed. `NoteAccuracyChart` colours
each dot from that score across its style's `bands` instead of switching
between a good and a poor colour.

Before:

```kotlin
AccuracyData(
    label = "Sa",
    targetPitchHz = 261.6f,
    deviationPercent = -12f,
    deviationRemark = AccuracyLevel.GOOD,
)
```

After:

```kotlin
AccuracyData(
    label = "Sa",
    targetPitchHz = 261.6f,
    deviationPercent = -12f,
    score = 82f,
)
```

Pass the score your evaluation produced. If you only kept a level, pick one
representative score per level.

`deviationPercent` is now drawn on a ±100 scale, where ±100 sits on the
neighbouring note. In 1.0.0 the chart's edge was ±50, so the same value now
sits half as far from the centre.

### `NoteAccuracyChartStyle`

`goodColor` and `poorColor` are replaced by `bands`, and
`goodNoteLabelTextStyle` and `poorNoteLabelTextStyle` by the existing
`noteLabelTextStyle`, used on every dot.

Before:

```kotlin
NoteAccuracyChartStyle.default().copy(goodColor = green, poorColor = red)
```

After:

```kotlin
NoteAccuracyChartStyle.default().copy(
    bands = ScoreBands(colors = listOf(red, green), mode = ScoreBandMode.Gradient),
)
```

The default is `ScoreBands.trafficLight()`.

## 3. Give the practice canvas a clock reader

`SingingPractice` and `SegmentScrubber` take the playhead only as
`currentTimeMs: () -> Long`; the `Long` versions are removed. A `Long` that
changes every frame recomposed the canvas and everything under it on every
frame.

Write the clock into snapshot state once per frame, and pass a remembered
reader over it.

Before:

```kotlin
var positionMs by remember { mutableLongStateOf(0L) }
// ... positionMs updated every frame from your player

SingingPractice(
    resources = resources,
    currentTimeMs = positionMs,
    performancePitch = pitchBuffer,
)
```

After:

```kotlin
val positionMs = remember { mutableLongStateOf(0L) }
// ... positionMs.longValue updated every frame from your player

val clock = remember { { positionMs.longValue } }

SingingPractice(
    resources = resources,
    currentTimeMs = clock,
    performancePitch = pitchBuffer,
)
```

The reader is called while drawing, never during composition. Keep it in a
`remember` so its identity holds; a new lambda on every recomposition costs a
redraw each time.

## 4. Style classes you construct directly

If you build styles with `.default()` and `.copy()`, nothing in this section
applies to you.

- `NoteBarsStyle` takes a required `labelColor` as its second parameter.
  Name the arguments after `noteColor`:

  ```kotlin
  NoteBarsStyle(noteColor = blue, labelColor = white, barHeight = 12.dp)
  ```

  The defaults changed too: bars are 16 dp tall (were 8), and labels are
  14 sp bold (were 8 sp) at full opacity. Pass `barHeight`,
  `labelFontSize`, `labelWeight` and `labelAlpha` to keep the 1.0.0 look.
- `SingingPracticeStyle` takes three more required properties: `metricLane`,
  `leadInMarks` and `callout`. Start from `SingingPracticeStyle.default()` and
  `.copy()` the parts you change.

## 5. Exhaustive `when` over `MarkerAnchor`

`MarkerAnchor` has a new case, `NONE` (no playhead marker). Add a branch for
it.

## 6. Parameters that did nothing are gone

These were accepted and not read in late 1.x builds, and are removed. Delete
them from your calls:

- `accuracy` on `SingingPractice`, `PitchBall` and `PitchTrail`.
- `currentTimeMs` on `NoteBars`.
- `PhaseProfile.trailAlpha`: the learner's line is shown where it was sung,
  whatever the phase.
- `PitchBallStyle.pulseScaleMin`, `pulseScaleMax`, `pulsePeriodMs` and
  `idlePulsePeriodMs`: the ball no longer pulses.
- `ContourEmphasisStyle.sampleDp`.
- iOS: `VoxaVisState.currentAccuracy` and `currentAccuracyFlow`.

## 7. Things that look different without a code change

- Under `VoxaVisTheme`, and in every iOS view, text uses Material's type scale:
  labels are smaller and titles larger than the single default size 1.x drew
  them all at. For other sizes, nest a `MaterialTheme` with your own
  `Typography` inside `VoxaVisTheme`.
- The learner's line is shown only where it was sung, at full strength in
  every phase. It no longer fades out between phases.
- The pitch ball no longer pulses while idle; it glows still
  (`PitchBallStyle.idleGlowAlpha`).
- `PhaseProfile.listen()` draws the grid at full strength (was 0.15);
  `PhaseProfile.sing()` draws the reference at 0.6 (was 0.3). Pass your own
  `PhaseProfile` values in `SingingPracticeStyle` to keep the old ones.
- Grid labels that would overlap are dropped by `GridLine.priority`. Give the
  lines that must keep their names `GridLine.PRIORITY_ANCHOR`.
- `PitchContourData.findPitchCentsAtTime` returns null more than 100 ms
  outside the contour, instead of the first or last point.

## 8. Licensing

`VV.initialize(proxyEndpoint, ...)` keeps its signature, and gains `proxyAuth`,
an async alternative to `proxyAuthProvider` for apps whose login token is
fetched asynchronously. Pass one of the two, not both.

What your proxy sees changes slightly:

- Only a 403 from your proxy switches the SDK off. A 401, for example before
  anyone has signed in, is treated like a network failure: `initialize` fails
  if the device holds no token yet, and the next call registers normally.
- A device that already holds a token does not wait on the network at start-up.

## 9. iOS

The practice canvas behind `VoxaVisState` now checks the licence, as it does
on Android: call `VV.initialize` before showing it (see the README). Use the
new `SingingPracticeView(state:)` SwiftUI view instead of wrapping
`MainViewController(state:)` yourself, and set `state.currentTimeMs` once per
displayed frame.

Swift does not see Kotlin's default arguments, so any initializer that gained
a parameter must be passed it. This affects `Segment`, `GridLine`,
`SingingPracticeConfig`, `SingingPracticeResources` and several `*Style`
types when you call their initializers directly. The Swift factories keep
defaults for the new parameters; prefer them:

```swift
let segment = Segment.create(startTimeMs: 0, endTimeMs: 4_000, type: .reference)
let line = GridLine.create(cents: 0, label: "Sa", priority: GridLine.companion.PRIORITY_ANCHOR)
```

`NoteAccuracyChartState` replaces `goodColor:` and `poorColor:` with
`bandColors:` (ARGB, low score to high), `bandStops:` and `discreteBands:`.

Before:

```swift
NoteAccuracyChartState(
    notes: notes, gridLineCount: 11, noteDiameter: 24, noteSpacing: 17,
    flatLabel: "Flat", sharpLabel: "Sharp",
    goodColor: 0xFF4CAF50, poorColor: 0xFFF44336
)
```

After:

```swift
NoteAccuracyChartState.create(
    notes: notes,
    bandColors: [0xFFE5484D, 0xFFF5A623, 0xFF30A46C]
)
```
