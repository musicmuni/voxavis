package com.musicmuni.voxavis.sample.sections.charts.scoretrend.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.ScoreTrendChart
import com.musicmuni.voxavis.components.charts.ScoreTrendChartStyle
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.sections.charts.scoretrend.viewmodel.ScoreTrendChartViewModel

@Composable
fun ScoreTrendChartView(vm: ScoreTrendChartViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = ScoreTrendChartStyle.default()
            ColorPalette("Line Color", vm.customLineColor ?: defaults.lineColor, { vm.customLineColor = it })
            DimensionSlider("Line Width", (vm.customLineStrokeWidth ?: defaults.lineStrokeWidth).value, { vm.customLineStrokeWidth = it.dp }, 1f..8f)
            DimensionSlider("Point Radius", (vm.customNormalPointRadius ?: defaults.normalPointRadius).value, { vm.customNormalPointRadius = it.dp }, 2f..12f)
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    val defaultStyle = ScoreTrendChartStyle.default()
    val style = defaultStyle.copy(
        lineColor = vm.customLineColor ?: defaultStyle.lineColor,
        lineStrokeWidth = vm.customLineStrokeWidth ?: defaultStyle.lineStrokeWidth,
        normalPointRadius = vm.customNormalPointRadius ?: defaultStyle.normalPointRadius,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScoreTrendChart(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            dataPoints = vm.dataPoints,
            bezierCurve = vm.bezierCurve,
            showGrid = vm.showGrid,
            animate = vm.animate,
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Bezier Curve", modifier = Modifier.weight(1f))
            Switch(checked = vm.bezierCurve, onCheckedChange = { vm.bezierCurve = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Show Grid", modifier = Modifier.weight(1f))
            Switch(checked = vm.showGrid, onCheckedChange = { vm.showGrid = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Animate", modifier = Modifier.weight(1f))
            Switch(checked = vm.animate, onCheckedChange = { vm.animate = it })
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.addPoint() }) { Text("Add Point") }
            OutlinedButton(onClick = { vm.reset() }) { Text("Reset") }
        }
    }
}
