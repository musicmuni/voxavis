---
slug: /
sidebar_position: 1
title: Introduction
---

# VoxaVis

VoxaVis draws what your users sing. It is a Compose Multiplatform library for
Android and iOS: a scrolling practice canvas with the teacher's line and the
learner's pitch, live pitch monitors, charts and meters for a session summary,
and segment and lyrics navigation.

It renders what you give it. It does not capture audio, detect pitch or score
singing; pair it with [VoxaTrace](https://voxatrace.ai) or any pitch source and
map its output to VoxaVis's own data types.

## What's included

| Group | Components |
| --- | --- |
| [Features](components/features.md) | `SingingPractice`, `InstantPitchMonitor`, `ScrollingPitchMonitor`, `PracticeReview` |
| [Navigation](components/navigation.md) | `SegmentScrubber`, `SegmentedSeekBar`, `LyricsOverlay` |
| [Charts and meters](components/charts-and-meters.md) | Note accuracy, radar, vocal range, scatter and score trend charts; score cards and metrics lists; ring, tuning, beat, level and confidence meters |
| [Primitives and layouts](components/primitives-and-layouts.md) | `PitchGrid`, `PitchContour`, `PitchBall`, `PitchTrail` and the rest, inside `PitchSpace` or `ScrollingPitchSpace` |

On iOS every component also has a SwiftUI view.

## Where to start

1. [Install](getting-started/installation.md) the library.
2. [Set up licensing](guides/licensing.md): the feature components need it.
3. Follow the [Android](getting-started/android-quickstart.md) or
   [iOS](getting-started/ios-quickstart.md) quick start.
4. Read [the clock](concepts/the-clock.md) before you wire the canvas to a
   player. It is the one thing that decides whether a playing lesson is cheap
   or expensive to draw.

Upgrading from 1.x: [Migrating to 2.0](getting-started/migrating-to-2.md).
