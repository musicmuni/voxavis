---
sidebar_position: 2
title: Lessons, phases and takes
---

# Lessons, phases and takes

## Resources say what the lesson is; config says what is shown

`SingingPractice` takes two objects that answer different questions.

| Object | Answers | Examples |
| --- | --- | --- |
| `SingingPracticeResources` | What the lesson **is** | mode, length, segments, notes, grid lines, the teacher's line, `referenceForm` |
| `SingingPracticeConfig` | What is **shown**, and how it behaves | pitch range, `showReference`, labels, playhead position, viewport |

Hide something with a config flag, never by leaving a resource out. Passing
`referencePitch = null` to hide the teacher's line tells the canvas the lesson
has no line, and it redraws the lesson differently. Use
`config.showReference = false` instead: the window, grid and phrases stay
exactly where they were.

When a lesson carries both a line and a note list, `referenceForm` says which
one the learner follows: `ReferenceForm.Contour`, `Notes` or `None`. Left out,
it is inferred from the mode and what is present.

## Segments make phases

The lesson's `segments` say who sings when:

| `SegmentType` | Phase | What is drawn |
| --- | --- | --- |
| `REFERENCE` | Listen | The teacher's line; the ball waits |
| `PERFORMANCE` | Sing | The teacher's line as a guide, the learner's line and the ball |
| `COMMENTARY` | Commentary | Your `commentaryContent`, if any |

How each phase looks is a `PhaseProfile` in `SingingPracticeStyle` (`listen`,
`sing`, `commentary`).

**The learner's line is drawn only inside `PERFORMANCE` segments.** The
microphone is open the whole time, but what it hears while the teacher sings
or someone speaks is not the learner singing. A finished answer stays on
screen and scrolls away with its segment. For free singing with no lesson,
pass one `PERFORMANCE` segment covering the track; with no segments at all,
the canvas is listening and draws no singer.

`SessionMode` says how the turns relate: `Singafter` (the learner answers the
teacher), `Singalong` (the learner sings with the teacher) or `Exercise`
(derived from the segment types).

## Takes on one clock

A session's clock only moves forward, while a recording's clock starts again
every time it is played. A `TakePlacement` ties the two: it puts a stretch
(`fromMs` to `toMs`) of a recording (its `resources`) at a point (`anchorMs`)
on the canvas's clock.

```kotlin
val takes = (0 until 3).map { i ->
    TakePlacement(
        anchorMs = countMs + i * (phraseMs + countMs),
        fromMs = 0,
        toMs = phraseMs,
        resources = phrase,
        metricLane = cycle,
        leadIn = count,
    )
}

SingingPractice(resources = phrase, currentTimeMs = clock, takes = takes)
```

The take that is ending scrolls off while the next one arrives, so a drill
repeats without the screen cutting. Without `takes`, the canvas plays
`resources` once from zero.

## Count-in and rhythm lane

- `LeadInMarks` is the count before a take: a list of `LeadInMark`s at
  negative times before it starts, each a beat (`MATRA`), a subdivision
  (`PULSE`) or a numeral (`NUMERAL`, with a `label`).
- `MetricLaneCycle` is the beat cycle drawn along the bottom of the canvas:
  one `BeatRole` per beat (`PRIMARY`, `SECONDARY`, `HOLLOW`, `PLAIN`), the beat
  length and an offset. Roles name how a beat sounds, so a cycle that begins
  on a hollow beat is described as it is.

Both can be given once on `SingingPractice` or per take on `TakePlacement`.

## The vertical window

`config.viewport` decides how much pitch is on screen:

- `PracticeViewport.FitAll` (default) fits the whole lesson.
- `PracticeViewport.FollowPhrase` sizes the window to the material and moves
  it from phrase to phrase. Its `onResolved` reports the window it chose
  (`PracticeWindowReport`).

`config.restingPitchCents` puts the ball on a note (the tonic, say) before
anything is sung, rather than in the middle of the window.
