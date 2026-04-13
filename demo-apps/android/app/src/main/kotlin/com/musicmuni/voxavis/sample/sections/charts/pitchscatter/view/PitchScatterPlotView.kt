package com.musicmuni.voxavis.sample.sections.charts.pitchscatter.view

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
import com.musicmuni.voxavis.PitchScatterPlot
import com.musicmuni.voxavis.components.charts.PitchScatterPlotStyle
import com.musicmuni.voxavis.sample.sections.charts.pitchscatter.viewmodel.PitchScatterPlotViewModel
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider

@Composable
fun PitchScatterPlotView(vm: PitchScatterPlotViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = PitchScatterPlotStyle.default()
            ColorPalette("Point Color", vm.customPointColor ?: defaults.pointColor, { vm.customPointColor = it })
            DimensionSlider("Line Alpha", vm.customConnectingLineAlpha ?: defaults.connectingLineAlpha, { vm.customConnectingLineAlpha = it }, 0f..1f, "", "%.2f")
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    val defaultStyle = PitchScatterPlotStyle.default()
    val style = defaultStyle.copy(
        pointColor = vm.customPointColor ?: defaultStyle.pointColor,
        connectingLineAlpha = vm.customConnectingLineAlpha ?: defaultStyle.connectingLineAlpha,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PitchScatterPlot(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            contour = vm.contour,
            pointRadius = vm.pointRadius.dp,
            showConnectingLine = vm.showConnectingLine,
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Point Radius: ${"%.1f".format(vm.pointRadius)}dp", modifier = Modifier.width(150.dp))
            Slider(value = vm.pointRadius, onValueChange = { vm.pointRadius = it }, valueRange = 0.5f..8f, modifier = Modifier.weight(1f))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Connecting Line", modifier = Modifier.weight(1f))
            Switch(checked = vm.showConnectingLine, onCheckedChange = { vm.showConnectingLine = it })
        }
    }
}
