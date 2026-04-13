package com.musicmuni.voxavis.sample.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DimensionSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    suffix: String = "dp",
    valueFormat: String = "%.1f",
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ${valueFormat.format(value)}$suffix",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(150.dp),
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
        )
    }
}
