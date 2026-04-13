package com.musicmuni.voxavis.sample.sections.charts.radarchart.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.RadarChart
import com.musicmuni.voxavis.components.charts.RadarChartStyle
import com.musicmuni.voxavis.sample.sections.charts.radarchart.viewmodel.RadarChartViewModel
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.MockData

@Composable
fun RadarChartDemoView(vm: RadarChartViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current

    LaunchedEffect(vm.autoAnimate) {
        if (vm.autoAnimate) {
            val start = System.currentTimeMillis()
            while (true) {
                val t = System.currentTimeMillis() - start
                val animated = vm.metrics.mapIndexed { i, m ->
                    val phase = i * 500L
                    m.copy(value = MockData.animatedValue(t + phase, 3000L, 0.3f, 0.5f).coerceIn(0f, 1f))
                }
                vm.metrics.clear()
                vm.metrics.addAll(animated)
                kotlinx.coroutines.delay(16)
            }
        }
    }
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = RadarChartStyle.default()
            DimensionSlider("Web Stroke", (vm.customWebStrokeWidth ?: defaults.webStrokeWidth).value, { vm.customWebStrokeWidth = it.dp }, 0.5f..6f)
            ColorPalette("Fill Color", vm.customFillColor ?: defaults.currentFillColor, { vm.customFillColor = it })
            ColorPalette("Grid Color", vm.customGridColor ?: defaults.gridColor, { vm.customGridColor = it })
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    val defaultStyle = RadarChartStyle.default()
    val style = defaultStyle.copy(
        webStrokeWidth = vm.customWebStrokeWidth ?: defaultStyle.webStrokeWidth,
        currentFillColor = vm.customFillColor ?: defaultStyle.currentFillColor,
        gridColor = vm.customGridColor ?: defaultStyle.gridColor,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RadarChart(
            modifier = Modifier.size(280.dp).align(Alignment.CenterHorizontally),
            metrics = vm.metrics,
            gridRingCount = vm.gridRingCount,
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Grid Rings: ${vm.gridRingCount}", modifier = Modifier.width(120.dp))
            Slider(
                value = vm.gridRingCount.toFloat(),
                onValueChange = { vm.gridRingCount = it.toInt() },
                valueRange = 2f..8f,
                steps = 5,
                modifier = Modifier.weight(1f)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Show Best", modifier = Modifier.weight(1f))
            Switch(checked = vm.showBest, onCheckedChange = { vm.showBest = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Auto-Animate", modifier = Modifier.weight(1f))
            Switch(checked = vm.autoAnimate, onCheckedChange = { vm.autoAnimate = it })
        }

        Button(onClick = { vm.randomize() }, enabled = !vm.autoAnimate) { Text("Randomize") }
    }
}
