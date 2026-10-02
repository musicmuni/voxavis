package com.musicmuni.voxavis.sample.sections.navigation.segmentscrubber.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.SegmentScrubber
import com.musicmuni.voxavis.model.SegmentChip
import com.musicmuni.voxavis.navigation.SegmentScrubberStyle
import com.musicmuni.voxavis.sample.sections.navigation.segmentscrubber.viewmodel.SegmentScrubberViewModel
import com.musicmuni.voxavis.sample.shared.DescribedSwitch
import com.musicmuni.voxavis.sample.shared.EveryFrame
import com.musicmuni.voxavis.sample.shared.GestureEventLog
import com.musicmuni.voxavis.sample.shared.OptionChip
import com.musicmuni.voxavis.sample.shared.ScoreBandModeChips
import com.musicmuni.voxavis.sample.shared.scoreBands
import com.musicmuni.voxavis.sample.shared.verdictFor

/**
 * A lesson's phrases as a bar the learner can scrub.
 *
 * Hollow is memory (what a phrase scored last pass), solid is this pass, and
 * the phrase being sung fills from the left. A chip names a score for a moment,
 * a dot under a phrase means the host is coming back to it, and focus dims the
 * rest while one phrase is worked on. On a crowded lesson the bar spreads under
 * the finger so a narrow phrase can still be aimed at.
 */
@Composable
fun SegmentScrubberView(vm: SegmentScrubberViewModel = viewModel()) {
    EveryFrame(running = vm.playing) { elapsedMs -> vm.onFrame(elapsedMs) }
    // Read by the bar in its own draw, so the thumb moves without recomposing it.
    val clock = remember(vm) { { vm.clockMs.longValue } }

    val bands = scoreBands(vm.bandMode)
    val scored = vm.visibleScore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SegmentScrubber(
            modifier = Modifier.fillMaxWidth(),
            segments = vm.segments,
            totalDurationMs = vm.totalDurationMs,
            currentTimeMs = clock,
            style = SegmentScrubberStyle.default().copy(bands = bands),
            interactive = vm.interactive,
            chip = scored?.let { SegmentChip(it.index, verdictFor(it.score), bands.colorFor(it.score)) },
            queuedIndices = vm.queued,
            focusedIndex = if (vm.focusCurrent) vm.currentIndex else null,
            onScrubPreview = { index, _ -> vm.preview = index },
            onSeekCommitted = { index, _ -> vm.seekTo(index) },
        )

        Text(
            vm.preview?.let { "Release to go to phrase ${it + 1}" }
                ?: "Phrase ${vm.currentIndex + 1} of ${vm.segments.size}",
            style = MaterialTheme.typography.bodyMedium,
        )
        GestureEventLog(events = vm.events)

        HorizontalDivider()
        Button(onClick = { vm.playing = !vm.playing }) {
            Text(if (vm.playing) "Pause" else "Play")
        }

        Text("Lesson", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OptionChip(selected = !vm.crowded, onClick = { vm.showCrowded(false) }, label = "8 phrases")
            OptionChip(selected = vm.crowded, onClick = { vm.showCrowded(true) }, label = "30 phrases")
        }
        Text(
            "With 30 the phrases are too narrow to aim at, so pressing spreads the ones under the finger.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        DescribedSwitch(
            label = "Focus the current phrase",
            description = "Dims every other phrase while one is worked on. The whole lesson stays on the bar.",
            checked = vm.focusCurrent,
            onCheckedChange = { vm.focusCurrent = it },
        )
        DescribedSwitch(
            label = "Interactive",
            description = "Off draws a read-only strip: no thumb, no gestures.",
            checked = vm.interactive,
            onCheckedChange = { vm.interactive = it },
        )

        Text("Score colours", style = MaterialTheme.typography.bodyMedium)
        ScoreBandModeChips(mode = vm.bandMode, onModeChange = { vm.bandMode = it })
        Text(
            "Phrases scoring under 0.6 get a dot: the host means to come back to them.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
