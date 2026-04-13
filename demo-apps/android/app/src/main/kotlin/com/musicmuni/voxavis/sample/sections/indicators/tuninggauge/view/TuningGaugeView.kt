package com.musicmuni.voxavis.sample.sections.indicators.tuninggauge.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.TuningGauge
import com.musicmuni.voxavis.components.meters.TuningGaugeStyle
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.sample.shared.OptionChip
import com.musicmuni.voxavis.sample.sections.indicators.tuninggauge.viewmodel.TuningGaugeViewModel

@Composable
fun TuningGaugeView(vm: TuningGaugeViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = TuningGaugeStyle.default()
            ColorPalette("In Tune", vm.customInTuneColor ?: defaults.inTuneColor, { vm.customInTuneColor = it })
            ColorPalette("Slightly Off", vm.customSlightlyOffColor ?: defaults.slightlyOffColor, { vm.customSlightlyOffColor = it })
            ColorPalette("Very Off", vm.customVeryOffColor ?: defaults.veryOffColor, { vm.customVeryOffColor = it })
            DimensionSlider("Needle Width", vm.customNeedleStrokeWidth ?: defaults.needleStrokeWidth, { vm.customNeedleStrokeWidth = it }, 1f..8f, "")
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    LaunchedEffect(vm.animate) {
        if (vm.animate) {
            val start = System.currentTimeMillis()
            while (true) {
                val t = System.currentTimeMillis() - start
                vm.centsOff = MockData.animatedValue(t, 2000L, 30f, 0f)
                vm.confidence = MockData.animatedValue(t, 3000L, 0.4f, 0.6f).coerceIn(0f, 1f)
                kotlinx.coroutines.delay(16)
            }
        }
    }

    val defaultStyle = TuningGaugeStyle.default()
    val style = defaultStyle.copy(
        inTuneColor = vm.customInTuneColor ?: defaultStyle.inTuneColor,
        slightlyOffColor = vm.customSlightlyOffColor ?: defaultStyle.slightlyOffColor,
        veryOffColor = vm.customVeryOffColor ?: defaultStyle.veryOffColor,
        needleStrokeWidth = vm.customNeedleStrokeWidth ?: defaultStyle.needleStrokeWidth,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TuningGauge(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            centsOff = vm.centsOff,
            noteLabel = vm.noteLabel,
            confidence = vm.confidence,
            inTuneThreshold = vm.inTuneThreshold,
            gaugeRange = vm.gaugeRange,
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
                Text("Cents Off: ${vm.centsOff.toInt()}", modifier = Modifier.width(130.dp))
                Slider(value = vm.centsOff, onValueChange = { vm.centsOff = it }, valueRange = -50f..50f, modifier = Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Confidence: ${"%.0f".format(vm.confidence * 100)}%", modifier = Modifier.width(130.dp))
                Slider(value = vm.confidence, onValueChange = { vm.confidence = it }, modifier = Modifier.weight(1f))
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("In-Tune Threshold: ${vm.inTuneThreshold.toInt()}c", modifier = Modifier.width(180.dp))
            Slider(value = vm.inTuneThreshold, onValueChange = { vm.inTuneThreshold = it }, valueRange = 5f..25f, modifier = Modifier.weight(1f))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Gauge Range: ${vm.gaugeRange.toInt()}c", modifier = Modifier.width(180.dp))
            Slider(value = vm.gaugeRange, onValueChange = { vm.gaugeRange = it }, valueRange = 25f..100f, modifier = Modifier.weight(1f))
        }

        Text("Note", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Sa", "Re", "Ga", "Ma", "Pa", "Dha", "Ni").forEach { note ->
                OptionChip(selected = vm.noteLabel == note, onClick = { vm.noteLabel = note }, label = note)
            }
        }
    }
}
