package com.musicmuni.voxavis.sample.sections.recipes.karaoke.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.LyricsOverlay
import com.musicmuni.voxavis.ScoreCard
import com.musicmuni.voxavis.SegmentedSeekBar
import com.musicmuni.voxavis.SingingPractice
import com.musicmuni.voxavis.TuningGauge
import com.musicmuni.voxavis.model.SessionMode
import com.musicmuni.voxavis.model.SingingPracticeResources
import com.musicmuni.voxavis.sample.sections.recipes.karaoke.viewmodel.KaraokeRecipeViewModel
import com.musicmuni.voxavis.sample.shared.MockData

@Composable
fun KaraokeRecipeView(vm: KaraokeRecipeViewModel = viewModel()) {

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

    LaunchedEffect(vm.playing) {
        if (vm.playing) {
            while (true) {
                MockData.simulatePerformancePitch(vm.performanceBuffer, vm.currentTimeMs)
                vm.simulatedAccuracy = MockData.simulateAccuracy(vm.currentTimeMs)
                // Extract tuning info from latest pitch
                val latest = vm.performanceBuffer.getLatest()
                if (latest != null && latest.cents > 0f) {
                    val (centsOff, label) = MockData.nearestSwaraInfo(latest.cents)
                    vm.latestCentsOff = centsOff
                    vm.latestSwaraLabel = label
                    vm.latestConfidence = latest.confidence
                }
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
        // Lyrics overlay (collapsed, 1 line)
        LyricsOverlay(
            segments = vm.segments,
            currentTimeMs = vm.currentTimeMs,
            isExpanded = false,
            visibleItemCount = 1,
            modifier = Modifier.fillMaxWidth(),
        )

        // Main singing canvas
        SingingPractice(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            resources = SingingPracticeResources.create(
                mode = SessionMode.Singafter,
                trackLengthMs = vm.trackLengthMs,
                segments = vm.segments,
                notes = vm.notes,
                gridLines = vm.gridLines,
                referencePitch = vm.referencePitch,
            ),
            currentTimeMs = vm.currentTimeMs,
            performancePitch = vm.performanceBuffer,
            accuracy = vm.simulatedAccuracy,
        )

        // Seekbar
        SegmentedSeekBar(
            segments = vm.segments,
            totalDurationMs = vm.trackLengthMs,
            currentTimeMs = vm.currentTimeMs,
            onTimeChanged = { vm.currentTimeMs = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        )

        // Bottom row: Tuning gauge + Score card
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TuningGauge(
                centsOff = vm.latestCentsOff,
                noteLabel = vm.latestSwaraLabel,
                confidence = vm.latestConfidence,
                modifier = Modifier.weight(1f),
            )
            ScoreCard(
                score = (vm.simulatedAccuracy * 100).toInt(),
                rating = when {
                    vm.simulatedAccuracy >= 0.85f -> "Excellent"
                    vm.simulatedAccuracy >= 0.65f -> "Good"
                    vm.simulatedAccuracy >= 0.45f -> "Fair"
                    else -> "Needs Work"
                },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Play/pause control
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(onClick = { vm.playing = !vm.playing }) {
                Text(if (vm.playing) "Pause" else "Play")
            }
        }
    }
}
