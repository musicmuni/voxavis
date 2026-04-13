package com.musicmuni.voxavis.sample.sections.recipes.summary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.sample.shared.MockData

class SummaryRecipeViewModel : ViewModel() {
    var playing by mutableStateOf(false)
    var currentTimeMs by mutableLongStateOf(0L)
    val trackLengthMs = MockData.TOTAL_DURATION_MS

    val segments = MockData.segments()
    val notes = MockData.notes()
    val gridLines = MockData.gridLines()
    val referencePitch = MockData.referencePitch()
    val performerPitch = MockData.performancePitchClose()
    val noteAccuracy = MockData.noteAccuracyData()
    val radarMetrics = MockData.spiderMetrics()
    val voiceMetrics = MockData.voiceMetrics()
    val vocalRange = MockData.vocalRangeData()
    val scoreTrend = MockData.scoreTrendData()
}
