---
sidebar_position: 2
title: Navigation
---

# Navigation

Moving around a lesson. None of these need the licence.

## SegmentScrubber

A bar of the lesson's segments that the learner drags along to seek. Under the
finger a lens spreads the segments apart, so a long lesson stays usable.

```kotlin
SegmentScrubber(
    segments = lesson.segments,
    totalDurationMs = lesson.trackLengthMs,
    currentTimeMs = clock,                 // () -> Long, read while drawing
    style = SegmentScrubberStyle.default(),
    chip = SegmentChip(segmentIndex = 2, label = "82", color = green),
    queuedIndices = setOf(1, 3),           // a dot under segments coming back
    focusedIndex = 2,                      // spotlight the one being worked on
    onScrubPreview = { index, timeMs -> /* name what is under the finger */ },
    onSeekCommitted = { index, timeMs -> player.seekTo(timeMs) },
)
```

Each segment is coloured by its score through the style's `bands`
(see [score colours](../concepts/styles-themes-and-scores.md#score-colours)).
A segment with `sungUnscored = true` is drawn as sung but not judged.
`interactive = false` gives a read-only strip.

iOS: `SegmentScrubberView(state:)` with a `SegmentScrubberState`; set its
`currentTimeMs` once per frame and use `onSeekCommitted(_:)` and
`onScrubPreview(_:)`.

## SegmentedSeekBar

A thin progress bar of segments, tapped to jump.

```kotlin
SegmentedSeekBar(
    segments = lesson.segments,
    totalDurationMs = lesson.trackLengthMs,
    currentTimeMs = positionMs,
    layout = SegmentBarLayout.Temporal, // or Packed: equal cells
    onSegmentTapped = { index, seekedForward -> },
)
```

`MarkerAnchor.NONE` in its style draws no playhead marker.

## LyricsOverlay

The lesson's lyrics, with the line being sung held in place and the block
rolling under it.

```kotlin
LyricsOverlay(
    segments = lesson.segments,
    currentTimeMs = positionMs,
    isExpanded = expanded,
    onExpandToggle = { expanded = !expanded },
    leadingLines = 1,
)
```

Without `leadingLines` it needs a bounded height from its parent.
