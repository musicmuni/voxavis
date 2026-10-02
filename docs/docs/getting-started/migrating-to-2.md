---
sidebar_position: 4
title: Migrating to 2.0
---

# Migrating to 2.0

VoxaVis 2.0 changes how scores are coloured (a `ScoreBands` you pass, instead
of colours built into each style), replaces `AccuracyData`'s level with a
0 to 100 score, reshapes a few style classes, and moves the practice canvas to
a clock it reads rather than a value it is handed.

The upgrade steps, with before and after code for Kotlin and Swift, are in
[MIGRATION.md](https://github.com/musicmuni/voxavis/blob/main/MIGRATION.md).
Every change is listed in the
[changelog](https://github.com/musicmuni/voxavis/blob/main/CHANGELOG.md).
