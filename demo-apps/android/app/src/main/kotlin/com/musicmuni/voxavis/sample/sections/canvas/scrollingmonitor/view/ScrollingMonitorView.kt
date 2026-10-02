package com.musicmuni.voxavis.sample.sections.canvas.scrollingmonitor.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.ScrollingPitchMonitor
import com.musicmuni.voxavis.features.PitchViewport
import com.musicmuni.voxavis.features.ScrollingPitchMonitorStyle
import com.musicmuni.voxavis.sample.sections.canvas.scrollingmonitor.viewmodel.ViewportChoice
import com.musicmuni.voxavis.sample.sections.canvas.scrollingmonitor.viewmodel.ScrollingMonitorViewModel
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.sample.shared.OptionChip

@Composable
fun ScrollingMonitorView(vm: ScrollingMonitorViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    val defaultStyle = ScrollingPitchMonitorStyle.default()
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = ScrollingPitchMonitorStyle.default()
            DimensionSlider(
                label = "Ball Radius",
                value = vm.customBallRadius ?: defaults.ball.radius.value,
                onValueChange = { vm.customBallRadius = it },
                valueRange = 4f..24f,
            )
            ColorPalette(
                label = "Contour Color",
                selectedColor = vm.customContourColor ?: defaults.contour.color,
                onColorSelected = { vm.customContourColor = it },
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
        if (vm.playing && !vm.useRecordedContour) {
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
        contour = defaultStyle.contour.copy(
            color = vm.customContourColor ?: defaultStyle.contour.color,
        ),
    )

    // Which slice of the cents axis is on screen. pitchRange stays the data's
    // bounds; a viewport chooses the window inside it.
    val viewport = remember(vm.viewport) {
        when (vm.viewport) {
            ViewportChoice.Whole -> null
            ViewportChoice.Static -> PitchViewport.Static(range = 0f..700f)
            ViewportChoice.Following -> PitchViewport.Following(sizeCents = 600f, initialCenterCents = 350f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        ScrollingPitchMonitor(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            trackLengthMs = vm.trackLengthMs,
            currentTimeMs = vm.currentTimeMs,
            performancePitch = if (!vm.useRecordedContour) vm.performanceBuffer else null,
            performanceContour = if (vm.useRecordedContour) vm.recordedContour else null,
            gridLines = vm.gridLines,
            pitchRange = 0f..1200f,
            viewport = viewport,
            barPositionRatio = vm.barPositionRatio,
            timePerInchMs = vm.timePerInchMs,
            showGridLabels = vm.showGridLabels,
            // Before a note is sung the ball waits on Sa, not mid-window.
            restingPitchCents = 0f,
            style = style,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

            // Viewport
            Text("Viewport")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OptionChip(selected = vm.viewport == ViewportChoice.Whole, onClick = { vm.viewport = ViewportChoice.Whole }, label = "Whole range")
                OptionChip(selected = vm.viewport == ViewportChoice.Static, onClick = { vm.viewport = ViewportChoice.Static }, label = "Sa to Pa")
                OptionChip(selected = vm.viewport == ViewportChoice.Following, onClick = { vm.viewport = ViewportChoice.Following }, label = "Following")
            }
            Text(
                when (vm.viewport) {
                    ViewportChoice.Whole -> "All of pitchRange, Sa to upper Sa."
                    ViewportChoice.Static -> "A fixed window inside pitchRange."
                    ViewportChoice.Following -> "A half-octave window that pans after the singer stays near its edge."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Data source toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Use Recorded Contour", modifier = Modifier.weight(1f))
                Switch(checked = vm.useRecordedContour, onCheckedChange = { vm.useRecordedContour = it })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show Grid Labels", modifier = Modifier.weight(1f))
                Switch(checked = vm.showGridLabels, onCheckedChange = { vm.showGridLabels = it })
            }

            // Bar position ratio
            Text("Bar Position: ${"%.2f".format(vm.barPositionRatio)}")
            Slider(
                value = vm.barPositionRatio,
                onValueChange = { vm.barPositionRatio = it },
                valueRange = 0.1f..0.9f,
            )

            // Time per inch
            Text("Time per Inch: ${vm.timePerInchMs}ms")
            Slider(
                value = vm.timePerInchMs.toFloat(),
                onValueChange = { vm.timePerInchMs = it.toInt() },
                valueRange = 1000f..10000f,
            )
        }
    }
}
