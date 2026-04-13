package com.musicmuni.voxavis.sample.sections.recipes.edgecases.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.ScrollingPitchMonitor
import com.musicmuni.voxavis.SingingPractice
import com.musicmuni.voxavis.model.SessionMode
import com.musicmuni.voxavis.model.SingingPracticeResources
import com.musicmuni.voxavis.sample.sections.recipes.edgecases.viewmodel.EdgeCasesRecipeViewModel
import com.musicmuni.voxavis.sample.shared.MockData

@Composable
fun EdgeCasesRecipeView(vm: EdgeCasesRecipeViewModel = viewModel()) {

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
                MockData.simulatePerformancePitch(vm.tinyBuffer, vm.currentTimeMs)
                MockData.simulatePerformancePitch(vm.normalBuffer, vm.currentTimeMs)
                kotlinx.coroutines.delay(16)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(modifier = Modifier.padding(bottom = 4.dp)) {
            Button(onClick = { vm.playing = !vm.playing }) {
                Text(if (vm.playing) "Pause" else "Play")
            }
        }

        // Edge Case 1: Empty data
        EdgeCaseCard(title = "Empty Data", description = "All parameters empty/null — should render gracefully") {
            SingingPractice(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                resources = SingingPracticeResources.create(
                    mode = SessionMode.Singafter,
                    trackLengthMs = vm.trackLengthMs,
                ),
                currentTimeMs = vm.currentTimeMs,
            )
        }

        // Edge Case 2: Silence gaps
        EdgeCaseCard(title = "Silence Gaps", description = "Contour with ~30% invalid/silent points") {
            ScrollingPitchMonitor(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                trackLengthMs = vm.trackLengthMs,
                currentTimeMs = vm.currentTimeMs,
                performanceContour = vm.silenceGapContour,
                gridLines = vm.gridLines,
                pitchRange = 0f..1200f,
            )
        }

        // Edge Case 3: Extreme time scales
        EdgeCaseCard(title = "Extreme Zoom", description = "timePerInchMs = 500 (very zoomed in)") {
            ScrollingPitchMonitor(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                trackLengthMs = vm.trackLengthMs,
                currentTimeMs = vm.currentTimeMs,
                performancePitch = vm.normalBuffer,
                gridLines = vm.gridLines,
                pitchRange = 0f..1200f,
                timePerInchMs = 500,
            )
        }

        EdgeCaseCard(title = "Extreme Compress", description = "timePerInchMs = 15000 (very compressed)") {
            ScrollingPitchMonitor(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                trackLengthMs = vm.trackLengthMs,
                currentTimeMs = vm.currentTimeMs,
                performancePitch = vm.normalBuffer,
                gridLines = vm.gridLines,
                pitchRange = 0f..1200f,
                timePerInchMs = 15000,
            )
        }

        // Edge Case 4: Tiny buffer
        EdgeCaseCard(title = "Tiny Buffer (capacity=50)", description = "Short trail compared to normal buffer") {
            ScrollingPitchMonitor(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                trackLengthMs = vm.trackLengthMs,
                currentTimeMs = vm.currentTimeMs,
                performancePitch = vm.tinyBuffer,
                gridLines = vm.gridLines,
                pitchRange = 0f..1200f,
            )
        }
    }
}

@Composable
private fun EdgeCaseCard(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
