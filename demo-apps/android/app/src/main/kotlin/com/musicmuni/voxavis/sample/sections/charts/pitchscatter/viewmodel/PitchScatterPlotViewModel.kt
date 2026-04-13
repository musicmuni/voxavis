package com.musicmuni.voxavis.sample.sections.charts.pitchscatter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.sample.shared.MockData

class PitchScatterPlotViewModel : ViewModel() {
    val contour = MockData.pitchContour()
    var pointRadius by mutableFloatStateOf(2f)
    var showConnectingLine by mutableStateOf(true)

    // Style overrides (null = follow theme)
    var customPointColor by mutableStateOf<Color?>(null)
    var customConnectingLineAlpha by mutableStateOf<Float?>(null)
}
