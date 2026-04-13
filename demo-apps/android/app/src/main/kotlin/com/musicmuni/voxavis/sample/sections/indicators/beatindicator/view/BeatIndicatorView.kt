package com.musicmuni.voxavis.sample.sections.indicators.beatindicator.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.BeatIndicator
import com.musicmuni.voxavis.components.meters.BeatIndicatorStyle
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.OptionChip
import com.musicmuni.voxavis.sample.sections.indicators.beatindicator.viewmodel.BeatIndicatorViewModel

@Composable
fun BeatIndicatorView(vm: BeatIndicatorViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = BeatIndicatorStyle.default()
            ColorPalette("Beat Color", vm.customBeatColor ?: defaults.beatColor, { vm.customBeatColor = it })
            ColorPalette("Downbeat Color", vm.customDownbeatColor ?: defaults.downbeatColor, { vm.customDownbeatColor = it })
            DimensionSlider("Spacing", vm.customSpacingMultiplier ?: defaults.spacingMultiplier, { vm.customSpacingMultiplier = it }, 1f..6f, "x")
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    LaunchedEffect(vm.running, vm.beatsPerCycle) {
        if (vm.running) {
            while (true) {
                kotlinx.coroutines.delay(500)
                vm.currentBeat = (vm.currentBeat + 1) % vm.beatsPerCycle
            }
        }
    }

    val defaultStyle = BeatIndicatorStyle.default()
    val style = defaultStyle.copy(
        beatColor = vm.customBeatColor ?: defaultStyle.beatColor,
        downbeatColor = vm.customDownbeatColor ?: defaultStyle.downbeatColor,
        spacingMultiplier = vm.customSpacingMultiplier ?: defaultStyle.spacingMultiplier,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BeatIndicator(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            currentBeat = vm.currentBeat,
            beatsPerCycle = vm.beatsPerCycle,
            bpm = 120f,
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Text("Beats Per Cycle", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(3, 4, 6, 7, 8).forEach { count ->
                OptionChip(selected = vm.beatsPerCycle == count, onClick = { vm.beatsPerCycle = count; vm.currentBeat = 0 }, label = "$count")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Running", modifier = Modifier.weight(1f))
            Switch(checked = vm.running, onCheckedChange = { vm.running = it })
        }
    }
}
