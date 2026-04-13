package com.musicmuni.voxavis.sample.sections.navigation.lyricsoverlay.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.LyricsOverlay
import com.musicmuni.voxavis.sample.sections.navigation.lyricsoverlay.viewmodel.LyricsOverlayViewModel
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.navigation.LyricsOverlayStyle

@Composable
fun LyricsOverlayView(vm: LyricsOverlayViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = LyricsOverlayStyle.default()
            ColorPalette("Active Color", vm.customActiveColor ?: defaults.activeColor, { vm.customActiveColor = it })
            DimensionSlider("Inactive Alpha", vm.customInactiveAlpha ?: defaults.inactiveAlpha, { vm.customInactiveAlpha = it }, 0f..1f, "", "%.2f")
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

    val defaultStyle = LyricsOverlayStyle.default()
    val style = defaultStyle.copy(
        activeColor = vm.customActiveColor ?: defaultStyle.activeColor,
        inactiveAlpha = vm.customInactiveAlpha ?: defaultStyle.inactiveAlpha,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LyricsOverlay(
            modifier = Modifier.fillMaxWidth().height(if (vm.isExpanded) 200.dp else 100.dp),
            segments = vm.segments,
            currentTimeMs = vm.currentTimeMs,
            isExpanded = vm.isExpanded,
            onExpandToggle = { vm.isExpanded = !vm.isExpanded },
            onSegmentSelected = { index -> vm.selectedSegmentIndex = index },
            visibleItemCount = vm.visibleItemCount,
            style = style,
        )

        if (vm.selectedSegmentIndex >= 0) {
            Text(
                "Selected segment: ${vm.selectedSegmentIndex}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.playing = !vm.playing }) {
                Text(if (vm.playing) "Pause" else "Play")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Expanded", modifier = Modifier.weight(1f))
            Switch(checked = vm.isExpanded, onCheckedChange = { vm.isExpanded = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Visible Items: ${vm.visibleItemCount}", modifier = Modifier.width(130.dp))
            Slider(
                value = vm.visibleItemCount.toFloat(),
                onValueChange = { vm.visibleItemCount = it.toInt() },
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
