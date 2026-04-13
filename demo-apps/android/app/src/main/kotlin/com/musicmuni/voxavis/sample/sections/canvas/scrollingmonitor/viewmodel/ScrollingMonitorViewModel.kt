package com.musicmuni.voxavis.sample.sections.canvas.scrollingmonitor.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.PitchContourData
import com.musicmuni.voxavis.sample.shared.MockData

class ScrollingMonitorViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    val trackLengthMs = MockData.TOTAL_DURATION_MS

    val gridLines = MockData.gridLines()
    val performanceBuffer = CircularPitchBuffer(capacity = 2000)
    val recordedContour: PitchContourData = MockData.scrollingMonitorContour()

    // Toggle between live buffer and recorded contour
    var useRecordedContour by mutableStateOf(false)

    // Configurable parameters
    var barPositionRatio by mutableFloatStateOf(0.3f)
    var timePerInchMs by mutableIntStateOf(3000)
    var showGridLabels by mutableStateOf(true)

    // Style overrides (null = follow theme)
    var customBallRadius by mutableStateOf<Float?>(null)
    var customContourColor by mutableStateOf<Color?>(null)
}
