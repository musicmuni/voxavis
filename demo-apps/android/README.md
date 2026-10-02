# VoxaVis Android demo

A sample app with one screen per VoxaVis component, each driven by mock data,
so you can see what a component draws and read the code that drives it. Every
screen lives under `app/src/main/kotlin/com/musicmuni/voxavis/sample/sections/`
as a `view/` (the composable) and a `viewmodel/` (its state); the mock data is in
`shared/MockData.kt`.

## Setup

### 1. Licence key

The four feature canvases (`SingingPractice`, `InstantPitchMonitor`,
`ScrollingPitchMonitor`, `PracticeReview`) need VoxaVis initialized with a
licence. Put your key in `local.properties`, next to `settings.gradle.kts`:

```properties
voxavis.apiKey=YOUR_API_KEY
```

`local.properties` is gitignored; never commit it. The build compiles the key
into `BuildConfig.VOXAVIS_API_KEY`, and `VoxaVisLicence.kt` starts the SDK with
it when the app opens.

Without a key the app still builds and runs. The canvas screens and the recipes
show how to set the key up instead of drawing; charts, indicators and navigation
need no licence and work as normal.

The demo initializes with `VV.initializeForServer(apiKey, ...)`, which embeds the
key in the app. That is for a demo only. A production app uses
`VV.initialize(proxyEndpoint, context)`, where your own server holds the key, or
`VV.initializeWithAttestation(apiKey, context, callback = ...)` if it has no backend.
`VoxaVisLicence.kt` shows both.

### 2. VoxaVis version

The VoxaVis release the demo builds against is declared once, in
`gradle.properties`:

```properties
voxavisVersion=2.0.0
```

To build against a local build of VoxaVis published to Maven Local (which
`settings.gradle.kts` searches first), override it on the command line:

```bash
./gradlew assembleDebug -PvoxavisVersion=<version in your Maven Local>
```

### 3. Build and install

```bash
./gradlew installDebug
```

minSdk 24, compileSdk 36.

## How the demo drives the clock

VoxaVis takes the playhead as a reader, `currentTimeMs: () -> Long`, and calls it
while drawing. The demo keeps the clock in snapshot state, advances it once per
displayed frame (`shared/EveryFrame.kt`), and hands the canvas a remembered
reader over it:

```kotlin
EveryFrame(running = vm.playing) { elapsedMs -> vm.onFrame(elapsedMs) }
val clock = remember(vm) { { vm.clockMs.longValue } }

SingingPractice(resources = vm.lesson, currentTimeMs = clock, ...)
```

A new frame then moves the canvas without recomposing it. In a real app the
clock comes from the audio player.

## What each section shows

### Canvas

| Screen | Shows |
| --- | --- |
| Sing-After Session | The teacher sings a phrase, the learner sings it back. Each phrase is a `TakePlacement` on one session clock: entered through a few words (`speechContent`, `speechMs`), a four-beat count (`LeadInMarks`), with Adi tala under it (`MetricLaneCycle`). A `SegmentScrubber` under the canvas shows last session (hollow) and this one (solid) in one `ScoreBands`, with a chip per verdict, a dot on phrases to come back to and the current phrase focused. Tap a phrase to practise it next: the take being sung scrolls off and the chosen one is placed after it. Options: sing each phrase once or twice, held-note emphasis on the teacher's line and the learner's trail, banded or blended score colours. |
| Lesson Forms | One lesson that ships both a recorded line and a transcription. `ReferenceForm` (the material) chooses which one the learner follows; `showReference` (the view) hides it without moving the grid or the window. `PracticeViewport.FitAll` against `FollowPhrase`, and `GridLine.priority` keeping Sa and Pa named on a short canvas. |
| Pitch Callout | `PitchCalloutData`: a sung note travelling to the line it belongs on. The host animates `progress`. |
| Singing Practice | `SingingPractice` with every mode, phase timing, styles and gestures. |
| Instant Pitch Monitor | Open-ended pitch with a grid, tonic and fifth. |
| Scrolling Pitch Monitor | Live pitch scrolling in time, with a `PitchViewport`: the whole range, a fixed window, or a window that follows the singer. |
| Practice Review | Post-session playback of the reference and the learner's line. |
| Custom Composition | A canvas built from primitives inside a layout, without a feature. |
| Config Builder | `SingingPractice` parameters, live. |

### Charts

ScoreCard, RadarChart, NoteAccuracyChart (scores from 0 to 100, coloured by
`ScoreBands` as bands or a blend), VocalRangeChart, PitchScatterPlot,
ScoreTrendChart, MetricsList, RingMeter.

### Indicators

TuningGauge, BeatIndicator, LevelMeter, ConfidenceMeter.

### Navigation

| Screen | Shows |
| --- | --- |
| SegmentScrubber | A scrubbable bar of phrases: memory against this pass, a chip, queued dots, focus, a read-only mode, and on a crowded lesson the lens that spreads phrases under the finger. |
| SegmentedSeekBar | A tappable bar of segments in `Temporal` or `Packed` layout, coloured by `ScoreBands`. |
| LyricsOverlay | Lyrics that follow playback. |

### Recipes

Karaoke Screen, Smart Tanpura, Post-Session Summary and Edge Cases: several
components combined the way an app would use them.

### Theme

The palette button on every screen opens the theme sheet: presets, the five
`VoxaVisColors`, and style controls for the screen you are on.
