package com.musicmuni.voxavis.sample.sections.navigation.segmentedseekbar.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.sample.shared.MockData

class SegmentedSeekBarViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    var lastTappedInfo by mutableStateOf("")
    val segments = MockData.segments()
    val totalDurationMs = MockData.TOTAL_DURATION_MS

    val seekEvents = emptyList<String>().toMutableStateList()

    fun addSeekEvent(event: String) {
        seekEvents.add(0, event)
        if (seekEvents.size > 5) seekEvents.removeRange(5, seekEvents.size)
    }

    // Style overrides (null = follow theme)
    var customMarkerColor by mutableStateOf<Color?>(null)
    var customScoreGoodColor by mutableStateOf<Color?>(null)
}
