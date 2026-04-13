package com.musicmuni.voxavis.sample.sections.canvas.playback.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.PitchContourData
import com.musicmuni.voxavis.sample.shared.MockData

class PlaybackViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    val segments = MockData.segments()
    val totalDurationMs = MockData.TOTAL_DURATION_MS
    val gridLines = MockData.gridLines()
    val notes = MockData.notes()
    val referencePitch = MockData.referencePitch()

    var showPerformerPitch by mutableStateOf(true)
    var showGridLabels by mutableStateOf(true)
    var showSolfegeLabels by mutableStateOf(true)
    var barPositionRatio by mutableFloatStateOf(0.5f)
    var timePerInchMs by mutableIntStateOf(3000)

    val performerPitch: PitchContourData = MockData.performancePitchClose()

    // Style overrides (null = follow theme)
    var customRefColor by mutableStateOf<Color?>(null)
    var customNoteReferenceColor by mutableStateOf<Color?>(null)
}
