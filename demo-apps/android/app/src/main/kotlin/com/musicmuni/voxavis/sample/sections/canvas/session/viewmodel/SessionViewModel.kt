package com.musicmuni.voxavis.sample.sections.canvas.session.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.computePitchRange
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.PitchPoint
import com.musicmuni.voxavis.model.ReferenceForm
import com.musicmuni.voxavis.model.SessionMode
import com.musicmuni.voxavis.model.SingingPracticeResources
import com.musicmuni.voxavis.model.TakePlacement
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.theme.ScoreBandMode
import kotlin.math.abs
import kotlin.random.Random

/** One take as this screen planned it: which phrase, which attempt, and where it sits. */
data class PlannedTake(
    val id: Int,
    val phrase: Int,
    val attempt: Int,
    val placement: TakePlacement,
    /** False for a take cut short by a seek: an answer interrupted is not scored. */
    val scorable: Boolean = true,
)

enum class Stage { Words, Count, Listen, Sing, Over }

/** A score the learner just earned, and when, on the session clock. */
data class Verdict(val phrase: Int, val score: Float, val atMs: Long)

class SessionViewModel : ViewModel() {

    // ── The material (ADR-012): what the recording IS, handed over whole ──

    val lesson: SingingPracticeResources = SingingPracticeResources.create(
        mode = SessionMode.Singafter,
        trackLengthMs = MockData.SESSION_LENGTH_MS,
        segments = MockData.sessionSegments(),
        notes = MockData.sessionNotes(),
        gridLines = MockData.gridLines(),
        referencePitch = MockData.sessionReferencePitch(),
        // The lesson ships a recorded line and a transcription. Only the host
        // knows the line is the thing to follow and the notes are its held
        // stretches, so it says so rather than leaving the canvas to infer it.
        referenceForm = ReferenceForm.Contour,
    )
    val pitchRange = computePitchRange(lesson.notes)
    private val cycle = MockData.adiTala()
    private val countIn = MockData.countIn()
    private val countInMs = -countIn.marks.first().timeMs

    // ── The session clock ──

    /**
     * Milliseconds since the session began. It only moves forward: a phrase
     * sung again is placed further along it, never by taking it back. Written
     * once a frame by [onFrame]; the canvas reads it through a reader.
     */
    val clockMs = mutableLongStateOf(0L)
    var playing by mutableStateOf(false)
    var started by mutableStateOf(false)
        private set

    // ── What is placed on the clock ──

    /** How many times each phrase is sung before the next. Changing it restarts. */
    var attemptsPerPhrase by mutableIntStateOf(1)
        private set
    private var nextId = 0
    var plan by mutableStateOf(planFrom(phrase = 0, entranceMs = 0L))
        private set

    /** What the canvas is handed: the same list until the plan changes. */
    val placements by derivedStateOf { plan.map { it.placement } }

    /** The take being entered or sung: the first one that has not ended. */
    val currentTake by derivedStateOf {
        val now = clockMs.longValue
        plan.firstOrNull { it.placement.anchorMs + it.placement.toMs > now }
    }

    /** Where the ball waits before each take: the note the take is entered on. */
    val entryNoteCents by derivedStateOf {
        currentTake?.let { take -> lesson.notes.first { it.startTimeMs >= take.placement.fromMs }.cents }
    }

    /** What the learner is doing now, worked out from the plan this screen made. */
    val stage by derivedStateOf {
        val take = currentTake ?: return@derivedStateOf Stage.Over
        val now = clockMs.longValue
        val firstNote = take.placement.anchorMs + take.placement.fromMs
        when {
            now < firstNote - countInMs -> Stage.Words
            now < firstNote -> Stage.Count
            (now - take.placement.anchorMs) % MockData.SESSION_PHRASE_MS < MockData.SESSION_CALL_MS -> Stage.Listen
            else -> Stage.Sing
        }
    }

    /** The learner's pitch, stamped on the session clock. Replaced on restart. */
    var performanceBuffer by mutableStateOf(CircularPitchBuffer())
        private set

    // ── Scores, for the phrase bar ──

    var phrases by mutableStateOf(MockData.sessionPhraseSegments())
        private set
    var queued by mutableStateOf(emptySet<Int>())
        private set
    var lastVerdict by mutableStateOf<Verdict?>(null)
        private set

    /** The latest verdict, for as long as its chip stays on the phrase bar. */
    val visibleVerdict by derivedStateOf {
        lastVerdict?.takeIf { clockMs.longValue - it.atMs < CHIP_MS }
    }
    private val scoredTakes = mutableSetOf<Int>()

    // ── View choices ──

    var bandMode by mutableStateOf(ScoreBandMode.Discrete)
    var emphasiseHeldNotes by mutableStateOf(true)
    var scrubPreview by mutableStateOf<Int?>(null)

    fun onFrame(elapsedMs: Long) {
        val now = clockMs.longValue + elapsedMs
        clockMs.longValue = now
        listen(now)
        scoreFinishedAnswers(now)
        if (plan.isEmpty() || now > plan.last().let { it.placement.anchorMs + it.placement.toMs }) {
            playing = false
        }
    }

    fun play() {
        if (currentTake == null) restart()
        started = true
        playing = true
    }

