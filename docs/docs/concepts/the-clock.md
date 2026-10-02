---
sidebar_position: 1
title: The clock
---

# The clock

The practice canvas and the segment scrubber take the playhead as something
they can **read**: `currentTimeMs: () -> Long`. They call it while drawing,
once or twice per displayed frame, and never while composing.

## Why a reader

A value that changes every frame, passed as a parameter, recomposes the
component and everything under it on every frame, and every curve on the
canvas is rebuilt. A reader is called where the drawing happens, so a moving
playhead moves one transform and the curves keep the shapes the renderer has
already built. The difference is the frame budget on a mid-range phone.

## How to write it

Write the player's position into snapshot state once per displayed frame, and
pass a remembered reader over that state.

```kotlin
val positionMs = remember { mutableLongStateOf(0L) }

LaunchedEffect(player) {
    while (true) withFrameMillis { positionMs.longValue = player.positionMs }
}

val clock = remember(positionMs) { { positionMs.longValue } }

SingingPractice(resources = lesson, currentTimeMs = clock)
SegmentScrubber(segments = lesson.segments, totalDurationMs = lesson.trackLengthMs, currentTimeMs = clock)
```

Keep the reader in a `remember` so its identity holds. A new lambda on every
recomposition makes the canvas redraw each time, which is the old cost back.

On iOS, set `VoxaVisState.currentTimeMs` (or `SegmentScrubberState.currentTimeMs`)
once per frame, for example from a `CADisplayLink`. The view reads it the same
way.

## Components that take a `Long`

The monitors, `PracticeReview`, `SegmentedSeekBar`, `LyricsOverlay`,
`ScrollingPitchSpace` and the time-based primitives still take the clock as a
`Long`, and recompose as it moves. `SingingPractice` and `SegmentScrubber`
take only a reader.

## The clock may start again

One canvas can show several sessions in a row, each on a clock from zero,
and a review can seek back: the canvas follows the clock wherever it goes. To
drill a phrase again in a practice session, though, place it again further
along the clock (see [takes](lessons-phases-and-takes.md#takes-on-one-clock))
rather than winding the clock back, so the screen scrolls on instead of
jumping.

## Nothing moves on its own

Nothing in VoxaVis animates while its inputs are still. A paused canvas asks
for no frames. If you want to cap how often a busy screen is drawn, that is a
Compose setting in your app, not a VoxaVis one.
