package com.musicmuni.voxavis.sample.sections.indicators.confidencemeter.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.ConfidenceMeter
import com.musicmuni.voxavis.components.meters.ConfidenceMeterStyle
import com.musicmuni.voxavis.model.Orientation
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.sample.shared.OptionChip
import com.musicmuni.voxavis.sample.sections.indicators.confidencemeter.viewmodel.ConfidenceMeterViewModel

@Composable
fun ConfidenceMeterView(vm: ConfidenceMeterViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = ConfidenceMeterStyle.default()
            ColorPalette("Low Color", vm.customLowColor ?: defaults.lowColor, { vm.customLowColor = it })
            ColorPalette("Medium Color", vm.customMediumColor ?: defaults.mediumColor, { vm.customMediumColor = it })
            ColorPalette("High Color", vm.customHighColor ?: defaults.highColor, { vm.customHighColor = it })
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    LaunchedEffect(vm.animate) {
        if (vm.animate) {
            val start = System.currentTimeMillis()
            while (true) {
                val t = System.currentTimeMillis() - start
                vm.confidence = MockData.animatedValue(t, 2500L, 0.45f, 0.5f).coerceIn(0f, 1f)
                kotlinx.coroutines.delay(16)
            }
        }
    }

    val defaultStyle = ConfidenceMeterStyle.default()
    val style = defaultStyle.copy(
        lowColor = vm.customLowColor ?: defaultStyle.lowColor,
        mediumColor = vm.customMediumColor ?: defaultStyle.mediumColor,
        highColor = vm.customHighColor ?: defaultStyle.highColor,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (vm.orientation == Orientation.Horizontal) {
            ConfidenceMeter(
                modifier = Modifier.fillMaxWidth().height(32.dp),
                confidence = vm.confidence,
                orientation = Orientation.Horizontal,
                showLabel = vm.showLabel,
                lowThreshold = vm.lowThreshold,
                highThreshold = vm.highThreshold,
                style = style,
            )
        } else {
            ConfidenceMeter(
                modifier = Modifier.width(32.dp).height(200.dp).align(Alignment.CenterHorizontally),
                confidence = vm.confidence,
                orientation = Orientation.Vertical,
                showLabel = vm.showLabel,
                lowThreshold = vm.lowThreshold,
                highThreshold = vm.highThreshold,
                style = style,
            )
        }

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Text("Orientation", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OptionChip(selected = vm.orientation == Orientation.Horizontal, onClick = { vm.orientation = Orientation.Horizontal }, label = "Horizontal")
            OptionChip(selected = vm.orientation == Orientation.Vertical, onClick = { vm.orientation = Orientation.Vertical }, label = "Vertical")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Show Label", modifier = Modifier.weight(1f))
            Switch(checked = vm.showLabel, onCheckedChange = { vm.showLabel = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Animate", modifier = Modifier.weight(1f))
            Switch(checked = vm.animate, onCheckedChange = { vm.animate = it })
        }

        if (!vm.animate) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Confidence: ${"%.0f".format(vm.confidence * 100)}%", modifier = Modifier.width(140.dp))
                Slider(value = vm.confidence, onValueChange = { vm.confidence = it }, modifier = Modifier.weight(1f))
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Low: ${"%.0f".format(vm.lowThreshold * 100)}%", modifier = Modifier.width(140.dp))
            Slider(value = vm.lowThreshold, onValueChange = { vm.lowThreshold = it }, valueRange = 0.1f..0.5f, modifier = Modifier.weight(1f))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("High: ${"%.0f".format(vm.highThreshold * 100)}%", modifier = Modifier.width(140.dp))
            Slider(value = vm.highThreshold, onValueChange = { vm.highThreshold = it }, valueRange = 0.5f..0.95f, modifier = Modifier.weight(1f))
        }
    }
}
