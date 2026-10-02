---
sidebar_position: 2
title: Android quick start
---

# Android quick start

A practice screen: the teacher's line, the learner's live pitch and a playhead
your player drives.

## 1. Initialize once

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        VV.initialize(
            proxyEndpoint = "https://your-server.com/voxatrace/register",
            context = this,
        )
    }
}
```

See [Licensing](../guides/licensing.md) for what the endpoint does and the
other ways to initialize.

## 2. Describe the lesson

```kotlin
val lesson = SingingPracticeResources.create(
    mode = SessionMode.Singafter,
    trackLengthMs = 30_000L,
    segments = listOf(
        Segment(startTimeMs = 0, endTimeMs = 4_000, type = SegmentType.REFERENCE, lyrics = "Sa Re Ga"),
        Segment(startTimeMs = 4_000, endTimeMs = 8_000, type = SegmentType.PERFORMANCE, lyrics = "Sa Re Ga"),
    ),
    referencePitch = PitchContourData(teacherPoints), // List<PitchPoint>, in cents
    gridLines = listOf(
        GridLine(0f, "Sa", isHighlighted = true, priority = GridLine.PRIORITY_ANCHOR),
        GridLine(200f, "Re"),
        GridLine(400f, "Ga"),
    ),
)
```

`REFERENCE` segments are the teacher's turns, `PERFORMANCE` segments the
learner's. The learner's line is drawn only inside `PERFORMANCE` segments.

## 3. Feed the singer and the clock

```kotlin
val pitchBuffer = remember { CircularPitchBuffer() }
val positionMs = remember { mutableLongStateOf(0L) }

// From your pitch detector's callback:
pitchBuffer.addBlocking(PitchPoint(timestampMs = nowMs, freqHz = hz, cents = cents))

// Once per displayed frame, from your player:
LaunchedEffect(player) {
    while (true) withFrameMillis { positionMs.longValue = player.positionMs }
}
```

Timestamps on the pitch points use the same clock as the playhead.

## 4. Draw

```kotlin
val clock = remember(positionMs) { { positionMs.longValue } }

SingingPractice(
    resources = lesson,
    currentTimeMs = clock,
    performancePitch = pitchBuffer,
    config = SingingPracticeConfig.create(pitchRange = -300f..1500f),
)
```

The clock is a reader the canvas calls while drawing; see
[The clock](../concepts/the-clock.md).

## Theme

Components read `MaterialTheme.colorScheme`. In an app with its own Material 3
theme there is nothing to do. Otherwise wrap them in `VoxaVisTheme`:

```kotlin
VoxaVisTheme(colors = VoxaVisColors(primary = Color(0xFFFFC800))) {
    SingingPractice(...)
}
```

## Demo app

`demo-apps/android` in the [repository](https://github.com/musicmuni/voxavis)
has a screen for every component.
