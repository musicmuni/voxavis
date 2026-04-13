package com.musicmuni.voxavis.sample.sections.canvas.freestyle.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.InstantPitchMonitor
import com.musicmuni.voxavis.features.InstantPitchMonitorStyle
import com.musicmuni.voxavis.sample.sections.canvas.freestyle.viewmodel.FreestyleViewModel
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.MockData

@Composable
fun FreestyleView(vm: FreestyleViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    val defaultStyle = InstantPitchMonitorStyle.default()
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = InstantPitchMonitorStyle.default()
            DimensionSlider(
                label = "Ball Radius",
                value = vm.customBallRadius ?: defaults.ball.radius.value,
                onValueChange = { vm.customBallRadius = it },
                valueRange = 4f..24f,
            )
            ColorPalette(
                label = "Performance Pitch",
                selectedColor = vm.customPerformancePitchColor ?: defaults.trail.warmColor,
                onColorSelected = { vm.customPerformancePitchColor = it },
            )
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

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

    val style = defaultStyle.copy(
        ball = defaultStyle.ball.copy(
            radius = vm.customBallRadius?.dp ?: defaultStyle.ball.radius,
        ),
        trail = defaultStyle.trail.copy(
            warmColor = vm.customPerformancePitchColor ?: defaultStyle.trail.warmColor,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        InstantPitchMonitor(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            currentTimeMs = vm.currentTimeMs,
            performancePitch = vm.performanceBuffer,
            gridLines = vm.effectiveGridLines,
            pitchRange = vm.effectivePitchRange,
            pitchBallSilenceThresholdMs = vm.pitchBallSilenceThresholdMs,
            tonicCents = if (vm.useFreestyle) 0f else null,
            tonicLabel = if (vm.useFreestyle) "Sa" else null,
            fifthCents = if (vm.useFreestyle) 700f else null,
            fifthLabel = if (vm.useFreestyle) "Pa" else null,
            style = style,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.playing = !vm.playing }) {
                    Text(if (vm.playing) "Pause" else "Play")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show Pitch Ball", modifier = Modifier.weight(1f))
                Switch(checked = vm.showPitchBall, onCheckedChange = { vm.showPitchBall = it })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show Reference Lines", modifier = Modifier.weight(1f))
                Switch(checked = vm.useFreestyle, onCheckedChange = { vm.useFreestyle = it })
            }

            // Grid preset
            Text("Grid Lines")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Full Scale" to 0, "Pentatonic" to 1, "None" to 2).forEach { (label, value) ->
                    FilterChip(
                        selected = vm.gridPreset == value,
                        onClick = { vm.gridPreset = value },
                        label = { Text(label) },
                    )
                }
            }

            // Pitch range
            Text("Pitch Range")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Half Oct" to 1, "Full Oct" to 0, "Two Oct" to 2).forEach { (label, value) ->
                    FilterChip(
                        selected = vm.pitchRangePreset == value,
                        onClick = { vm.pitchRangePreset = value },
                        label = { Text(label) },
                    )
                }
            }

            // Silence threshold
            Text("Silence Threshold: ${vm.pitchBallSilenceThresholdMs}ms")
            Slider(
                value = vm.pitchBallSilenceThresholdMs.toFloat(),
                onValueChange = { vm.pitchBallSilenceThresholdMs = it.toLong() },
                valueRange = 100f..2000f,
            )
        }
    }
}
