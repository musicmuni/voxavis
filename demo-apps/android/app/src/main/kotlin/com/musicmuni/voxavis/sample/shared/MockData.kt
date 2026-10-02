package com.musicmuni.voxavis.sample.shared

import com.musicmuni.voxavis.model.BeatRole
import com.musicmuni.voxavis.model.ChartPoint
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.GridLine
import com.musicmuni.voxavis.model.LeadInMark
import com.musicmuni.voxavis.model.LeadInMarkKind
import com.musicmuni.voxavis.model.LeadInMarks
import com.musicmuni.voxavis.model.MetricLaneCycle
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
        AccuracyData("Sa", 261.63f, -2f, score = 95f),
        AccuracyData("Re", 293.66f, 8f, score = 80f),
        AccuracyData("Ga", 329.63f, -15f, score = 55f),
        AccuracyData("Ma", 349.23f, 25f, score = 28f),
        AccuracyData("Pa", 392.00f, 3f, score = 92f),
        AccuracyData("Dha", 440.00f, -7f, score = 82f),
        AccuracyData("Ni", 493.88f, 12f, score = 60f),
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
        return notes.zip(freqs).map { (name, freq) ->
            AccuracyData(name, freq, Random.nextFloat() * 50f - 25f, Random.nextFloat() * 100f)
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

    // ── Sing-after session (Sing-After Session screen) ──
    //
    // A recording in which the teacher sings a phrase (the call) and then
    // leaves the same length of silence for the learner to sing it back (the
    // answer). Three phrases, each one cycle of Adi tala for the call and one
    // for the answer.

    const val SESSION_BEAT_MS = 500L
    const val SESSION_CALL_MS = 8 * SESSION_BEAT_MS
    const val SESSION_PHRASE_MS = 2 * SESSION_CALL_MS

    private val SESSION_CALLS = listOf(
        listOf("Sa", "Re", "Ga", "Ma"),
        listOf("Ga", "Ma", "Pa", "Dha"),
        listOf("Pa", "Dha", "Ni", "Sa'"),
    )

    val SESSION_PHRASE_COUNT: Int get() = SESSION_CALLS.size
    val SESSION_LENGTH_MS: Long get() = SESSION_CALLS.size * SESSION_PHRASE_MS

    /** How long the slide into a note takes, before the note is held. */
    private const val GLIDE_MS = 150L

    /** Adi tala at 120 beats a minute: 8 beats, in sections of 4, 2 and 2. */
    fun adiTala(): MetricLaneCycle = MetricLaneCycle(
        weights = listOf(
            BeatRole.PRIMARY, BeatRole.PLAIN, BeatRole.PLAIN, BeatRole.PLAIN,
            BeatRole.SECONDARY, BeatRole.HOLLOW, BeatRole.SECONDARY, BeatRole.HOLLOW,
        ),
        beatMs = SESSION_BEAT_MS.toFloat(),
        pulsesPerBeat = 2,
        angaStarts = listOf(0, 4, 6),
    )

    /**
     * Four beats counted in, with the half beat between each, timed against the
     * first note of the phrase they open (so every time is negative).
     */
    fun countIn(beats: Int = 4): LeadInMarks = LeadInMarks(
        marks = (beats downTo 1).flatMap { beat ->
            val at = -beat * SESSION_BEAT_MS
            listOf(
                LeadInMark(timeMs = at, kind = LeadInMarkKind.MATRA),
                LeadInMark(timeMs = at + SESSION_BEAT_MS / 2, kind = LeadInMarkKind.PULSE),
            )
        },
    )

    /** The call and the answer of every phrase, on the recording's own clock. */
    fun sessionSegments(): List<Segment> = SESSION_CALLS.flatMapIndexed { i, call ->
        val start = i * SESSION_PHRASE_MS
        val lyrics = call.joinToString(" ")
        val cents = call.map { SWARA_CENTS[it]!! }
        listOf(
            Segment(start, start + SESSION_CALL_MS, SegmentType.REFERENCE, lyrics = lyrics, noteCents = cents),
            Segment(start + SESSION_CALL_MS, start + SESSION_PHRASE_MS, SegmentType.PERFORMANCE, lyrics = lyrics, noteCents = cents),
        )
    }

    /**
     * The held stretch of every note in the calls: the transcription a lesson
     * ships beside its recording. Each note after the first is reached by a
     * slide, so its held stretch starts [GLIDE_MS] into its beat.
     */
    fun sessionNotes(): List<ScoreNote> = SESSION_CALLS.flatMapIndexed { i, call ->
        val start = i * SESSION_PHRASE_MS
        val slot = SESSION_CALL_MS / call.size
        call.mapIndexed { k, swara ->
            ScoreNote(
                startTimeMs = start + k * slot + if (k == 0) 0L else GLIDE_MS,
                endTimeMs = start + (k + 1) * slot,
                cents = SWARA_CENTS[swara]!!,
                label = swara,
                segmentType = SegmentType.REFERENCE,
            )
        }
    }

    /** The teacher's recorded line: the calls, sung, and silence in the answers. */
    fun sessionReferencePitch(): PitchContourData = glidingContour(sessionNotes(), SESSION_LENGTH_MS)

    /**
     * One entry per phrase (call and answer together), for a phrase bar. What
     * each phrase scored last session is its memory; this session has not
     * scored anything yet.
     */
    fun sessionPhraseSegments(): List<Segment> {
        val lastSession = listOf(0.72f, 0.45f, ScoreThresholds.NOT_PRACTICED)
        return SESSION_CALLS.mapIndexed { i, call ->
            Segment(
                startTimeMs = i * SESSION_PHRASE_MS,
                endTimeMs = (i + 1) * SESSION_PHRASE_MS,
                type = SegmentType.PERFORMANCE,
                lyrics = call.joinToString(" "),
                previousScore = lastSession[i],
            )
        }
    }

    /**
     * How far off the mock learner sings each phrase, in cents, on the first
     * attempt and on the repeat. A real app has a singer; this one is told.
     */
    fun sessionSingerOffsetCents(phrase: Int, attempt: Int): Float =
        listOf(listOf(18f, 6f), listOf(55f, 25f), listOf(-30f, -12f))[phrase][attempt.coerceAtMost(1)]

    // ── Lesson forms (Lesson Forms screen) ──
    //
    // A sung-along lesson that spans two octaves, from Pa in the lower octave
    // to Re in the upper one, so a window narrower than the whole of it has
    // somewhere to go. It ships both a recorded line and a transcription.

    const val FORMS_DURATION_MS = 20000L

    private val FORMS_CENTS = mapOf(
        ".Pa" to -500f, ".Dha" to -300f, ".Ni" to -100f,
        "Sa" to 0f, "Re" to 200f, "Ga" to 400f, "Ma" to 500f,
        "Pa" to 700f, "Dha" to 900f, "Ni" to 1100f,
        "Sa'" to 1200f, "Re'" to 1400f,
    )

    private val FORMS_PHRASES = listOf(
        listOf(".Pa", ".Dha", ".Ni", "Sa"),
        listOf("Re", "Ga", "Ma", "Pa"),
        listOf("Dha", "Ni", "Sa'", "Re'"),
        listOf("Sa'", "Pa", "Ga", "Sa"),
    )

    fun formsSegments(): List<Segment> = FORMS_PHRASES.mapIndexed { i, phrase ->
        Segment(
            startTimeMs = i * 5000L,
            endTimeMs = (i + 1) * 5000L,
            type = SegmentType.PERFORMANCE,
            lyrics = phrase.joinToString(" "),
            noteCents = phrase.map { FORMS_CENTS[it]!! },
        )
    }

    fun formsNotes(): List<ScoreNote> = FORMS_PHRASES.flatMapIndexed { i, phrase ->
        phrase.mapIndexed { k, swara ->
            ScoreNote(
                startTimeMs = i * 5000L + k * 1250L + if (k == 0) 0L else GLIDE_MS,
                endTimeMs = i * 5000L + (k + 1) * 1250L,
                cents = FORMS_CENTS[swara]!!,
                label = swara,
                segmentType = SegmentType.PERFORMANCE,
            )
        }
    }

    fun formsReferencePitch(): PitchContourData = glidingContour(formsNotes(), FORMS_DURATION_MS)

    /**
     * One line per swara over the lesson's span. Ranked, the tonic in every
     * octave is an anchor and Pa comes next, so those names are the ones kept
     * when the canvas cannot fit them all. Unranked, every name is equal and
     * the ones higher up the screen win.
     */
    fun formsGridLines(ranked: Boolean): List<GridLine> = FORMS_CENTS.map { (label, cents) ->
        val isSa = label == "Sa" || label == "Sa'"
        GridLine(
            cents = cents,
            label = label,
            isHighlighted = isSa,
            lineWeight = if (isSa) 2f else 1f,
            priority = when {
                !ranked -> GridLine.PRIORITY_DEFAULT
                isSa -> GridLine.PRIORITY_ANCHOR
                label.endsWith("Pa") -> GridLine.PRIORITY_ANCHOR / 2
                else -> GridLine.PRIORITY_DEFAULT
            },
        )
    }

    // ── Phrase bar (SegmentScrubber screen) ──

    /**
     * A lesson's phrases with the silence between them. Roughly half have a
     * score from last session (their memory); none has one from this pass yet.
     * [crowded] makes thirty short ones, narrow enough for the bar's lens.
     */
    fun scrubberPhrases(crowded: Boolean): List<Segment> {
        val rng = Random(if (crowded) 7 else 3)
        val count = if (crowded) 30 else 8
        var t = 0L
        return List(count) { i ->
            val length = if (crowded) 600L + rng.nextLong(1200L) else 1500L + rng.nextLong(3500L)
            val segment = Segment(
                startTimeMs = t,
                endTimeMs = t + length,
                type = SegmentType.PERFORMANCE,
                lyrics = "Phrase ${i + 1}",
                previousScore = if (rng.nextBoolean()) 0.3f + rng.nextFloat() * 0.65f else ScoreThresholds.NOT_PRACTICED,
            )
            t += length + 300L + rng.nextLong(600L)
            segment
        }
    }

    /** What the mock singer scores on phrase [index] in this pass: a spread across every band. */
    fun scrubberScore(index: Int): Float =
        listOf(0.92f, 0.55f, 0.78f, 0.66f, 0.35f, 0.86f, 0.71f, 0.48f, 0.97f, 0.6f)[index % 10]

    /**
     * A sung line through [notes]: each note is reached by a slide from the one
     * before it and held with a little vibrato, and there is silence wherever
     * no note is near. [notes] are the held stretches; the slide fills the gap
     * in front of each one.
     */
    private fun glidingContour(notes: List<ScoreNote>, durationMs: Long): PitchContourData {
        val points = mutableListOf<PitchPoint>()
        val stepMs = 20L
        var t = 0L
        while (t <= durationMs) {
            val next = notes.indexOfFirst { t < it.endTimeMs }
            val note = notes.getOrNull(next)
            val previous = notes.getOrNull(next - 1)
            val cents = when {
                note == null -> null
                t >= note.startTimeMs -> note.cents + sin(t.toDouble() / 200.0 * Math.PI).toFloat() * 5f
                previous != null && note.startTimeMs - previous.endTimeMs <= GLIDE_MS && t >= previous.endTimeMs -> {
                    val f = (t - previous.endTimeMs).toFloat() / (note.startTimeMs - previous.endTimeMs)
                    val eased = f * f * (3f - 2f * f)
                    previous.cents + (note.cents - previous.cents) * eased
                }
                else -> null
            }
            points += if (cents == null) {
                PitchPoint.invalid(t)
            } else {
                PitchPoint(timestampMs = t, freqHz = 261.63f, cents = cents)
            }
            t += stepMs
        }
        return PitchContourData(points)
    }
}
