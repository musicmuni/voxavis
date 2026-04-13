package com.musicmuni.voxavis.sample.sections.charts.noteaccuracy.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.NoteAccuracyChart
import com.musicmuni.voxavis.components.charts.NoteAccuracyChartStyle
import com.musicmuni.voxavis.sample.sections.charts.noteaccuracy.viewmodel.NoteAccuracyChartViewModel
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.MockData

@Composable
fun NoteAccuracyChartView(vm: NoteAccuracyChartViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current

    LaunchedEffect(vm.autoAnimate) {
        if (vm.autoAnimate) {
            val start = System.currentTimeMillis()
            while (true) {
                val t = System.currentTimeMillis() - start
                val animated = vm.notes.mapIndexed { i, n ->
                    val phase = i * 400L
                    n.copy(deviationPercent = MockData.animatedValue(t + phase, 2000L, 20f, 0f))
                }
                vm.notes.clear()
                vm.notes.addAll(animated)
                kotlinx.coroutines.delay(16)
            }
        }
    }
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = NoteAccuracyChartStyle.default()
            ColorPalette("Good Color", vm.customGoodColor ?: defaults.goodColor, { vm.customGoodColor = it })
            ColorPalette("Poor Color", vm.customPoorColor ?: defaults.poorColor, { vm.customPoorColor = it })
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    val defaultStyle = NoteAccuracyChartStyle.default()
    val style = defaultStyle.copy(
        goodColor = vm.customGoodColor ?: defaultStyle.goodColor,
        poorColor = vm.customPoorColor ?: defaultStyle.poorColor,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NoteAccuracyChart(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            notes = vm.notes,
            noteDiameter = vm.noteDiameter.dp,
            gridLineCount = vm.gridLineCount,
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dot Size: ${vm.noteDiameter.toInt()}dp", modifier = Modifier.width(130.dp))
            Slider(value = vm.noteDiameter, onValueChange = { vm.noteDiameter = it }, valueRange = 12f..48f, modifier = Modifier.weight(1f))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Grid Lines: ${vm.gridLineCount}", modifier = Modifier.width(130.dp))
            Slider(value = vm.gridLineCount.toFloat(), onValueChange = { vm.gridLineCount = it.toInt() }, valueRange = 3f..21f, steps = 8, modifier = Modifier.weight(1f))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Simulate Live", modifier = Modifier.weight(1f))
            Switch(checked = vm.autoAnimate, onCheckedChange = { vm.autoAnimate = it })
        }

        Button(onClick = { vm.randomize() }, enabled = !vm.autoAnimate) { Text("Randomize") }
    }
}
