package com.musicmuni.voxavis.sample.sections.navigation.lyricsoverlay.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.sample.shared.MockData

class LyricsOverlayViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    var isExpanded by mutableStateOf(false)
    var visibleItemCount by mutableIntStateOf(3)
    val segments = MockData.segments().filter { it.lyrics != null }
    val totalDurationMs = MockData.TOTAL_DURATION_MS

    var selectedSegmentIndex by mutableStateOf(-1)

    // Style overrides (null = follow theme)
    var customActiveColor by mutableStateOf<Color?>(null)
    var customInactiveAlpha by mutableStateOf<Float?>(null)
}
