package com.musicmuni.voxavis.sample.sections.canvas.callout.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.SingingPractice
import com.musicmuni.voxavis.model.PitchCalloutData
import com.musicmuni.voxavis.sample.sections.canvas.callout.viewmodel.CalloutViewModel
import com.musicmuni.voxavis.sample.shared.OptionChip
import kotlinx.coroutines.delay

/**
 * A verdict on one note, drawn at the playhead: a dot where it was sung,
 * travelling to the line it belongs on.
 *
 * The canvas draws it; the host animates it. [PitchCalloutData.progress] runs
 * from 0 (where it was sung) to 1 (arrived), and the host owns that clock
 * because the host owns the gap the verdict is shown in.
 */
@Composable
fun CalloutView(vm: CalloutViewModel = viewModel()) {
    val clock = remember(vm) { { vm.clockMs.longValue } }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(vm.example, vm.replays) {
        progress.snapTo(0f)
        delay(400)
        progress.animateTo(1f, tween(durationMillis = 700))
    }
    val example = vm.example

    Column(modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)) {
        SingingPractice(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            resources = vm.resources,
            currentTimeMs = clock,
            performancePitch = vm.performanceBuffer,
            config = vm.config,
            callout = PitchCalloutData(
                sungCents = example.sungCents,
                targetCents = example.targetCents,
                label = example.label,
                progress = progress.value,
            ),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Paused at the end of the learner's first answer. Pick a note to show its verdict.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vm.examples.forEach { candidate ->
                    OptionChip(
                        selected = candidate == example,
                        onClick = { vm.example = candidate },
                        label = candidate.title,
                    )
                }
            }
            OutlinedButton(onClick = { vm.replays++ }) { Text("Replay") }
            Text(
                "A note sung off its line starts in the learner's colour and turns to the " +
                    "on-target colour as it arrives. A note already on its line is on target " +
                    "from the first frame.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
