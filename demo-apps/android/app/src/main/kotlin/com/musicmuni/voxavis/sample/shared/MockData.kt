package com.musicmuni.voxavis.sample.shared

import com.musicmuni.voxavis.model.AccuracyLevel
import com.musicmuni.voxavis.model.ChartPoint
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.GridLine
import com.musicmuni.voxavis.model.RadarMetric
import com.musicmuni.voxavis.model.ScoreNote
import com.musicmuni.voxavis.model.AccuracyData
import com.musicmuni.voxavis.model.PitchContourData
import com.musicmuni.voxavis.model.PitchPoint
import com.musicmuni.voxavis.model.ScoreThresholds
import com.musicmuni.voxavis.model.Segment
import com.musicmuni.voxavis.model.SegmentType
import com.musicmuni.voxavis.model.RangeData
import com.musicmuni.voxavis.model.Metric
import kotlin.math.sin
import kotlin.random.Random

object MockData {

    // Indian classical swaras in cents (Shuddh scale)
    private val SWARA_CENTS = mapOf(
        "Sa" to 0f, "Re" to 200f, "Ga" to 400f, "Ma" to 500f,
        "Pa" to 700f, "Dha" to 900f, "Ni" to 1100f, "Sa'" to 1200f,
    )

    fun segments(): List<Segment> = listOf(
        // Pair 1: Teacher → Student — Sa Re Ga Ma
        Segment(
            startTimeMs = 0L, endTimeMs = 5000L,
            type = SegmentType.REFERENCE, lyrics = "Sa Re Ga Ma",
            noteCents = listOf(0f, 200f, 400f, 500f),
        ),
        Segment(
            startTimeMs = 5000L, endTimeMs = 10000L,
            type = SegmentType.PERFORMANCE, lyrics = "Sa Re Ga Ma",
            noteCents = listOf(0f, 200f, 400f, 500f),
            score = 0.65f, bestScore = 0.7f,
        ),
        // Pair 2: Teacher → Student — Pa Dha Ni Sa'
        Segment(
            startTimeMs = 10000L, endTimeMs = 15000L,
            type = SegmentType.REFERENCE, lyrics = "Pa Dha Ni Sa'",
            noteCents = listOf(700f, 900f, 1100f, 1200f),
        ),
        Segment(
            startTimeMs = 15000L, endTimeMs = 20000L,
            type = SegmentType.PERFORMANCE, lyrics = "Pa Dha Ni Sa'",
            noteCents = listOf(700f, 900f, 1100f, 1200f),
            score = 0.3f, bestScore = 0.4f,
        ),
        // Pair 3: Teacher → Student — Full aaroha
        Segment(
            startTimeMs = 20000L, endTimeMs = 25000L,
            type = SegmentType.REFERENCE, lyrics = "Aaroha",
            noteCents = listOf(0f, 200f, 400f, 500f, 700f, 900f, 1100f),
        ),
        Segment(
            startTimeMs = 25000L, endTimeMs = 30000L,
            type = SegmentType.PERFORMANCE, lyrics = "Aaroha",
            noteCents = listOf(0f, 200f, 400f, 500f, 700f, 900f, 1100f),
            score = ScoreThresholds.NOT_PRACTICED,
            bestScore = ScoreThresholds.NOT_PRACTICED,
        ),
    )

    const val TOTAL_DURATION_MS = 30000L
    const val SINGALONG_DURATION_MS = 20000L
    const val EXERCISE_DURATION_MS = 20000L

    /**
     * Grid lines for the 7 swaras + upper Sa, spanning the typical practice range.
     */
    fun gridLines(): List<GridLine> = listOf(
        GridLine(cents = 0f, label = "Sa", isHighlighted = true, lineWeight = 2f),
        GridLine(cents = 200f, label = "Re"),
        GridLine(cents = 400f, label = "Ga"),
        GridLine(cents = 500f, label = "Ma"),
        GridLine(cents = 700f, label = "Pa"),
        GridLine(cents = 900f, label = "Dha"),
        GridLine(cents = 1100f, label = "Ni"),
        GridLine(cents = 1200f, label = "Sa'", isHighlighted = true, lineWeight = 2f),
    )

