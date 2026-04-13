package com.musicmuni.voxavis.sample.sections.charts.scoretrend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.ChartPoint
import com.musicmuni.voxavis.sample.shared.MockData
import kotlin.random.Random

class ScoreTrendChartViewModel : ViewModel() {
    val dataPoints = mutableStateListOf<ChartPoint>().apply { addAll(MockData.scoreTrendData()) }
    var bezierCurve by mutableStateOf(true)
    var showGrid by mutableStateOf(true)
    var animate by mutableStateOf(true)

    fun addPoint() {
        val score = Random.nextInt(50, 100).toFloat()
        dataPoints.add(ChartPoint(score, score.toInt().toString()))
    }

    fun reset() {
        dataPoints.clear()
        dataPoints.addAll(MockData.scoreTrendData())
    }

    // Style overrides (null = follow theme)
    var customLineColor by mutableStateOf<Color?>(null)
    var customLineStrokeWidth by mutableStateOf<Dp?>(null)
    var customNormalPointRadius by mutableStateOf<Dp?>(null)
}
