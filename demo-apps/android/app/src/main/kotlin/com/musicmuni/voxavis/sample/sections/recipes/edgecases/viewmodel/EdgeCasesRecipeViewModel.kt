package com.musicmuni.voxavis.sample.sections.recipes.edgecases.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.CircularPitchBuffer
import com.musicmuni.voxavis.sample.shared.MockData

class EdgeCasesRecipeViewModel : ViewModel() {
    var playing by mutableStateOf(true)
    var currentTimeMs by mutableLongStateOf(0L)
    val trackLengthMs = MockData.TOTAL_DURATION_MS

    // Tiny buffer for edge case demo
    val tinyBuffer = CircularPitchBuffer(capacity = 50)
    val normalBuffer = CircularPitchBuffer(capacity = 2000)

    val gridLines = MockData.gridLines()
    val silenceGapContour = MockData.silenceGapContour()
}
