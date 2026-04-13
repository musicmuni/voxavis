package com.musicmuni.voxavis.sample.sections.indicators.confidencemeter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.Orientation

class ConfidenceMeterViewModel : ViewModel() {
    var confidence by mutableFloatStateOf(0.7f)
    var orientation by mutableStateOf(Orientation.Horizontal)
    var showLabel by mutableStateOf(true)
    var animate by mutableStateOf(true)

    var lowThreshold by mutableFloatStateOf(0.3f)
    var highThreshold by mutableFloatStateOf(0.7f)

    // Style overrides (null = follow theme)
    var customLowColor by mutableStateOf<Color?>(null)
    var customMediumColor by mutableStateOf<Color?>(null)
    var customHighColor by mutableStateOf<Color?>(null)
}
