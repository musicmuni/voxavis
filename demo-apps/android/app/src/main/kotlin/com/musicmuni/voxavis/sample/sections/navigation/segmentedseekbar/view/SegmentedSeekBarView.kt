package com.musicmuni.voxavis.sample.sections.navigation.segmentedseekbar.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.SegmentedSeekBar
import com.musicmuni.voxavis.sample.sections.navigation.segmentedseekbar.viewmodel.SegmentedSeekBarViewModel
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.navigation.SegmentedSeekBarStyle

@Composable
fun SegmentedSeekBarView(vm: SegmentedSeekBarViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = SegmentedSeekBarStyle.default()
            ColorPalette("Marker Color", vm.customMarkerColor ?: defaults.markerColor, { vm.customMarkerColor = it })
            ColorPalette("Score Good Color", vm.customScoreGoodColor ?: defaults.scoreGoodColor, { vm.customScoreGoodColor = it })
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

    val defaultStyle = SegmentedSeekBarStyle.default()
    val style = defaultStyle.copy(
        markerColor = vm.customMarkerColor ?: defaultStyle.markerColor,
        scoreGoodColor = vm.customScoreGoodColor ?: defaultStyle.scoreGoodColor,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SegmentedSeekBar(
            modifier = Modifier.fillMaxWidth(),
            segments = vm.segments,
            totalDurationMs = vm.totalDurationMs,
            currentTimeMs = vm.currentTimeMs,
            barHeight = 6.dp,
            onSegmentTapped = { index, forward ->
                vm.lastTappedInfo = "Segment $index (${if (forward) "forward" else "backward"})"
                vm.addSeekEvent("Tap: segment $index")
            },
            onTimeChanged = { timeMs ->
                vm.currentTimeMs = timeMs
                vm.addSeekEvent("Seek: ${timeMs / 1000}s")
            },
            style = style,
        )

        if (vm.lastTappedInfo.isNotEmpty()) {
            Text("Last tapped: ${vm.lastTappedInfo}", style = MaterialTheme.typography.bodySmall)
        }

        Text("Time: ${vm.currentTimeMs / 1000}s / ${vm.totalDurationMs / 1000}s", style = MaterialTheme.typography.bodySmall)

        if (vm.seekEvents.isNotEmpty()) {
            com.musicmuni.voxavis.sample.shared.GestureEventLog(events = vm.seekEvents)
        }

        HorizontalDivider()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.playing = !vm.playing }) {
                Text(if (vm.playing) "Pause" else "Play")
            }
        }
    }
}
