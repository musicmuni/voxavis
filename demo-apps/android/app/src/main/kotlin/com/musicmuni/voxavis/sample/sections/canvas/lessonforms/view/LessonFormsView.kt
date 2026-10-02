package com.musicmuni.voxavis.sample.sections.canvas.lessonforms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.musicmuni.voxavis.SingingPractice
import com.musicmuni.voxavis.model.PracticeViewport
import com.musicmuni.voxavis.model.ReferenceForm
import com.musicmuni.voxavis.model.SingingPracticeConfig
import com.musicmuni.voxavis.rememberNameableLineCount
import com.musicmuni.voxavis.sample.sections.canvas.lessonforms.viewmodel.LessonFormsViewModel
import com.musicmuni.voxavis.sample.shared.DescribedSwitch
import com.musicmuni.voxavis.sample.shared.EveryFrame
import com.musicmuni.voxavis.sample.shared.OptionChip

/**
 * One lesson, two forms, and the difference between the material and the view
 * (ADR-012).
 *
 * The lesson ships a recorded line and a transcription. [ReferenceForm] says
 * which of them the learner follows; it is a resource, so it can change what is
 * drawn and how tall the window is. "Show reference" is config: it hides the
 * line or the bars and moves nothing else, so the grid and the window stay put.
 */
@Composable
fun LessonFormsView(vm: LessonFormsViewModel = viewModel()) {
    EveryFrame(running = vm.playing) { elapsedMs -> vm.onFrame(elapsedMs) }
    val clock = remember(vm) { { vm.clockMs.longValue } }

    // Remembered, so the window is not worked out again on every recomposition.
    val viewport = remember(vm.followPhrase) {
        if (vm.followPhrase) {
            PracticeViewport.FollowPhrase(onResolved = { vm.windowReport = it })
        } else {
            PracticeViewport.FitAll
        }
    }
    val config = remember(viewport, vm.showReference) {
        SingingPracticeConfig.create(
            pitchRange = vm.bounds,
            viewport = viewport,
            showReference = vm.showReference,
        )
    }
    val canvasHeight = if (vm.tallCanvas) 260.dp else 150.dp

    Column(modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)) {
        SingingPractice(
            modifier = Modifier.fillMaxWidth().height(canvasHeight),
            resources = vm.resources,
            currentTimeMs = clock,
            performancePitch = vm.performanceBuffer,
            config = config,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = { vm.playing = !vm.playing }) {
                Text(if (vm.playing) "Pause" else "Play")
            }

            HorizontalDivider()
            Text("Reference form (the lesson)", style = MaterialTheme.typography.titleSmall)
            Text(
                "What the learner follows. Notes draws the transcription as bars; Contour " +
                    "draws the recorded line; None leaves the grid and the learner's own line.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReferenceForm.entries.forEach { form ->
                    OptionChip(
                        selected = vm.form == form,
                        onClick = { vm.form = form },
                        label = form.name,
                    )
                }
            }

            DescribedSwitch(
                label = "Show reference (the view)",
                description = "Hides the line or the bars. The grid and the window do not move.",
                checked = vm.showReference,
                onCheckedChange = { vm.showReference = it },
            )

            HorizontalDivider()
            Text("Viewport", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OptionChip(selected = !vm.followPhrase, onClick = { vm.followPhrase = false }, label = "Fit all")
                OptionChip(selected = vm.followPhrase, onClick = { vm.followPhrase = true }, label = "Follow phrase")
            }
            val report = vm.windowReport
            Text(
                if (vm.followPhrase && report != null) {
                    "A ${report.windowCents.toInt()}¢ window over ${report.boundsCents.toInt()}¢ " +
                        "of material, moving to each phrase."
                } else {
                    "Every note of the lesson on screen at once."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider()
            Text("Grid labels", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OptionChip(selected = vm.tallCanvas, onClick = { vm.tallCanvas = true }, label = "Tall canvas")
                OptionChip(selected = !vm.tallCanvas, onClick = { vm.tallCanvas = false }, label = "Short canvas")
            }
            DescribedSwitch(
                label = "Rank labels",
                description = "GridLine.priority: Sa is an anchor and Pa comes next, so they keep " +
                    "their names when the canvas cannot fit them all. Off, the names higher up win.",
                checked = vm.rankedLabels,
                onCheckedChange = { vm.rankedLabels = it },
            )
            Text(
                "This canvas can name ${rememberNameableLineCount(canvasHeight)} lines without two colliding.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
