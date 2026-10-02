package com.musicmuni.voxavis.sample.sections.canvas.callout.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.computePitchRange
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.PitchPoint
import com.musicmuni.voxavis.model.SessionMode
import com.musicmuni.voxavis.model.SingingPracticeConfig
import com.musicmuni.voxavis.model.SingingPracticeResources
import com.musicmuni.voxavis.sample.shared.MockData

/** One note's verdict: where it was sung and the line it belongs on. */
data class CalloutExample(val title: String, val label: String, val sungCents: Float, val targetCents: Float)

class CalloutViewModel : ViewModel() {

    val resources = SingingPracticeResources.create(
        mode = SessionMode.Singafter,
        trackLengthMs = MockData.TOTAL_DURATION_MS,
        segments = MockData.segments(),
        notes = MockData.notes(),
        gridLines = MockData.gridLines(),
        referencePitch = MockData.referencePitch(),
    )

    val config = SingingPracticeConfig.create(pitchRange = computePitchRange(resources.notes))

    /** Held at the end of the learner's first answer, where a verdict on it belongs. */
    val clockMs = mutableLongStateOf(ANSWER_END_MS)

    val examples = listOf(
        CalloutExample("Ma, sharp", label = "Ma", sungCents = 560f, targetCents = 500f),
        CalloutExample("Ga, flat", label = "Ga", sungCents = 340f, targetCents = 400f),
        CalloutExample("Sa, on target", label = "Sa", sungCents = 0f, targetCents = 0f),
    )
    var example by mutableStateOf(examples.first())

    /** Bumped to play the same callout again. */
    var replays by mutableIntStateOf(0)

    /** The learner's answer, already sung, so the canvas shows the take the verdict is about. */
    val performanceBuffer = CircularPitchBuffer().apply {
        val sung = MockData.performancePitchClose()
        var t = ANSWER_START_MS
        while (t <= ANSWER_END_MS) {
            addBlocking(sung.findPitchCentsAtTime(t)?.let { PitchPoint(t, 261.63f, it) } ?: PitchPoint.invalid(t))
            t += 16L
        }
    }

    private companion object {
        const val ANSWER_START_MS = 5000L
        const val ANSWER_END_MS = 9900L
    }
}
