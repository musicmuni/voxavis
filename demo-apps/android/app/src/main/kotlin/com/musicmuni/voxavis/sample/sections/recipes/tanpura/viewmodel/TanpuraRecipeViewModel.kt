package com.musicmuni.voxavis.sample.sections.recipes.tanpura.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.sample.shared.MockData

class TanpuraRecipeViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    val trackLengthMs = MockData.TOTAL_DURATION_MS

    val gridLines = MockData.gridLines()
    val performanceBuffer = CircularPitchBuffer(capacity = 2000)

    var latestCentsOff by mutableFloatStateOf(0f)
    var latestSwaraLabel by mutableStateOf("Sa")
    var latestConfidence by mutableFloatStateOf(0f)
}