    fun restart() {
        clockMs.longValue = 0L
        performanceBuffer = CircularPitchBuffer()
        phrases = MockData.sessionPhraseSegments()
        queued = emptySet()
        lastVerdict = null
        scoredTakes.clear()
        plan = planFrom(phrase = 0, entranceMs = 0L)
    }

    fun changeAttempts(attempts: Int) {
        attemptsPerPhrase = attempts
        restart()
    }

    /**
     * Practise [phrase] next, from now.
     *
     * Nothing is rewound. The take being sung stops here and carries on
     * scrolling off to the left with what was sung in it, and the chosen phrase
     * is placed straight after, entered through its count.
     */
    fun jumpTo(phrase: Int) {
        val now = clockMs.longValue
        val begun = plan.filter { it.placement.anchorMs + it.placement.fromMs <= now }
        val kept = begun.map { take ->
            val p = take.placement
            if (p.anchorMs + p.toMs <= now) {
                take
            } else {
                take.copy(placement = p.copy(toMs = now - p.anchorMs), scorable = false)
            }
        }
        plan = kept + planFrom(phrase, entranceMs = now)
        started = true
        playing = true
    }

    /**
     * Lays phrases [phrase] onward back to back from [entranceMs]: for each,
     * a few words (except the first, which is entered through its count alone),
     * a four-beat count, then the call and the answer. Each is a slice of the same recording, placed where it will
     * be heard on the session clock.
     */
    private fun planFrom(phrase: Int, entranceMs: Long): List<PlannedTake> {
        val takes = mutableListOf<PlannedTake>()
        var entrance = entranceMs
        for (p in phrase until MockData.SESSION_PHRASE_COUNT) {
            repeat(attemptsPerPhrase) { attempt ->
                val speechMs = if (takes.isEmpty()) 0L else SPEECH_MS
                val fromMs = p * MockData.SESSION_PHRASE_MS
                val toMs = fromMs + MockData.SESSION_PHRASE_MS
                val firstNoteAt = entrance + speechMs + countInMs
                takes += PlannedTake(
                    id = nextId++,
                    phrase = p,
                    attempt = attempt,
                    placement = TakePlacement(
                        // Where this recording's zero sits on the session clock,
                        // so that its fromMs lands on firstNoteAt.
                        anchorMs = firstNoteAt - fromMs,
                        fromMs = fromMs,
                        toMs = toMs,
                        resources = lesson,
                        metricLane = cycle,
                        leadIn = countIn,
                        speechMs = speechMs,
                    ),
                )
                entrance = firstNoteAt + (toMs - fromMs)
            }
        }
        return takes
    }

    /** The playhead on the recording's own clock, held to the current take's stretch. */
    fun lessonTimeMs(): Long {
        val take = currentTake ?: return MockData.SESSION_LENGTH_MS
        val p = take.placement
        return (clockMs.longValue - p.anchorMs).coerceIn(p.fromMs, p.toMs)
    }

    /**
     * The mock microphone. While a take's answer is playing, the learner sings
     * the call back, a little late and off by the amount [MockData] sets for
     * that phrase and attempt. Anywhere else the microphone hears nothing.
     */
    private fun listen(now: Long) {
        val take = plan.firstOrNull { now >= it.placement.anchorMs + it.placement.fromMs && now < it.placement.anchorMs + it.placement.toMs }
        val recordingMs = take?.let { now - it.placement.anchorMs }
        val inAnswer = recordingMs != null &&
            recordingMs % MockData.SESSION_PHRASE_MS >= MockData.SESSION_CALL_MS
        val sung = if (take != null && inAnswer) {
            lesson.referencePitch
                ?.findPitchCentsAtTime(recordingMs - MockData.SESSION_CALL_MS - SINGER_LAG_MS)
                ?.let { it + MockData.sessionSingerOffsetCents(take.phrase, take.attempt) + jitter.nextFloat() * 12f - 6f }
        } else {
            null
        }
        performanceBuffer.addBlocking(
            if (sung == null) PitchPoint.invalid(now) else PitchPoint(now, 261.63f, sung, confidence = 0.9f),
        )
    }

    /**
     * Scores each answer as it ends. A real app scores the take with its pitch
     * evaluator; here the score follows from how far off the mock singer was.
     */
    private fun scoreFinishedAnswers(now: Long) {
        for (take in plan) {
            val answerEnd = take.placement.anchorMs + take.placement.toMs
            if (!take.scorable || take.id in scoredTakes || now < answerEnd) continue
            scoredTakes += take.id
            val offset = MockData.sessionSingerOffsetCents(take.phrase, take.attempt)
            val score = (1f - abs(offset) / 80f).coerceIn(0f, 1f)
            phrases = phrases.mapIndexed { i, segment ->
                if (i == take.phrase) segment.copy(score = score) else segment
            }
            queued = if (score < 0.6f) queued + take.phrase else queued - take.phrase
            lastVerdict = Verdict(take.phrase, score, atMs = now)
        }
    }

    private val jitter = Random(5)

    companion object {
        /** How long the words before a phrase stay up. */
        const val SPEECH_MS = 1500L

        /** How long a verdict's chip stays on the phrase bar. */
        const val CHIP_MS = 2000L

        /** How far behind the teacher the mock learner comes in. */
        private const val SINGER_LAG_MS = 120L
    }
}