    /**
     * Teacher notes matching the segment structure — aaroha, avaroha, and full scale patterns.
     */
    fun notes(): List<ScoreNote> {
        val notes = mutableListOf<ScoreNote>()

        // Helper to add a pattern of swaras within a segment
        fun addPattern(
            swaras: List<String>, segStart: Long, segEnd: Long, type: SegmentType,
        ) {
            val dur = (segEnd - segStart) / swaras.size
            swaras.forEachIndexed { i, swara ->
                notes += ScoreNote(
                    startTimeMs = segStart + i * dur,
                    endTimeMs = segStart + (i + 1) * dur,
                    cents = SWARA_CENTS[swara]!!,
                    label = swara,
                    segmentType = type,
                )
            }
        }

        val pair1 = listOf("Sa", "Re", "Ga", "Ma")
        val pair2 = listOf("Pa", "Dha", "Ni", "Sa'")
        val aaroha = listOf("Sa", "Re", "Ga", "Ma", "Pa", "Dha", "Ni")

        // Pair 1: Teacher (0-5s) → Student (5-10s) — Sa Re Ga Ma
        addPattern(pair1, 0L, 5000L, SegmentType.REFERENCE)
        addPattern(pair1, 5000L, 10000L, SegmentType.PERFORMANCE)

        // Pair 2: Teacher (10-15s) → Student (15-20s) — Pa Dha Ni Sa'
        addPattern(pair2, 10000L, 15000L, SegmentType.REFERENCE)
        addPattern(pair2, 15000L, 20000L, SegmentType.PERFORMANCE)

        // Pair 3: Teacher (20-25s) → Student (25-30s) — Full aaroha
        addPattern(aaroha, 20000L, 25000L, SegmentType.REFERENCE)
        addPattern(aaroha, 25000L, 30000L, SegmentType.PERFORMANCE)

        return notes
    }

    /**
     * Reference pitch — smooth contour that follows the notes with gentle transitions.
     */
    fun referencePitch(): PitchContourData {
        val refNotes = notes().filter { it.segmentType == SegmentType.REFERENCE }
        return buildContourFromNotes(refNotes, TOTAL_DURATION_MS)
    }

    // ── Singafter performance contours ──

    /** Close-tracking performance — singafter mode. */
    fun performancePitchClose(): PitchContourData {
        val perfNotes = notes().filter { it.segmentType == SegmentType.PERFORMANCE }
        return buildCloseContour(perfNotes, TOTAL_DURATION_MS)
    }

    /** Loose-tracking performance — singafter mode. */
    fun performancePitchLoose(): PitchContourData {
        val perfNotes = notes().filter { it.segmentType == SegmentType.PERFORMANCE }
        return buildLooseContour(perfNotes, TOTAL_DURATION_MS)
    }

    // ── Singalong performance contours ──

    /** Close-tracking performance — singalong mode. */
    fun performancePitchCloseSingalong(): PitchContourData {
        return buildCloseContour(singalongNotes(), SINGALONG_DURATION_MS)
    }

    /** Loose-tracking performance — singalong mode. */
    fun performancePitchLooseSingalong(): PitchContourData {
        return buildLooseContour(singalongNotes(), SINGALONG_DURATION_MS)
    }

    /**
     * Feeds a point from a pre-built contour into a buffer at the given time.
     * Finds the nearest point in the contour and adds it to the buffer,
     * preserving invalid points so gaps are faithfully reproduced.
     */
    suspend fun feedContourToBuffer(
        contour: PitchContourData,
        buffer: CircularPitchBuffer,
        timeMs: Long,
    ) {
        val points = contour.points
        if (points.isEmpty()) {
            buffer.add(PitchPoint.invalid(timeMs))
            return
        }
        // Binary search for nearest point at/after timeMs
        var lo = 0
        var hi = points.size - 1
        while (lo < hi) {
            val mid = (lo + hi) / 2
            if (points[mid].timestampMs < timeMs) lo = mid + 1 else hi = mid
        }
        val point = points.getOrNull(lo)
        if (point == null || kotlin.math.abs(point.timestampMs - timeMs) > 100) {
            buffer.add(PitchPoint.invalid(timeMs))
        } else {
            buffer.add(point)
        }
    }

