package com.musicmuni.voxavis.sample.sections.canvas.freestyle.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.model.GridLine
import com.musicmuni.voxavis.sample.shared.MockData

class FreestyleViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    var showPitchBall by mutableStateOf(true)
    var useFreestyle by mutableStateOf(false)
    val gridLines = MockData.gridLines()
    val performanceBuffer = CircularPitchBuffer(capacity = 2000)

    // Grid preset: 0=Full Scale, 1=Pentatonic, 2=None
    var gridPreset by mutableIntStateOf(0)

    // Pitch range preset: 0=Full octave, 1=Half octave, 2=Two octaves
    var pitchRangePreset by mutableIntStateOf(0)

    val effectiveGridLines: List<GridLine>
        get() = when (gridPreset) {
            0 -> gridLines
            1 -> MockData.pentatonicGridLines()
            else -> emptyList()
        }

    val effectivePitchRange: ClosedFloatingPointRange<Float>
        get() = when (pitchRangePreset) {
            1 -> 0f..600f
            2 -> 0f..2400f
            else -> 0f..1200f
        }

    var pitchBallSilenceThresholdMs by mutableLongStateOf(500L)

    // Style overrides (null = follow theme)
    var customBallRadius by mutableStateOf<Float?>(null)
    var customPerformancePitchColor by mutableStateOf<Color?>(null)
}
