package com.musicmuni.voxavis.sample.sections.canvas.practice.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.BallMode
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.ScoreNote
import com.musicmuni.voxavis.model.PitchContourData
import com.musicmuni.voxavis.model.Segment
import com.musicmuni.voxavis.model.SegmentType
import com.musicmuni.voxavis.model.SessionMode
import com.musicmuni.voxavis.model.SessionPhase
import com.musicmuni.voxavis.model.SingingPracticeResources
import com.musicmuni.voxavis.sample.shared.MockData

enum class PerformanceContourOption { CLOSE, LOOSE }

class PracticeViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)

    // Config toggles (now direct params on SingingPractice)
    var showCommentary by mutableStateOf(false)

    // Session mode
    var sessionMode by mutableStateOf(SessionMode.Singafter)

    /** The canvas's current phase, as its onPhaseStateChanged event reports it. */
    var phase by mutableStateOf(SessionPhase.LISTEN)

    // Phase timing
    var preSingPrepMs by mutableLongStateOf(150L)
    var postSingPrepMs by mutableLongStateOf(150L)

    // Mode-derived data. Derived, so each is built when an input changes rather
    // than on every read: the clock reads some of them every frame.
    val segments: List<Segment> by derivedStateOf {
        val base = when (sessionMode) {
            SessionMode.Singafter -> MockData.segments()
            SessionMode.Singalong -> MockData.singalongSegments()
            SessionMode.Exercise -> MockData.exerciseSegments()
        }
        val selected = if (showCommentary && sessionMode == SessionMode.Singafter) {
            MockData.segmentsWithCommentary()
        } else base
        // Apply per-segment prep overrides to PERFORMANCE segments
        selected.map { seg ->
            if (seg.type == SegmentType.PERFORMANCE) {
                seg.copy(prerollMs = preSingPrepMs, postrollMs = postSingPrepMs)
            } else seg
        }
    }

    val totalDurationMs: Long
        get() = when (sessionMode) {
            SessionMode.Singafter -> MockData.TOTAL_DURATION_MS
            SessionMode.Singalong -> MockData.SINGALONG_DURATION_MS
            SessionMode.Exercise -> MockData.EXERCISE_DURATION_MS
        }

    val notes: List<ScoreNote> by derivedStateOf {
        when (sessionMode) {
            SessionMode.Singafter -> MockData.notes()
            SessionMode.Singalong -> MockData.singalongNotes()
            SessionMode.Exercise -> MockData.exerciseNotes()
        }
    }

    val referencePitch: PitchContourData? by derivedStateOf {
        when (sessionMode) {
            SessionMode.Singafter -> MockData.referencePitch()
            SessionMode.Singalong -> MockData.referencePitchSingalong()
            SessionMode.Exercise -> MockData.exerciseReferencePitch()
        }
    }

    // Performance contour
    var performanceContourOption by mutableStateOf(PerformanceContourOption.CLOSE)

    val performancePitch: PitchContourData by derivedStateOf {
        when (sessionMode) {
            SessionMode.Singafter, SessionMode.Exercise -> when (performanceContourOption) {
                PerformanceContourOption.CLOSE -> MockData.performancePitchClose()
                PerformanceContourOption.LOOSE -> MockData.performancePitchLoose()
            }
            SessionMode.Singalong -> when (performanceContourOption) {
                PerformanceContourOption.CLOSE -> MockData.performancePitchCloseSingalong()
                PerformanceContourOption.LOOSE -> MockData.performancePitchLooseSingalong()
            }
        }
    }

    val gridLines = MockData.gridLines()

    /** The lesson as the canvas takes it: rebuilt when the mode or the timing changes, not per frame. */
    val resources: SingingPracticeResources by derivedStateOf {
        SingingPracticeResources.create(
            mode = sessionMode,
            trackLengthMs = totalDurationMs,
            segments = segments,
            notes = notes,
            gridLines = gridLines,
            referencePitch = referencePitch,
        )
    }
    val performanceBuffer = CircularPitchBuffer(capacity = 2000)

    // Gesture event log
    val gestureEvents = mutableListOf<String>().toMutableStateList()

    fun addGestureEvent(event: String) {
        gestureEvents.add(0, event)
        if (gestureEvents.size > 5) gestureEvents.removeRange(5, gestureEvents.size)
    }

    // Canvas layout
    var barPositionRatio by mutableFloatStateOf(0.75f)
    var timePerInchMs by mutableIntStateOf(3000)
    var minPitchCents by mutableFloatStateOf(-150f)
    var maxPitchCents by mutableFloatStateOf(1350f)

    // Visibility toggles
    var showGridLabels by mutableStateOf(true)
    var showSolfegeLabels by mutableStateOf(true)

    // Additional parameters
    var numLoops by mutableIntStateOf(1)
    var pitchBallSilenceThresholdMs by mutableLongStateOf(500L)

    // === Style overrides (null = follow theme) ===

    // Pitch Ball
    var customBallRadius by mutableStateOf<Float?>(null)
    var customBallActiveColor by mutableStateOf<Color?>(null)
    var customBallIdleColor by mutableStateOf<Color?>(null)
    var customBallWarmAmberColor by mutableStateOf<Color?>(null)
    var customSmoothingStiffness by mutableStateOf<Float?>(null)
    var customReadyStiffness by mutableStateOf<Float?>(null)
    var customIdleGlowAlpha by mutableStateOf<Float?>(null)

    // Performance Trail
    var customPerformancePitchColor by mutableStateOf<Color?>(null)
    var customTrailCoolColor by mutableStateOf<Color?>(null)
    var customTrailDuration by mutableStateOf<Float?>(null)
    var customTrailHeadWidth by mutableStateOf<Float?>(null)
    var customTrailTailWidth by mutableStateOf<Float?>(null)
    var customTrailHeadAlpha by mutableStateOf<Float?>(null)

    // Reference Contour
    var customContourColor by mutableStateOf<Color?>(null)
    var customContourWidth by mutableStateOf<Float?>(null)
    var customGuideBGWidthMultiplier by mutableStateOf<Float?>(null)
    var customGuideBGAlpha by mutableStateOf<Float?>(null)
    var customGuideFGAlpha by mutableStateOf<Float?>(null)
    var customHeadDotRadius by mutableStateOf<Float?>(null)
    var customHeadGlowRadius by mutableStateOf<Float?>(null)
    var customHeadGlowAlpha by mutableStateOf<Float?>(null)
    var customUseConfidence by mutableStateOf<Boolean?>(null)

    // Note Bars
    var customNoteColor by mutableStateOf<Color?>(null)
    var customBarHeight by mutableStateOf<Float?>(null)
    var customBarCornerRadius by mutableStateOf<Float?>(null)
    var customLabelAlpha by mutableStateOf<Float?>(null)
    var customLabelFontSize by mutableStateOf<Float?>(null)

    // Now Line
    var customNowLineColor by mutableStateOf<Color?>(null)
    var customNowLineWidth by mutableStateOf<Float?>(null)
    var customNowLineGlowWidth by mutableStateOf<Float?>(null)
    var customNowLinePeakAlpha by mutableStateOf<Float?>(null)
    var customNowLineGlowAlpha by mutableStateOf<Float?>(null)

    // Score Flash
    var customScoreFlashTextColor by mutableStateOf<Color?>(null)
    var customScoreFlashTextSize by mutableStateOf<Float?>(null)
    var customScoreFlashFadeInMs by mutableStateOf<Int?>(null)
    var customScoreFlashHoldMs by mutableStateOf<Int?>(null)
    var customScoreFlashFadeOutMs by mutableStateOf<Int?>(null)

    // Grid Lines
    var customGridLineColor by mutableStateOf<Color?>(null)
    var customGridHighlightedLineColor by mutableStateOf<Color?>(null)
    var customGridLineWidth by mutableStateOf<Float?>(null)
    var customGridLabelTextColor by mutableStateOf<Color?>(null)
    var customGridLabelTextSize by mutableStateOf<Float?>(null)
    var customGridHighlightedLabelTextSize by mutableStateOf<Float?>(null)
    var customGridHighlightedLabelTextColor by mutableStateOf<Color?>(null)
    var customGridLabelBackgroundColor by mutableStateOf<Color?>(null)

    // Segment Bands
    var customSegmentReferenceColor by mutableStateOf<Color?>(null)
    var customSegmentPerformanceColor by mutableStateOf<Color?>(null)
    var customSegmentCommentaryColor by mutableStateOf<Color?>(null)

    // Phase Ambience: Listen
    var customListenGridAlpha by mutableStateOf<Float?>(null)
    var customListenContentAlpha by mutableStateOf<Float?>(null)
    var customListenReferenceAlpha by mutableStateOf<Float?>(null)
    var customListenBallMode by mutableStateOf<BallMode?>(null)
    var customListenBallSize by mutableStateOf<Float?>(null)

    // Phase Ambience: Sing
    var customSingGridAlpha by mutableStateOf<Float?>(null)
    var customSingContentAlpha by mutableStateOf<Float?>(null)
    var customSingReferenceAlpha by mutableStateOf<Float?>(null)
    var customSingBallMode by mutableStateOf<BallMode?>(null)
    var customSingBallSize by mutableStateOf<Float?>(null)

    // Phase Ambience: Commentary
    var customCommentaryGridAlpha by mutableStateOf<Float?>(null)
    var customCommentaryContentAlpha by mutableStateOf<Float?>(null)
    var customCommentaryReferenceAlpha by mutableStateOf<Float?>(null)
    var customCommentaryBallMode by mutableStateOf<BallMode?>(null)
    var customCommentaryBallSize by mutableStateOf<Float?>(null)
}
