package com.musicmuni.voxavis.sample.sections.charts.scorecard.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.ScoreCard
import com.musicmuni.voxavis.components.cards.ScoreCardStyle
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DimensionSlider
import com.musicmuni.voxavis.sample.shared.OptionChip
import com.musicmuni.voxavis.sample.sections.charts.scorecard.viewmodel.ScoreCardViewModel

@Composable
fun ScoreCardView(vm: ScoreCardViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = ScoreCardStyle.default()
            ColorPalette("Score Color", vm.customScoreColor ?: defaults.scoreColor, { vm.customScoreColor = it })
            DimensionSlider("Rating Corner Radius", vm.customRatingCornerRadius?.value ?: defaults.ratingCornerRadius.value, { vm.customRatingCornerRadius = it.dp }, 0f..24f)
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    val defaultStyle = ScoreCardStyle.default()
    val style = defaultStyle.copy(
        scoreColor = vm.customScoreColor ?: defaultStyle.scoreColor,
        ratingCornerRadius = vm.customRatingCornerRadius ?: defaultStyle.ratingCornerRadius,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScoreCard(
            modifier = Modifier.fillMaxWidth(),
            score = vm.score,
            rating = vm.rating,
            ratingColor = when (vm.rating) {
                "EXCELLENT" -> Color(0xFF4CAF50)
                "GOOD" -> Color(0xFF2196F3)
                "FAIR" -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            },
            subtitle = "Demo session",
            style = style,
        )

        HorizontalDivider()
        Text("Configuration", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Score: ${vm.score}", modifier = Modifier.width(100.dp))
            Slider(
                value = vm.score.toFloat(),
                onValueChange = { vm.score = it.toInt(); vm.updateRating() },
                valueRange = 0f..100f,
                modifier = Modifier.weight(1f)
            )
        }

        Text("Rating", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("EXCELLENT", "GOOD", "FAIR", "NEEDS WORK").forEach { r ->
                OptionChip(selected = vm.rating == r, onClick = { vm.rating = r }, label = r)
            }
        }
    }
}
