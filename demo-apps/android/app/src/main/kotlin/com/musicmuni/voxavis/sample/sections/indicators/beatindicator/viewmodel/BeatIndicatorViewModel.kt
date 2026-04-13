package com.musicmuni.voxavis.sample.sections.indicators.beatindicator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class BeatIndicatorViewModel : ViewModel() {
    var currentBeat by mutableIntStateOf(0)
    var beatsPerCycle by mutableIntStateOf(4)
    var running by mutableStateOf(true)

    // Style overrides (null = follow theme)
    var customBeatColor by mutableStateOf<Color?>(null)
    var customDownbeatColor by mutableStateOf<Color?>(null)
    var customSpacingMultiplier by mutableStateOf<Float?>(null)
}
