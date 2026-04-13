package com.musicmuni.voxavis.sample.sections.charts.metricslist.view

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
import com.musicmuni.voxavis.MetricsList
import com.musicmuni.voxavis.components.cards.MetricsListStyle
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.sections.charts.metricslist.viewmodel.MetricsListViewModel

@Composable
fun MetricsListDemoView(vm: MetricsListViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = MetricsListStyle.default()
            ColorPalette("Text Color", vm.customTextColor ?: defaults.textColor, { vm.customTextColor = it })
            ColorPalette("Divider Color", vm.customDividerColor ?: defaults.dividerColor, { vm.customDividerColor = it })
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    val defaultStyle = MetricsListStyle.default()
    val style = defaultStyle.copy(
        textColor = vm.customTextColor ?: defaultStyle.textColor,
        dividerColor = vm.customDividerColor ?: defaultStyle.dividerColor,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricsList(
            modifier = Modifier.fillMaxWidth(),
            metrics = vm.metrics,
            showBestValues = vm.showBestValues,
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Show Best Values", modifier = Modifier.weight(1f))
            Switch(checked = vm.showBestValues, onCheckedChange = { vm.showBestValues = it })
        }

        Button(onClick = { vm.randomize() }) { Text("Randomize") }
    }
}