    /**
     * Simulates real-time performance pitch data into a CircularPitchBuffer.
     * Produces a wandering pitch around a base with jitter and occasional silence gaps.
     * Call this in a LaunchedEffect with 16ms delay between iterations.
     */
    suspend fun simulatePerformancePitch(buffer: CircularPitchBuffer, timeMs: Long) {
        // ~10% chance of silence gap
        if (Random.nextFloat() < 0.10f) {
            buffer.add(PitchPoint.invalid(timeMs))
            return
        }
        // Wandering base pitch: slow sine wave between Sa (0) and Pa (700)
        val baseCents = 350f + sin(timeMs.toDouble() / 4000.0 * Math.PI).toFloat() * 350f
        // Random jitter ±20 cents
        val jitter = (Random.nextFloat() - 0.5f) * 40f
        // Varying confidence: higher when jitter is small, lower when large
        val confidence = (1f - kotlin.math.abs(jitter) / 20f).coerceIn(0.3f, 1f)
        buffer.add(
            timestampMs = timeMs,
            freqHz = 261.63f,
            centsValue = baseCents + jitter,
            confidence = confidence,
        )
    }

    fun noteAccuracyData(): List<AccuracyData> = listOf(
        AccuracyData("Sa", 261.63f, -2f, AccuracyLevel.EXCELLENT),
        AccuracyData("Re", 293.66f, 8f, AccuracyLevel.GOOD),
        AccuracyData("Ga", 329.63f, -15f, AccuracyLevel.FAIR),
        AccuracyData("Ma", 349.23f, 25f, AccuracyLevel.NEEDS_WORK),
        AccuracyData("Pa", 392.00f, 3f, AccuracyLevel.EXCELLENT),
        AccuracyData("Dha", 440.00f, -7f, AccuracyLevel.GOOD),
        AccuracyData("Ni", 493.88f, 12f, AccuracyLevel.FAIR),
    )

    fun scoreTrendData(): List<ChartPoint> = listOf(
        ChartPoint(65f, "65"),
        ChartPoint(72f, "72"),
        ChartPoint(68f, "68"),
        ChartPoint(80f, "80"),
        ChartPoint(85f, "85", isHighlighted = true),
        ChartPoint(78f, "78"),
        ChartPoint(87f, "87", isCurrent = true),
    )

    fun pitchContour(count: Int = 200): PitchContourData {
        val points = (0 until count).map { i ->
            val timeMs = i * 50L
            val baseCents = 600f + (i % 40) * 30f
            val jitter = (sin(i * 0.3) * 50f).toFloat()
            PitchPoint(
                timestampMs = timeMs,
                freqHz = 261.63f,
                cents = baseCents + jitter
            )
        }
        return PitchContourData(points)
    }

    fun spiderMetrics(): List<RadarMetric> = listOf(
        RadarMetric("Pitch", 0.85f, 0.80f),
        RadarMetric("Breath", 0.65f, 0.70f),
        RadarMetric("Range", 0.45f, 0.40f),
        RadarMetric("Agility", 0.72f, 0.68f),
        RadarMetric("Tone", 0.58f),
    )

    fun voiceMetrics(): List<Metric> = listOf(
        Metric("Vocal Range", "0.8", "oct", "1.2"),
        Metric("Breath Control", "18", "sec", "22"),
        Metric("Pitch Accuracy", "85", "%", "90"),
        Metric("Agility", "72", "%"),
    )

    fun vocalRangeData(): RangeData = RangeData(
        lowNoteCents = 200f,
        highNoteCents = 2600f,
        lowNoteLabel = "D3",
        highNoteLabel = "D5",
        octaveSpan = 2.0f,
    )

