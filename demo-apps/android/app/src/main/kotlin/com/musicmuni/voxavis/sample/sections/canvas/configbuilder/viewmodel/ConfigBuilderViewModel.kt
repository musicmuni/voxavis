package com.musicmuni.voxavis.sample.sections.canvas.configbuilder.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.sample.shared.MockData

class ConfigBuilderViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)

    // SingingPractice direct parameters
    var barPositionRatio by mutableFloatStateOf(0.75f)
    var timePerInchMs by mutableFloatStateOf(3000f)

    // Data visibility toggles (control what data is passed to SingingPractice)
    var showNotes by mutableStateOf(true)
    var showSegments by mutableStateOf(true)
    var showRefPitch by mutableStateOf(true)
    var showPerformancePitch by mutableStateOf(true)
    var showGridLines by mutableStateOf(true)
    var showGridLabels by mutableStateOf(true)
    var showSolfegeLabels by mutableStateOf(true)

    // Data sources
    val segments = MockData.segments()
    val totalDurationMs = MockData.TOTAL_DURATION_MS
    val gridLines = MockData.gridLines()
    val notes = MockData.notes()
    val referencePitch = MockData.referencePitch()
    val performanceBuffer = CircularPitchBuffer(capacity = 2000)

    // Style overrides (null = follow theme)
    var customBallRadius by mutableStateOf<Float?>(null)
    var customReferenceSegmentColor by mutableStateOf<Color?>(null)
}
