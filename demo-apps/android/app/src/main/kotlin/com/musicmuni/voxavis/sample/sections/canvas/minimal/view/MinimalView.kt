package com.musicmuni.voxavis.sample.sections.canvas.minimal.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.NoteBars
import com.musicmuni.voxavis.PitchBall
import com.musicmuni.voxavis.PitchContour
import com.musicmuni.voxavis.PitchGrid
import com.musicmuni.voxavis.PitchTrail
import com.musicmuni.voxavis.ReferenceLine
import com.musicmuni.voxavis.ScrollingPitchSpace
import com.musicmuni.voxavis.SegmentBands
import com.musicmuni.voxavis.computePitchRange
import com.musicmuni.voxavis.primitives.ContourRole
import com.musicmuni.voxavis.sample.sections.canvas.minimal.viewmodel.MinimalViewModel
import com.musicmuni.voxavis.sample.shared.MockData

@Composable
fun MinimalView(vm: MinimalViewModel = viewModel()) {

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
                kotlinx.coroutines.delay(16)
            }
        }
    }

    val pitchRange = computePitchRange(vm.notes)

    // Get latest pitch for PitchBall
    var latestPitchCents by remember { mutableStateOf<Float?>(null) }
    var isSinging by remember { mutableStateOf(false) }
    LaunchedEffect(vm.playing) {
        if (vm.playing) {
            while (true) {
                val latest = vm.performanceBuffer.getLatest()
                if (latest != null && latest.cents > 0f) {
                    latestPitchCents = latest.cents
                    isSinging = (vm.currentTimeMs - latest.timestampMs) < 500L
                } else {
                    isSinging = false
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
        // Header explaining what this demo shows
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Text(
                text = "Custom Composition — Uses primitives + layouts directly for full control. Toggle each primitive below.",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // Canvas: ScrollingPitchSpace with togglable primitives
        ScrollingPitchSpace(
            trackLengthMs = vm.trackLengthMs,
            currentTimeMs = vm.currentTimeMs,
            pitchRange = pitchRange,
            modifier = Modifier.fillMaxWidth().height(220.dp),
        ) {
            if (vm.showSegmentBands) {
                SegmentBands(segments = vm.segments)
            }
            if (vm.showGrid) {
                PitchGrid(gridLines = vm.gridLines)
            }
            if (vm.showNoteBars) {
                NoteBars(notes = vm.notes, currentTimeMs = vm.currentTimeMs)
            }
            if (vm.showReferenceContour) {
                PitchContour(
                    contour = vm.referencePitch,
                    currentTimeMs = vm.currentTimeMs,
                    contourRole = ContourRole.GUIDE,
                )
            }
            if (vm.showReferenceLine) {
                ReferenceLine(pitchCents = 0f, label = "Sa")
            }
            if (vm.showUserTrail) {
                PitchTrail(buffer = vm.performanceBuffer, currentTimeMs = vm.currentTimeMs)
            }
            if (vm.showPitchBall) {
                PitchBall(currentPitchCents = latestPitchCents, isSinging = isSinging)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.playing = !vm.playing }) {
                    Text(if (vm.playing) "Pause" else "Play")
                }
            }

            // Time slider
            Text("Time: ${vm.currentTimeMs / 1000}s / ${vm.trackLengthMs / 1000}s")
            Slider(
                value = vm.currentTimeMs.toFloat(),
                onValueChange = { vm.currentTimeMs = it.toLong() },
                valueRange = 0f..vm.trackLengthMs.toFloat(),
            )

            HorizontalDivider()
            Text("Primitives", style = MaterialTheme.typography.labelMedium)

            // Toggle switches for each primitive
            PrimitiveToggle("PitchGrid", vm.showGrid) { vm.showGrid = it }
            PrimitiveToggle("SegmentBands", vm.showSegmentBands) { vm.showSegmentBands = it }
            PrimitiveToggle("NoteBars", vm.showNoteBars) { vm.showNoteBars = it }
            PrimitiveToggle("PitchContour (Reference)", vm.showReferenceContour) { vm.showReferenceContour = it }
            PrimitiveToggle("ReferenceLine", vm.showReferenceLine) { vm.showReferenceLine = it }
            PrimitiveToggle("PitchTrail (User)", vm.showUserTrail) { vm.showUserTrail = it }
            PrimitiveToggle("PitchBall", vm.showPitchBall) { vm.showPitchBall = it }
        }
    }
}

@Composable
private fun PrimitiveToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
