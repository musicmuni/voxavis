package com.musicmuni.voxavis.sample.sections.charts.scorecard.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel

class ScoreCardViewModel : ViewModel() {
    var score by mutableIntStateOf(87)
    var rating by mutableStateOf("EXCELLENT")

    fun updateRating() {
        rating = when {
            score >= 85 -> "EXCELLENT"
            score >= 70 -> "GOOD"
            score >= 50 -> "FAIR"
            else -> "NEEDS WORK"
        }
    }

    // Style overrides (null = follow theme)
    var customScoreColor by mutableStateOf<Color?>(null)
    var customRatingCornerRadius by mutableStateOf<Dp?>(null)
}
