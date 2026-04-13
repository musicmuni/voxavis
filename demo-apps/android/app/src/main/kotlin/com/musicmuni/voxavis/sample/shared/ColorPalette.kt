package com.musicmuni.voxavis.sample.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val defaultColorPalette = listOf(
    Color(0xFF4ECDC4), // Teal (default primary)
    Color(0xFFFFE66D), // Yellow (default secondary)
    Color(0xFF8FD5A6), // Green (default positive)
    Color(0xFFE36464), // Red (default negative)
    Color(0xFF6C63FF), // Purple
    Color(0xFFFF6B6B), // Coral
    Color(0xFF48BFE3), // Sky blue
    Color(0xFFFF9F1C), // Orange
    Color(0xFFE0E0E0), // Light gray
    Color.White,
    Color(0xFF1A1A2E), // Dark blue (default background)
    Color(0xFFF5F5F5), // Light background
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPalette(
    label: String,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    colors: List<Color> = defaultColorPalette,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            colors.forEach { color ->
                val isSelected = color == selectedColor
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            } else {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                            }
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}
