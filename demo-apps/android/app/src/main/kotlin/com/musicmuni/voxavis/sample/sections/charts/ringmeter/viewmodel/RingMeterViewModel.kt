package com.musicmuni.voxavis.sample.sections.charts.ringmeter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class RingMeterViewModel : ViewModel() {
    var value by mutableFloatStateOf(0.7f)
    var animate by mutableStateOf(false)

    // Style overrides (null = follow theme)
    var customRingColor by mutableStateOf<Color?>(null)
    var customRingAlpha by mutableStateOf<Float?>(null)
}