    fun randomSpiderMetrics(): List<RadarMetric> = listOf(
        RadarMetric("Pitch", Random.nextFloat(), Random.nextFloat() * 0.5f + 0.5f),
        RadarMetric("Breath", Random.nextFloat(), Random.nextFloat() * 0.5f + 0.5f),
        RadarMetric("Range", Random.nextFloat(), Random.nextFloat() * 0.5f + 0.5f),
        RadarMetric("Agility", Random.nextFloat(), Random.nextFloat() * 0.5f + 0.5f),
        RadarMetric("Tone", Random.nextFloat()),
    )

    fun randomAccuracyData(): List<AccuracyData> {
        val notes = listOf("Sa", "Re", "Ga", "Ma", "Pa", "Dha", "Ni")
        val freqs = listOf(261.63f, 293.66f, 329.63f, 349.23f, 392.00f, 440.00f, 493.88f)
        val levels = AccuracyLevel.entries
        return notes.zip(freqs).map { (name, freq) ->
            AccuracyData(name, freq, Random.nextFloat() * 50f - 25f, levels.random())
        }
    }

    fun randomMetrics(): List<Metric> = listOf(
        Metric("Vocal Range", "%.1f".format(Random.nextFloat() * 2), "oct", "1.2"),
        Metric("Breath Control", "${Random.nextInt(10, 25)}", "sec", "22"),
        Metric("Pitch Accuracy", "${Random.nextInt(60, 100)}", "%", "90"),
        Metric("Agility", "${Random.nextInt(50, 100)}", "%"),
    )

    fun animatedValue(timeMs: Long, periodMs: Long, amplitude: Float, offset: Float): Float {
        return (sin(timeMs.toDouble() / periodMs * 2 * Math.PI) * amplitude + offset).toFloat()
    }

    /**
     * Simulate accuracy (0.0–1.0) based on time.
     * Slowly oscillates with some noise for realistic feel.
     */
    fun simulateAccuracy(timeMs: Long): Float {
        val base = 0.5f + sin(timeMs.toDouble() / 3000.0 * Math.PI).toFloat() * 0.35f
        val noise = (Random.nextFloat() - 0.5f) * 0.1f
        return (base + noise).coerceIn(0f, 1f)
    }

    // ── Singalong mode data ──

    /**
     * Singalong segments — all STUDENT (phase engine derives singalong when no TEACHER exists).
     */
    fun singalongSegments(): List<Segment> = listOf(
        Segment(0L, 5000L, SegmentType.PERFORMANCE, lyrics = "Sa Re Ga Ma", score = 0.85f, bestScore = 0.88f),
        Segment(5000L, 10000L, SegmentType.PERFORMANCE, lyrics = "Pa Dha Ni Sa'", score = 0.72f, bestScore = 0.78f),
        Segment(10000L, 15000L, SegmentType.PERFORMANCE, lyrics = "Sa' Ni Dha Pa", score = 0.6f, bestScore = 0.65f),
        Segment(15000L, 20000L, SegmentType.PERFORMANCE, lyrics = "Ma Ga Re Sa"),
    )

    /**
     * Singalong notes — all STUDENT segment type.
     */
    fun singalongNotes(): List<ScoreNote> {
        val patterns = listOf(
            listOf("Sa", "Re", "Ga", "Ma"),
            listOf("Pa", "Dha", "Ni", "Sa'"),
            listOf("Sa'", "Ni", "Dha", "Pa"),
            listOf("Ma", "Ga", "Re", "Sa"),
        )
        val notes = mutableListOf<ScoreNote>()
        patterns.forEachIndexed { seg, swaras ->
            val segStart = seg * 5000L
            swaras.forEachIndexed { i, swara ->
                notes += ScoreNote(
                    startTimeMs = segStart + (i * 1250).toLong(),
                    endTimeMs = segStart + ((i + 1) * 1250).toLong(),
                    cents = SWARA_CENTS[swara]!!,
                    label = swara,
                    segmentType = SegmentType.PERFORMANCE,
                )
            }
        }
        return notes
    }

