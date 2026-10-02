package com.musicmuni.voxavis.sample.sections.canvas.lessonforms.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.computePitchRange
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.PitchPoint
import com.musicmuni.voxavis.model.PracticeWindowReport
import com.musicmuni.voxavis.model.ReferenceForm
import com.musicmuni.voxavis.model.SessionMode
import com.musicmuni.voxavis.model.SingingPracticeResources
import com.musicmuni.voxavis.sample.shared.MockData
import kotlin.random.Random

class LessonFormsViewModel : ViewModel() {

    val clockMs = mutableLongStateOf(0L)
    var playing by mutableStateOf(true)

    // ── The material (resources): what the lesson IS ──

    /** Which of the lesson's two forms is the thing to follow. */
    var form by mutableStateOf(ReferenceForm.Contour)

    /** Whether the grid names are ranked, so the tonic survives a short canvas. */
    var rankedLabels by mutableStateOf(true)

    private val notes = MockData.formsNotes()
    private val segments = MockData.formsSegments()
    private val contour = MockData.formsReferencePitch()

    /**
     * The lesson, with both its forms handed over whatever is shown. The
     * transcription and the line are the material; [form] says which one the
     * learner follows.
     */
    val resources by derivedStateOf {
        SingingPracticeResources.create(
            mode = SessionMode.Singalong,
            trackLengthMs = MockData.FORMS_DURATION_MS,
            segments = segments,
            notes = notes,
            gridLines = MockData.formsGridLines(ranked = rankedLabels),
            referencePitch = contour,
            referenceForm = form,
        )
    }

    /** Every note of the lesson, with room around it. */
    val bounds = computePitchRange(notes)

    // ── The view (config): what is shown ──

    /** Hides the reference, line or bars, and moves nothing else. */
    var showReference by mutableStateOf(true)

    /** A window that slides to each phrase, instead of the whole lesson at once. */
    var followPhrase by mutableStateOf(false)

    var tallCanvas by mutableStateOf(true)

    /** What the follow-phrase window worked out to, reported once per lesson. */
    var windowReport by mutableStateOf<PracticeWindowReport?>(null)

    val performanceBuffer = CircularPitchBuffer()
    private val jitter = Random(9)

    /**
     * Moves the clock and lets the mock learner sing along, a touch sharp and
     * a little behind the line. The lesson loops: a clock that starts again is
     * an ordinary input, and the learner's buffer clears itself when it does.
     */
    fun onFrame(elapsedMs: Long) {
        val now = (clockMs.longValue + elapsedMs) % MockData.FORMS_DURATION_MS
        clockMs.longValue = now
        val sung = contour.findPitchCentsAtTime(now - 100L)?.let { it + 10f + jitter.nextFloat() * 10f - 5f }
        performanceBuffer.addBlocking(
            if (sung == null) PitchPoint.invalid(now) else PitchPoint(now, 261.63f, sung, confidence = 0.9f),
        )
    }
}
