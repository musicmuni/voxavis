package com.musicmuni.voxavis.sample.sections.charts.vocalrange.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.sample.shared.MockData

class VocalRangeChartViewModel : ViewModel() {
    val range = MockData.vocalRangeData()
    var currentPitchCents by mutableFloatStateOf(1400f)
    var animate by mutableStateOf(true)

    // Style overrides (null = follow theme)
    var customBarColor by mutableStateOf<Color?>(null)
    var customBarWidth by mutableStateOf<Float?>(null)
    var customBarCornerRadius by mutableStateOf<Float?>(null)
}
