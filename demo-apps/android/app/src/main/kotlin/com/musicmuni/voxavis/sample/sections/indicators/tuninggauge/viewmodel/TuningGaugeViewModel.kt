package com.musicmuni.voxavis.sample.sections.indicators.tuninggauge.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class TuningGaugeViewModel : ViewModel() {
    var centsOff by mutableFloatStateOf(0f)
    var confidence by mutableFloatStateOf(0.8f)
    var noteLabel by mutableStateOf("Sa")
    var animate by mutableStateOf(true)

    var inTuneThreshold by mutableFloatStateOf(10f)
    var gaugeRange by mutableFloatStateOf(50f)

    // Style overrides (null = follow theme)
    var customInTuneColor by mutableStateOf<Color?>(null)
    var customSlightlyOffColor by mutableStateOf<Color?>(null)
    var customVeryOffColor by mutableStateOf<Color?>(null)
    var customNeedleStrokeWidth by mutableStateOf<Float?>(null)
}
