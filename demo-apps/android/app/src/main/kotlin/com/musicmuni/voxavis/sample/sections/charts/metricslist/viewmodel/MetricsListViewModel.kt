package com.musicmuni.voxavis.sample.sections.charts.metricslist.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.Metric
import com.musicmuni.voxavis.sample.shared.MockData

class MetricsListViewModel : ViewModel() {
    val metrics = mutableStateListOf<Metric>().apply { addAll(MockData.voiceMetrics()) }
    var showBestValues by mutableStateOf(true)

    fun randomize() {
        metrics.clear()
        metrics.addAll(MockData.randomMetrics())
    }

    // Style overrides (null = follow theme)
    var customTextColor by mutableStateOf<Color?>(null)
    var customDividerColor by mutableStateOf<Color?>(null)
}