    /**
     * Reference pitch for singalong — follows singalong notes with slight vibrato.
     * Passed as referencePitch slot so the pace dot can track it.
     */
    fun referencePitchSingalong(): PitchContourData {
        val allNotes = singalongNotes()
        return buildContourFromNotes(allNotes, SINGALONG_DURATION_MS)
    }

    // ── Exercise mode data ──

    /**
     * Exercise segments: all PERFORMANCE for singalong-style exercise.
     */
    fun exerciseSegments(): List<Segment> = listOf(
        Segment(0, 5000, SegmentType.PERFORMANCE, lyrics = "Sa Re Ga Ma"),
        Segment(5000, 10000, SegmentType.PERFORMANCE, lyrics = "Sa Re Ga Ma"),
        Segment(10000, 15000, SegmentType.PERFORMANCE, lyrics = "Pa Dha Ni Sa'"),
        Segment(15000, 20000, SegmentType.PERFORMANCE, lyrics = "Pa Dha Ni Sa'"),
    )

    /**
     * Exercise notes with scores. Covers all 4 segments (0–20s), all PERFORMANCE.
     */
    fun exerciseNotes(): List<ScoreNote> {
        val notes = mutableListOf<ScoreNote>()
        val swaras1 = listOf("Sa", "Re", "Ga", "Ma")
        val swaras2 = listOf("Pa", "Dha", "Ni", "Sa'")

        // Segment 1 (0-5s): Sa Re Ga Ma
        swaras1.forEachIndexed { i, swara ->
            notes += ScoreNote(
                startTimeMs = (i * 1250).toLong(),
                endTimeMs = ((i + 1) * 1250).toLong(),
                cents = SWARA_CENTS[swara]!!,
                label = swara,
                segmentType = SegmentType.PERFORMANCE,
                score = listOf(0.92f, 0.78f, 0.55f, 0.88f)[i],
            )
        }
        // Segment 2 (5-10s): Sa Re Ga Ma (repeat)
        swaras1.forEachIndexed { i, swara ->
            notes += ScoreNote(
                startTimeMs = 5000L + (i * 1250).toLong(),
                endTimeMs = 5000L + ((i + 1) * 1250).toLong(),
                cents = SWARA_CENTS[swara]!!,
                label = swara,
                segmentType = SegmentType.PERFORMANCE,
                score = listOf(0.92f, 0.78f, 0.55f, 0.88f)[i],
            )
        }
        // Segment 3 (10-15s): Pa Dha Ni Sa'
        swaras2.forEachIndexed { i, swara ->
            notes += ScoreNote(
                startTimeMs = 10000L + (i * 1250).toLong(),
                endTimeMs = 10000L + ((i + 1) * 1250).toLong(),
                cents = SWARA_CENTS[swara]!!,
                label = swara,
                segmentType = SegmentType.PERFORMANCE,
                score = listOf(0.85f, 0.62f, 0.91f, 0.74f)[i],
            )
        }
        // Segment 4 (15-20s): Pa Dha Ni Sa' (repeat)
        swaras2.forEachIndexed { i, swara ->
            notes += ScoreNote(
                startTimeMs = 15000L + (i * 1250).toLong(),
                endTimeMs = 15000L + ((i + 1) * 1250).toLong(),
                cents = SWARA_CENTS[swara]!!,
                label = swara,
                segmentType = SegmentType.PERFORMANCE,
                score = listOf(0.85f, 0.62f, 0.91f, 0.74f)[i],
            )
        }
        return notes
    }

    /**
     * Reference pitch for exercise mode — follows exercise notes.
     */
    fun exerciseReferencePitch(): PitchContourData {
        val allNotes = exerciseNotes()
        return buildContourFromNotes(allNotes, EXERCISE_DURATION_MS)
    }

    // ── Commentary helper ──

    /**
     * Same as segments() but with the COMMENTARY gap converted to a real commentary segment.
     */
    fun segmentsWithCommentary(): List<Segment> = segments().map { segment ->
        if (segment.type == SegmentType.COMMENTARY && segment.isGap) {
            segment.copy(isGap = false, lyrics = "Great job! Let's try the avaroha next.")
        } else segment
    }

