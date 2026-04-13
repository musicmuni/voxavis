package com.musicmuni.voxavis.sample.sections.canvas.minimal.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.PitchContourData
import com.musicmuni.voxavis.model.GridLine
import com.musicmuni.voxavis.model.ScoreNote
import com.musicmuni.voxavis.model.Segment
import com.musicmuni.voxavis.sample.shared.MockData

class MinimalViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    val trackLengthMs = MockData.TOTAL_DURATION_MS

    // Primitive toggles
    var showGrid by mutableStateOf(true)
    var showSegmentBands by mutableStateOf(true)
    var showNoteBars by mutableStateOf(true)
    var showReferenceContour by mutableStateOf(true)
    var showUserTrail by mutableStateOf(true)
    var showPitchBall by mutableStateOf(true)
    var showReferenceLine by mutableStateOf(true)

    // Data
    val gridLines: List<GridLine> = MockData.gridLines()
    val segments: List<Segment> = MockData.segments()
    val notes: List<ScoreNote> = MockData.notes()
    val referencePitch: PitchContourData = MockData.referencePitch()
    val performanceBuffer = CircularPitchBuffer(capacity = 2000)
}
