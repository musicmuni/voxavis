package com.musicmuni.voxavis.sample.sections.recipes.karaoke.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.sample.shared.MockData

class KaraokeRecipeViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    val trackLengthMs = MockData.TOTAL_DURATION_MS

    val segments = MockData.segments()
    val notes = MockData.notes()
    val gridLines = MockData.gridLines()
    val referencePitch = MockData.referencePitch()
    val performanceBuffer = CircularPitchBuffer(capacity = 2000)

    var simulatedAccuracy by mutableFloatStateOf(0f)
    var latestCentsOff by mutableFloatStateOf(0f)
    var latestSwaraLabel by mutableStateOf("Sa")
    var latestConfidence by mutableFloatStateOf(0f)
}
