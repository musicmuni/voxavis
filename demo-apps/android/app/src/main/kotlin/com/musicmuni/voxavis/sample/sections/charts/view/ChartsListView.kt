package com.musicmuni.voxavis.sample.sections.charts.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicmuni.voxavis.sample.FeatureCard
import com.musicmuni.voxavis.sample.PaletteAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsListView(onBack: () -> Unit, onFeatureClick: (String) -> Unit) {
    BackHandler(onBack = onBack)
    val features = listOf(
        "ScoreCard" to "Score display with rating",
        "RadarChart" to "Multi-axis voice quality radar",
        "NoteAccuracyChart" to "Per-note accuracy visualization",
        "VocalRangeChart" to "Vocal range with live marker",
        "PitchScatterPlot" to "Pitch contour scatter plot",
        "ScoreTrendChart" to "Session score trend line",
        "MetricsList" to "Formatted voice metrics table",
        "RingMeter" to "Radial breath capacity gauge",
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Charts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { PaletteAction() },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(features) { (name, desc) ->
                FeatureCard(title = name, description = desc, onClick = { onFeatureClick(name) })
            }
        }
    }
}
