package com.musicmuni.voxavis.sample.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.musicmuni.voxavis.theme.ScoreBandMode
import com.musicmuni.voxavis.theme.ScoreBands

/**
 * The demo's one mapping from a score (`0f..1f`) to a colour.
 *
 * VoxaVis owns no verdict palette: every score-coloured component takes a
 * [ScoreBands], and the host builds it once and hands the same one to each, so
 * a phrase bar, a chip and a chart can never disagree about what a score is
 * worth. Red, amber and green from the theme; in [ScoreBandMode.Discrete] the
 * splits are at 0.6 and 0.8, in [ScoreBandMode.Gradient] the three are blended.
 */
@Composable
fun scoreBands(mode: ScoreBandMode): ScoreBands {
    val cs = MaterialTheme.colorScheme
    val colors = listOf(cs.error, lerp(cs.tertiary, cs.error, 0.5f), cs.tertiary)
    return when (mode) {
        ScoreBandMode.Discrete -> ScoreBands(colors, ScoreBandMode.Discrete, stops = listOf(0.6f, 0.8f))
        ScoreBandMode.Gradient -> ScoreBands(colors, ScoreBandMode.Gradient)
    }
}

/** A word for a score, for a chip. The same splits as [scoreBands]. */
fun verdictFor(score: Float): String = when {
    score >= 0.8f -> "Good"
    score >= 0.6f -> "Close"
    else -> "Again"
}

/** Two chips that switch a screen's score colouring between bands and a blend. */
@Composable
fun ScoreBandModeChips(mode: ScoreBandMode, onModeChange: (ScoreBandMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OptionChip(
            selected = mode == ScoreBandMode.Discrete,
            onClick = { onModeChange(ScoreBandMode.Discrete) },
            label = "Bands",
        )
        OptionChip(
            selected = mode == ScoreBandMode.Gradient,
            onClick = { onModeChange(ScoreBandMode.Gradient) },
            label = "Gradient",
        )
    }
}