    // ── Scrolling monitor data ──

    /**
     * Pre-recorded contour that wanders across swaras over 30s.
     * Used by ScrollingPitchMonitor demo.
     */
    fun scrollingMonitorContour(durationMs: Long = TOTAL_DURATION_MS): PitchContourData {
        val points = mutableListOf<PitchPoint>()
        val stepMs = 50L
        var t = 0L
        while (t <= durationMs) {
            // Slow wandering between Sa and Sa' with occasional dips
            val baseCents = 600f + sin(t.toDouble() / 5000.0 * Math.PI).toFloat() * 500f
            val vibrato = sin(t.toDouble() / 180.0 * Math.PI).toFloat() * 8f
            points += PitchPoint(timestampMs = t, freqHz = 261.63f, cents = baseCents + vibrato)
            t += stepMs
        }
        return PitchContourData(points)
    }

    /**
     * Pentatonic grid lines — Sa, Re, Ga, Pa, Dha only.
     */
    fun pentatonicGridLines(): List<GridLine> = listOf(
        GridLine(cents = 0f, label = "Sa", isHighlighted = true, lineWeight = 2f),
        GridLine(cents = 200f, label = "Re"),
        GridLine(cents = 400f, label = "Ga"),
        GridLine(cents = 700f, label = "Pa"),
        GridLine(cents = 900f, label = "Dha"),
    )

    /**
     * Contour with frequent silence/invalid gaps (~30% of points).
     * Used by Edge Cases recipe.
     */
    fun silenceGapContour(durationMs: Long = TOTAL_DURATION_MS): PitchContourData {
        val points = mutableListOf<PitchPoint>()
        val stepMs = 50L
        var t = 0L
        while (t <= durationMs) {
            if (Random.nextFloat() < 0.30f) {
                points += PitchPoint.invalid(t)
            } else {
                val cents = 400f + sin(t.toDouble() / 3000.0 * Math.PI).toFloat() * 300f
                points += PitchPoint(timestampMs = t, freqHz = 261.63f, cents = cents)
            }
            t += stepMs
        }
        return PitchContourData(points)
    }

    /**
     * Returns (centsOff, swaraLabel) for the nearest swara to a given cents value.
     * Used by karaoke/tanpura recipes to feed TuningGauge.
     */
    fun nearestSwaraInfo(cents: Float): Pair<Float, String> {
        val wrapped = ((cents % 1200f) + 1200f) % 1200f
        var bestLabel = "Sa"
        var bestDist = Float.MAX_VALUE
        for ((label, swaraCents) in SWARA_CENTS) {
            if (label == "Sa'") continue
            val dist = kotlin.math.abs(wrapped - swaraCents)
            val distWrapped = kotlin.math.min(dist, 1200f - dist)
            if (distWrapped < bestDist) {
                bestDist = distWrapped
                bestLabel = label
            }
        }
        val swaraCents = SWARA_CENTS[bestLabel]!!
        val diff = wrapped - swaraCents
        val centsOff = if (diff > 600f) diff - 1200f else if (diff < -600f) diff + 1200f else diff
        return centsOff to bestLabel
    }

    // ── Shared contour builders ──

    /** Reference contour: subtle vibrato, follows notes exactly. */
    private fun buildContourFromNotes(allNotes: List<ScoreNote>, durationMs: Long): PitchContourData {
        val points = mutableListOf<PitchPoint>()
        val stepMs = 50L
        var t = 0L
        while (t <= durationMs) {
            val note = allNotes.find { t in it.startTimeMs until it.endTimeMs }
            if (note != null) {
                val vibrato = sin(t.toDouble() / 200.0 * Math.PI).toFloat() * 5f
                points += PitchPoint(timestampMs = t, freqHz = 261.63f, cents = note.cents + vibrato)
            } else {
                points += PitchPoint.invalid(t)
            }
            t += stepMs
        }
        return PitchContourData(points)
    }

