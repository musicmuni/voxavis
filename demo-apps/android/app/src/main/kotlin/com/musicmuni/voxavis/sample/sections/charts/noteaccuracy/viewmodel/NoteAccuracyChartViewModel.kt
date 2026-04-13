package com.musicmuni.voxavis.sample.sections.charts.noteaccuracy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.AccuracyData
import com.musicmuni.voxavis.sample.shared.MockData

class NoteAccuracyChartViewModel : ViewModel() {
    val notes = mutableStateListOf<AccuracyData>().apply { addAll(MockData.noteAccuracyData()) }
    var noteDiameter by mutableFloatStateOf(24f)
    var gridLineCount by mutableIntStateOf(11)

    fun randomize() {
        notes.clear()
        notes.addAll(MockData.randomAccuracyData())
    }

    // Style overrides (null = follow theme)
    var customGoodColor by mutableStateOf<Color?>(null)
    var customPoorColor by mutableStateOf<Color?>(null)

    var autoAnimate by mutableStateOf(false)
}
