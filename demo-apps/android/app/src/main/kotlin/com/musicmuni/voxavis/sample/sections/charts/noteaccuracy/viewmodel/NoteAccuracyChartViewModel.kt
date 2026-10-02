package com.musicmuni.voxavis.sample.sections.charts.noteaccuracy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.AccuracyData
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.theme.ScoreBandMode

class NoteAccuracyChartViewModel : ViewModel() {
    val notes = mutableStateListOf<AccuracyData>().apply { addAll(MockData.noteAccuracyData()) }
    var noteDiameter by mutableFloatStateOf(24f)
    var gridLineCount by mutableIntStateOf(11)

    fun randomize() {
        notes.clear()
        notes.addAll(MockData.randomAccuracyData())
    }

    var autoAnimate by mutableStateOf(false)
    var bandMode by mutableStateOf(ScoreBandMode.Gradient)
}
