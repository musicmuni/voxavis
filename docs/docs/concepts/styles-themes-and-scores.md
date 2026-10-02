---
sidebar_position: 3
title: Styles, themes and scores
---

# Styles, themes and scores

## Colours come from Material 3

Every component reads `MaterialTheme.colorScheme`. An app with its own
Material 3 theme needs nothing else: VoxaVis takes its colours.

An app without one wraps VoxaVis in `VoxaVisTheme`, which builds a full
Material 3 scheme from five colours:

```kotlin
VoxaVisTheme(
    colors = VoxaVisColors(
        primary = Color(0xFFFFC800),
        secondary = Color(0xFF6EACDA),
        positive = Color(0xFF75C88E),
        negative = Color(0xFFDB7989),
        background = Color(0xFF0C0C0C),
    ),
) {
    SingingPractice(...)
}
```

`VoxaVisPresets.Dark` and `VoxaVisPresets.Light` are ready-made, and
`ColorOverrides` replaces individual Material roles.

## Styles

Each component has a `*Style` data class whose `default()` reads the theme.
Change one thing with `.copy()`:

```kotlin
RadarChart(
    metrics = metrics,
    style = RadarChartStyle.default().copy(webStrokeWidth = 3.dp),
)
```

Start from `default()` rather than calling a style's constructor: new
properties get defaults there, and your code keeps compiling.

## Score colours

Scores are coloured by a `ScoreBands` you choose, not by a palette built into
the library. It lists colours from low score to high:

```kotlin
// Discrete: colors.size - 1 ascending stops in 0..1.
ScoreBands(
    colors = listOf(red, amber, green),
    mode = ScoreBandMode.Discrete,
    stops = listOf(0.6f, 0.8f),
)

// Gradient across 0..1.
ScoreBands(colors = listOf(red, amber, green), mode = ScoreBandMode.Gradient)

// Ready-made red, amber, green gradient.
ScoreBands.trafficLight()
```

Styles that colour scores take one as `bands`:
`SegmentedSeekBarStyle`, `SegmentScrubberStyle` and `NoteAccuracyChartStyle`.
On iOS, `NoteAccuracyChartState.create` takes `bandColors`, `bandStops` and
`discreteBands` instead.

Segment scores run from 0 to 1, with `ScoreThresholds.NOT_PRACTICED` (-1) for
none. `AccuracyData.score` runs from 0 to 100.
