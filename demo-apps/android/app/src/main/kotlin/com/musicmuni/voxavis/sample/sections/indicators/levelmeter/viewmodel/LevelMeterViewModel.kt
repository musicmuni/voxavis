package com.musicmuni.voxavis.sample.sections.indicators.levelmeter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.Orientation

class LevelMeterViewModel : ViewModel() {
    var level by mutableFloatStateOf(0.5f)
    var segmentCount by mutableIntStateOf(20)
    var orientation by mutableStateOf(Orientation.Vertical)
    var animate by mutableStateOf(true)

    var loudThreshold by mutableFloatStateOf(0.7f)
    var clippingThreshold by mutableFloatStateOf(0.9f)

    // Style overrides (null = follow theme)
    var customNormalColor by mutableStateOf<Color?>(null)
    var customLoudColor by mutableStateOf<Color?>(null)
    var customClippingColor by mutableStateOf<Color?>(null)
}