    /** Close performance contour: +5¢ offset, subtle vibrato (7¢). Good student. */
    private fun buildCloseContour(perfNotes: List<ScoreNote>, durationMs: Long): PitchContourData {
        val points = mutableListOf<PitchPoint>()
        val stepMs = 50L
        var t = 0L
        while (t <= durationMs) {
            val note = perfNotes.find { t in it.startTimeMs until it.endTimeMs }
            if (note != null) {
                val vibrato = sin(t.toDouble() / 180.0 * Math.PI).toFloat() * 7f
                val offset = 5f
                points += PitchPoint(timestampMs = t, freqHz = 261.63f, cents = note.cents + offset + vibrato)
            } else {
                points += PitchPoint.invalid(t)
            }
            t += stepMs
        }
        return PitchContourData(points)
    }

    /** Loose performance contour: wandering ±30¢, strong vibrato (15¢), late attacks, ~15% dropouts with burst gaps. */
    private fun buildLooseContour(perfNotes: List<ScoreNote>, durationMs: Long): PitchContourData {
        val points = mutableListOf<PitchPoint>()
        val stepMs = 50L
        val rng = Random(42)
        var t = 0L
        var burstGapRemaining = 0 // frames left in current burst gap
        while (t <= durationMs) {
            val lookupT = (t - 100L).coerceAtLeast(0L)
            val note = perfNotes.find { lookupT in it.startTimeMs until it.endTimeMs }
            if (note != null) {
                // Burst gap: emit consecutive invalid frames
                if (burstGapRemaining > 0) {
                    points += PitchPoint.invalid(t)
                    burstGapRemaining--
                } else if (rng.nextFloat() < 0.03f) {
                    // ~3% chance to start a burst gap (3-6 frames = 150-300ms)
                    burstGapRemaining = 3 + rng.nextInt(4)
                    points += PitchPoint.invalid(t)
                    burstGapRemaining--
                } else if (rng.nextFloat() < 0.12f) {
                    // ~12% single-frame dropout
                    points += PitchPoint.invalid(t)
                } else {
                    val wanderingOffset = sin(t.toDouble() / 3000.0 * Math.PI).toFloat() * 30f
                    val vibrato = sin(t.toDouble() / 130.0 * Math.PI).toFloat() * 15f
                    points += PitchPoint(timestampMs = t, freqHz = 261.63f, cents = note.cents + wanderingOffset + vibrato)
                }
            } else {
                points += PitchPoint.invalid(t)
            }
            t += stepMs
        }
        return PitchContourData(points)
    }

    /**
     * Segments with per-segment preroll/postroll demonstrating the phase timing system.
     * Mix of segments with custom timing and default (metered) timing.
     */
    fun segmentsWithTiming(): List<Segment> = listOf(
        Segment(
            startTimeMs = 0L, endTimeMs = 5000L,
            type = SegmentType.REFERENCE, lyrics = "Sa Re Ga Ma",
            score = 0.9f, bestScore = 0.92f,
            noteCents = listOf(0f, 200f, 400f, 500f),
        ),
        Segment(
            startTimeMs = 5000L, endTimeMs = 12000L,
            type = SegmentType.PERFORMANCE, lyrics = "Pa Dha Ni Sa",
            score = 0.65f, bestScore = 0.7f,
            noteCents = listOf(700f, 900f, 1100f, 1200f),
            prerollMs = 800L, postrollMs = 600L, // shorter transitions
        ),
        Segment(
            startTimeMs = 12000L, endTimeMs = 17000L,
            type = SegmentType.REFERENCE, lyrics = "Sa Ni Dha Pa",
            score = 0.3f, bestScore = 0.4f,
            noteCents = listOf(1200f, 1100f, 900f, 700f),
        ),
        Segment(
            startTimeMs = 17000L, endTimeMs = 24000L,
            type = SegmentType.PERFORMANCE, lyrics = "Ma Ga Re Sa",
            noteCents = listOf(500f, 400f, 200f, 0f),
            // prerollMs=0, postrollMs=0 (defaults) — uses session-level durations
        ),
    )

    const val TIMED_DURATION_MS = 24000L
}
