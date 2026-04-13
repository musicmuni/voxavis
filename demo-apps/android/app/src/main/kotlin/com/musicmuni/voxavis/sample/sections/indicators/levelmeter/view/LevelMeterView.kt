package com.musicmuni.voxavis.sample.sections.indicators.levelmeter.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.LevelMeter
import com.musicmuni.voxavis.components.meters.LevelMeterStyle
import com.musicmuni.voxavis.model.Orientation
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.sample.shared.OptionChip
import com.musicmuni.voxavis.sample.sections.indicators.levelmeter.viewmodel.LevelMeterViewModel

@Composable
fun LevelMeterDemoView(vm: LevelMeterViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = LevelMeterStyle.default()
            ColorPalette("Normal Color", vm.customNormalColor ?: defaults.normalColor, { vm.customNormalColor = it })
            ColorPalette("Loud Color", vm.customLoudColor ?: defaults.loudColor, { vm.customLoudColor = it })
            ColorPalette("Clipping Color", vm.customClippingColor ?: defaults.clippingColor, { vm.customClippingColor = it })
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    LaunchedEffect(vm.animate) {
        if (vm.animate) {
            val start = System.currentTimeMillis()
            while (true) {
                val t = System.currentTimeMillis() - start
                vm.level = MockData.animatedValue(t, 600L, 0.4f, 0.5f).coerceIn(0f, 1f)
                kotlinx.coroutines.delay(16)
            }
        }
    }

    val defaultStyle = LevelMeterStyle.default()
    val style = defaultStyle.copy(
        normalColor = vm.customNormalColor ?: defaultStyle.normalColor,
        loudColor = vm.customLoudColor ?: defaultStyle.loudColor,
        clippingColor = vm.customClippingColor ?: defaultStyle.clippingColor,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (vm.orientation == Orientation.Vertical) {
            LevelMeter(
                modifier = Modifier.width(40.dp).height(200.dp).align(Alignment.CenterHorizontally),
                level = vm.level,
                segmentCount = vm.segmentCount,
                orientation = Orientation.Vertical,
                loudThreshold = vm.loudThreshold,
                clippingThreshold = vm.clippingThreshold,
                style = style,
            )
        } else {
            LevelMeter(
                modifier = Modifier.fillMaxWidth().height(32.dp),
                level = vm.level,
                segmentCount = vm.segmentCount,
                orientation = Orientation.Horizontal,
                loudThreshold = vm.loudThreshold,
                clippingThreshold = vm.clippingThreshold,
                style = style,
            )
        }

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Text("Orientation", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OptionChip(selected = vm.orientation == Orientation.Vertical, onClick = { vm.orientation = Orientation.Vertical }, label = "Vertical")
            OptionChip(selected = vm.orientation == Orientation.Horizontal, onClick = { vm.orientation = Orientation.Horizontal }, label = "Horizontal")
        }

        Text("Segments", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(0, 10, 20, 30).forEach { count ->
                OptionChip(selected = vm.segmentCount == count, onClick = { vm.segmentCount = count }, label = if (count == 0) "Smooth" else "$count")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Animate", modifier = Modifier.weight(1f))
            Switch(checked = vm.animate, onCheckedChange = { vm.animate = it })
        }

        if (!vm.animate) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Level: ${"%.0f".format(vm.level * 100)}%", modifier = Modifier.width(120.dp))
                Slider(value = vm.level, onValueChange = { vm.level = it }, modifier = Modifier.weight(1f))
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Loud: ${"%.0f".format(vm.loudThreshold * 100)}%", modifier = Modifier.width(120.dp))
            Slider(value = vm.loudThreshold, onValueChange = { vm.loudThreshold = it }, valueRange = 0.3f..0.95f, modifier = Modifier.weight(1f))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Clipping: ${"%.0f".format(vm.clippingThreshold * 100)}%", modifier = Modifier.width(120.dp))
            Slider(value = vm.clippingThreshold, onValueChange = { vm.clippingThreshold = it }, valueRange = 0.5f..1f, modifier = Modifier.weight(1f))
        }
    }
}
