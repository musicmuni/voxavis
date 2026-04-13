package com.musicmuni.voxavis.sample.sections.charts.vocalrange.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.VocalRangeChart
import com.musicmuni.voxavis.components.charts.VocalRangeChartStyle
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.sample.sections.charts.vocalrange.viewmodel.VocalRangeChartViewModel
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider

@Composable
fun VocalRangeChartView(vm: VocalRangeChartViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = VocalRangeChartStyle.default()
            ColorPalette("Bar Color", vm.customBarColor ?: defaults.barColor, { vm.customBarColor = it })
            DimensionSlider("Bar Width", vm.customBarWidth ?: defaults.barWidth, { vm.customBarWidth = it }, 10f..80f, "")
            DimensionSlider("Corner Radius", vm.customBarCornerRadius ?: defaults.barCornerRadius, { vm.customBarCornerRadius = it }, 0f..24f, "")
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    LaunchedEffect(vm.animate) {
        if (vm.animate) {
            val start = System.currentTimeMillis()
            while (true) {
                val t = System.currentTimeMillis() - start
                vm.currentPitchCents = MockData.animatedValue(t, 3000L, 1200f, 1400f)
                kotlinx.coroutines.delay(16)
            }
        }
    }

    val defaultStyle = VocalRangeChartStyle.default()
    val style = defaultStyle.copy(
        barColor = vm.customBarColor ?: defaultStyle.barColor,
        barWidth = vm.customBarWidth ?: defaultStyle.barWidth,
        barCornerRadius = vm.customBarCornerRadius ?: defaultStyle.barCornerRadius,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VocalRangeChart(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            range = vm.range,
            currentPitchCents = vm.currentPitchCents,
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Animate", modifier = Modifier.weight(1f))
            Switch(checked = vm.animate, onCheckedChange = { vm.animate = it })
        }

        if (!vm.animate) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pitch: ${vm.currentPitchCents.toInt()}¢", modifier = Modifier.width(130.dp))
                Slider(value = vm.currentPitchCents, onValueChange = { vm.currentPitchCents = it }, valueRange = 200f..2600f, modifier = Modifier.weight(1f))
            }
        }
    }
}
