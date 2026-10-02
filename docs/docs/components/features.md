---
sidebar_position: 1
title: Features
---

# Features

Complete screens' worth of drawing. All four need the licence
(see [Licensing](../guides/licensing.md)).

## SingingPractice

The practice canvas: the teacher's line or notes, the learner's live pitch as
a line and a ball, phrases, a playhead, and optionally takes, a count-in and a
rhythm lane.

```kotlin
SingingPractice(
    resources = lesson,          // SingingPracticeResources: what the lesson is
    currentTimeMs = clock,       // () -> Long, read while drawing
    performancePitch = buffer,   // CircularPitchBuffer from your pitch detector
    config = SingingPracticeConfig.create(pitchRange = -300f..1500f),
    style = SingingPracticeStyle.default(),
    events = SingingPracticeEvents(onTap = { /* pause */ }),
    takes = takes,               // optional: List<TakePlacement>
    metricLane = cycle,          // optional: MetricLaneCycle
    leadIn = count,              // optional: LeadInMarks
    callout = calloutData,       // optional: PitchCalloutData
)
```

Also: `commentaryContent` (your composable during commentary segments),
`speechContent` (shown in the gap before a take's count), and `pitchOverlay`
(draw your own content at a pitch, with `PitchOverlayScope.yFor(cents)`).

Read [Lessons, phases and takes](../concepts/lessons-phases-and-takes.md) and
[The clock](../concepts/the-clock.md) before wiring it up.

iOS: `SingingPracticeView(state:)` with a `VoxaVisState`.

## InstantPitchMonitor

A live pitch ball over a grid, with no time axis: for tuning and holding a
note.

```kotlin
InstantPitchMonitor(
    currentTimeMs = nowMs,
    performancePitch = buffer,
    gridLines = scaleLines,
    pitchRange = 0f..1200f,
    tonicCents = 0f,
    tonicLabel = "Sa",
)
```

## ScrollingPitchMonitor

The learner's line scrolling past a playhead, with no lesson: for free singing
and warm-ups.

```kotlin
ScrollingPitchMonitor(
    trackLengthMs = 60_000L,
    currentTimeMs = nowMs,
    performancePitch = buffer,
    gridLines = scaleLines,
    pitchRange = -1200f..2400f,
    viewport = PitchViewport.Following(sizeCents = 1400f, initialCenterCents = 600f),
    restingPitchCents = 0f,
)
```

`viewport` is `null` for the whole `pitchRange`, `PitchViewport.Static` for a
fixed window, or `PitchViewport.Following` to pan with the singer.

## PracticeReview

A finished session played back: the teacher's line and the learner's recorded
line on one timeline.

```kotlin
PracticeReview(
    resources = reviewResources, // PracticeReviewResources
    currentTimeMs = playbackMs,
    config = PracticeReviewConfig(),
)
```
