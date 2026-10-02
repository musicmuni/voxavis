---
sidebar_position: 4
title: Primitives and layouts
---

# Primitives and layouts

The pieces the features are built from, for a screen the features do not
cover. A layout provides the coordinate system; primitives placed inside it
draw in it.

## Layouts

| Layout | Coordinates |
| --- | --- |
| `PitchSpace(pitchRange)` | Pitch on the vertical axis only |
| `ScrollingPitchSpace(trackLengthMs, currentTimeMs, pitchRange, ...)` | Time scrolling past a playhead, pitch vertical |

```kotlin
ScrollingPitchSpace(
    trackLengthMs = 30_000L,
    currentTimeMs = positionMs,
    pitchRange = -300f..1500f,
) {
    PitchGrid(gridLines = scaleLines)
    PitchContour(contour = teacherLine, currentTimeMs = positionMs)
    PitchTrail(buffer = pitchBuffer, currentTimeMs = positionMs)
    NowLine()
}
```

## Primitives

| Primitive | Draws |
| --- | --- |
| `PitchGrid` | The scale's lines and their names (`List<GridLine>`; `GridLine.priority` decides which names stay when two would overlap) |
| `PitchContour` | A line from `PitchContourData`; `emphasisSpans` picks out held notes |
| `PitchTrail` | The learner's line from a `CircularPitchBuffer` |
| `PitchBall` | The ball at the current pitch |
| `NoteBars` | Notes as bars with their names |
| `SegmentBands` | Phrase backgrounds |
| `ReferenceLine` | A horizontal line at one pitch, such as the tonic |
| `NowLine` | The playhead |

Primitives take `modifier` as their last parameter.

`ScrollingPitchSpace` and the primitives take the clock as a `Long`, so a
screen built from them recomposes as the clock moves. For a playing lesson,
`SingingPractice` reads the clock while drawing and costs much less per frame
(see [The clock](../concepts/the-clock.md)).
