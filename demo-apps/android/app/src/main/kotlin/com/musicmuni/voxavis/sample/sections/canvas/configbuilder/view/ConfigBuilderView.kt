package com.musicmuni.voxavis.sample.sections.canvas.configbuilder.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.SingingPractice
import com.musicmuni.voxavis.features.SingingPracticeStyle
import com.musicmuni.voxavis.model.SessionMode
import com.musicmuni.voxavis.model.SingingPracticeConfig
import com.musicmuni.voxavis.model.SingingPracticeResources
import com.musicmuni.voxavis.sample.sections.canvas.configbuilder.viewmodel.ConfigBuilderViewModel
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.MockData

@Composable
fun ConfigBuilderView(vm: ConfigBuilderViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    val defaultStyle = SingingPracticeStyle.default()
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = SingingPracticeStyle.default()
            DimensionSlider(
                label = "Ball Radius",
                value = vm.customBallRadius ?: defaults.ball.radius.value,
                onValueChange = { vm.customBallRadius = it },
                valueRange = 4f..24f,
            )
            ColorPalette(
                label = "Reference Segment",
                selectedColor = vm.customReferenceSegmentColor ?: defaults.segmentBands.referenceColor,
                onColorSelected = { vm.customReferenceSegmentColor = it },
            )
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    LaunchedEffect(vm.playing) {
        if (vm.playing) {
            val start = System.currentTimeMillis()
            val offset = vm.currentTimeMs
            while (true) {
                vm.currentTimeMs = (offset + System.currentTimeMillis() - start) % vm.totalDurationMs
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

    val style = defaultStyle.copy(
        ball = defaultStyle.ball.copy(
            radius = vm.customBallRadius?.dp ?: defaultStyle.ball.radius,
        ),
        segmentBands = defaultStyle.segmentBands.copy(
            referenceColor = vm.customReferenceSegmentColor ?: defaultStyle.segmentBands.referenceColor,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        SingingPractice(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            resources = SingingPracticeResources.create(
                mode = SessionMode.Singafter,
                trackLengthMs = vm.totalDurationMs,
                segments = if (vm.showSegments) vm.segments else emptyList(),
                notes = if (vm.showNotes) vm.notes else emptyList(),
                gridLines = if (vm.showGridLines) vm.gridLines else emptyList(),
                referencePitch = if (vm.showRefPitch) vm.referencePitch else null,
            ),
            currentTimeMs = vm.currentTimeMs,
            performancePitch = if (vm.showPerformancePitch) vm.performanceBuffer else null,
            config = SingingPracticeConfig.create(
                barPositionRatio = vm.barPositionRatio,
                timePerInchMs = vm.timePerInchMs.toInt(),
                showGridLabels = vm.showGridLabels,
                showSolfegeLabels = vm.showSolfegeLabels,
            ),
            style = style,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.playing = !vm.playing }) {
                    Text(if (vm.playing) "Pause" else "Play")
                }
            }

            HorizontalDivider()
            Text("SingingPractice Parameters", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Bar Position: ${"%.1f".format(vm.barPositionRatio)}", modifier = Modifier.width(150.dp))
                Slider(value = vm.barPositionRatio, onValueChange = { vm.barPositionRatio = it }, valueRange = 0.1f..0.9f, modifier = Modifier.weight(1f))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Time/Inch: ${vm.timePerInchMs.toInt()}ms", modifier = Modifier.width(150.dp))
                Slider(value = vm.timePerInchMs, onValueChange = { vm.timePerInchMs = it }, valueRange = 1000f..10000f, modifier = Modifier.weight(1f))
            }

            HorizontalDivider()
            Text("Data Visibility", style = MaterialTheme.typography.titleSmall)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Notes", modifier = Modifier.weight(1f))
                Switch(checked = vm.showNotes, onCheckedChange = { vm.showNotes = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Segments", modifier = Modifier.weight(1f))
                Switch(checked = vm.showSegments, onCheckedChange = { vm.showSegments = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reference Pitch", modifier = Modifier.weight(1f))
                Switch(checked = vm.showRefPitch, onCheckedChange = { vm.showRefPitch = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Performance Pitch", modifier = Modifier.weight(1f))
                Switch(checked = vm.showPerformancePitch, onCheckedChange = { vm.showPerformancePitch = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Grid Lines", modifier = Modifier.weight(1f))
                Switch(checked = vm.showGridLines, onCheckedChange = { vm.showGridLines = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Grid Labels", modifier = Modifier.weight(1f))
                Switch(checked = vm.showGridLabels, onCheckedChange = { vm.showGridLabels = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Solfege Labels", modifier = Modifier.weight(1f))
                Switch(checked = vm.showSolfegeLabels, onCheckedChange = { vm.showSolfegeLabels = it })
            }
        }
    }
}
