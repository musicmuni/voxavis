---
sidebar_position: 3
title: Charts and meters
---

# Charts and meters

Standalone components for summaries and live feedback. None of them need the
licence. Each has a `*Style` with `default()`, and on iOS a `*State` and a
SwiftUI `*View` (for example `RingMeterState` and `RingMeterView`).

## Charts

| Component | Shows | Data |
| --- | --- | --- |
| `NoteAccuracyChart` | How flat or sharp each note was, coloured by its score | `List<AccuracyData>` (score 0 to 100) |
| `RadarChart` | Several metrics on one web, with a best | `List<RadarMetric>` |
| `VocalRangeChart` | The singer's range, with a live marker | `RangeData`, `currentPitchCents` |
| `PitchScatterPlot` | A contour as points | `PitchContourData` |
| `ScoreTrendChart` | Scores over sessions | `List<ChartPoint>` |

`NoteAccuracyChartDefaults.heightFor(rows)` and `rowsThatFit(height)` size the
note chart so every row draws at full size.

```kotlin
NoteAccuracyChart(
    notes = listOf(
        AccuracyData(label = "Sa", targetPitchHz = 261.6f, deviationPercent = -4f, score = 92f),
        AccuracyData(label = "Ga", targetPitchHz = 329.6f, deviationPercent = 18f, score = 61f),
    ),
    flatLabel = "Flat",
    sharpLabel = "Sharp",
    perfectLabel = "In tune",
)
```

## Cards

| Component | Shows |
| --- | --- |
| `ScoreCard` | A score out of a maximum, with a rating |
| `MetricsList` | Labelled values with units and bests (`List<Metric>`) |

## Meters

| Component | Shows | Input |
| --- | --- | --- |
| `RingMeter` | A filling ring, for breath length | `value`, `displayText` |
| `TuningGauge` | A needle, cents off a note | `centsOff`, `noteLabel`, `confidence` |
| `BeatIndicator` | The beat of a cycle | `currentBeat`, `beatsPerCycle` |
| `LevelMeter` | Input level with a peak hold | `level` 0 to 1 |
| `ConfidenceMeter` | How sure the pitch detector is | `confidence` 0 to 1 |
