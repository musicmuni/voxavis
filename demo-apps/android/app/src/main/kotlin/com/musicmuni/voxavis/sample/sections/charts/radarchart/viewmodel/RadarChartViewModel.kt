package com.musicmuni.voxavis.sample.sections.charts.radarchart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.RadarMetric
import com.musicmuni.voxavis.sample.shared.MockData

class RadarChartViewModel : ViewModel() {
    var gridRingCount by mutableIntStateOf(4)
    var showBest by mutableStateOf(true)
    val metrics = mutableStateListOf<RadarMetric>().apply { addAll(MockData.spiderMetrics()) }

    fun randomize() {
        metrics.clear()
        metrics.addAll(MockData.randomSpiderMetrics())
    }

    // Style overrides (null = follow theme)
    var customWebStrokeWidth by mutableStateOf<Dp?>(null)
    var customFillColor by mutableStateOf<Color?>(null)
    var customGridColor by mutableStateOf<Color?>(null)

    var autoAnimate by mutableStateOf(false)
}
