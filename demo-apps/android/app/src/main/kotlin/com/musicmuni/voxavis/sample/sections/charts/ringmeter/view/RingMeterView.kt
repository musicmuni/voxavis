package com.musicmuni.voxavis.sample.sections.charts.ringmeter.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.RingMeter
import com.musicmuni.voxavis.components.meters.RingMeterStyle
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.sample.sections.charts.ringmeter.viewmodel.RingMeterViewModel

@Composable
fun RingMeterView(vm: RingMeterViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = RingMeterStyle.default()
            ColorPalette("Ring Color", vm.customRingColor ?: defaults.ringColor, { vm.customRingColor = it })
            DimensionSlider("Ring Alpha", vm.customRingAlpha ?: defaults.ringAlpha, { vm.customRingAlpha = it }, 0f..1f, "", "%.2f")
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    LaunchedEffect(vm.animate) {
        if (vm.animate) {
            val start = System.currentTimeMillis()
            while (true) {
                val t = System.currentTimeMillis() - start
                vm.value = MockData.animatedValue(t, 4000L, 0.4f, 0.5f).coerceIn(0f, 1f)
                kotlinx.coroutines.delay(16)
            }
        }
    }

    val defaultStyle = RingMeterStyle.default()
    val style = defaultStyle.copy(
        ringColor = vm.customRingColor ?: defaultStyle.ringColor,
        ringAlpha = vm.customRingAlpha ?: defaultStyle.ringAlpha,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RingMeter(
            modifier = Modifier.size(240.dp),
            value = vm.value,
            displayText = "${(vm.value * 25).toInt()}",
            unitText = "seconds",
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Value: ${"%.2f".format(vm.value)}", modifier = Modifier.width(120.dp))
            Slider(value = vm.value, onValueChange = { vm.value = it; vm.animate = false }, modifier = Modifier.weight(1f))
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Animate", modifier = Modifier.weight(1f))
            Switch(checked = vm.animate, onCheckedChange = { vm.animate = it })
        }
    }
}
