package com.musicmuni.voxavis.sample.sections.recipes.summary.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.MetricsList
import com.musicmuni.voxavis.NoteAccuracyChart
import com.musicmuni.voxavis.PracticeReview
import com.musicmuni.voxavis.RadarChart
import com.musicmuni.voxavis.model.PracticeReviewResources
import com.musicmuni.voxavis.ScoreCard
import com.musicmuni.voxavis.ScoreTrendChart
import com.musicmuni.voxavis.VocalRangeChart
import com.musicmuni.voxavis.sample.sections.recipes.summary.viewmodel.SummaryRecipeViewModel
import com.musicmuni.voxavis.sample.shared.MockData

@Composable
fun SummaryRecipeView(vm: SummaryRecipeViewModel = viewModel()) {

    LaunchedEffect(vm.playing) {
        if (vm.playing) {
            val start = System.currentTimeMillis()
            val offset = vm.currentTimeMs
            while (true) {
                vm.currentTimeMs = offset + System.currentTimeMillis() - start
                kotlinx.coroutines.delay(16)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        // Top row: Score + Radar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ScoreCard(
                score = 82,
                rating = "Good",
                modifier = Modifier.weight(1f),
            )
            RadarChart(
                metrics = vm.radarMetrics,
                modifier = Modifier.weight(1f),
            )
        }

        // Practice review with dual contour
        Text("Session Review", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 16.dp))
        PracticeReview(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            resources = PracticeReviewResources.create(
                trackLengthMs = vm.trackLengthMs,
                segments = vm.segments,
                notes = vm.notes,
                gridLines = vm.gridLines,
                referencePitch = vm.referencePitch,
                performerPitch = vm.performerPitch,
            ),
            currentTimeMs = vm.currentTimeMs,
        )

        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Button(onClick = { vm.playing = !vm.playing }) {
                Text(if (vm.playing) "Pause" else "Play")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Note accuracy
        Text("Note Accuracy", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 16.dp))
        NoteAccuracyChart(
            notes = vm.noteAccuracy,
            modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 16.dp),
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Metrics + Vocal Range
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricsList(
                metrics = vm.voiceMetrics,
                modifier = Modifier.weight(1f),
            )
            VocalRangeChart(
                range = vm.vocalRange,
                modifier = Modifier.weight(1f),
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Score trend
        Text("Score Trend", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 16.dp))
        ScoreTrendChart(
            dataPoints = vm.scoreTrend,
            modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp),
        )
    }
}
