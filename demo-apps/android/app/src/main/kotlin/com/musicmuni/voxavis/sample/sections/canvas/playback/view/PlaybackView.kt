package com.musicmuni.voxavis.sample.sections.canvas.playback.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.PracticeReview
import com.musicmuni.voxavis.features.PracticeReviewStyle
import com.musicmuni.voxavis.model.PracticeReviewConfig
import com.musicmuni.voxavis.model.PracticeReviewResources
import com.musicmuni.voxavis.sample.sections.canvas.playback.viewmodel.PlaybackViewModel
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState

@Composable
fun PlaybackView(vm: PlaybackViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    val defaultStyle = PracticeReviewStyle.default()
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = PracticeReviewStyle.default()
            ColorPalette(
                label = "Reference Pitch",
                selectedColor = vm.customRefColor ?: defaults.referenceContour.color,
                onColorSelected = { vm.customRefColor = it },
            )
            ColorPalette(
                label = "Note Color",
                selectedColor = vm.customNoteReferenceColor ?: defaults.noteBars.noteColor,
                onColorSelected = { vm.customNoteReferenceColor = it },
            )
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    LaunchedEffect(vm.playing) {
        if (vm.playing) {
            val start = System.currentTimeMillis()
            val offset = vm.currentTimeMs
            while (true) {
                vm.currentTimeMs = (offset + System.currentTimeMillis() - start) % vm.totalDurationMs
                kotlinx.coroutines.delay(16)
            }
        }
    }

    val style = defaultStyle.copy(
        referenceContour = defaultStyle.referenceContour.copy(
            color = vm.customRefColor ?: defaultStyle.referenceContour.color,
        ),
        noteBars = defaultStyle.noteBars.copy(
            noteColor = vm.customNoteReferenceColor ?: defaultStyle.noteBars.noteColor,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        PracticeReview(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            resources = PracticeReviewResources.create(
                trackLengthMs = vm.totalDurationMs,
                segments = vm.segments,
                notes = vm.notes,
                gridLines = vm.gridLines,
                referencePitch = vm.referencePitch,
                performerPitch = if (vm.showPerformerPitch) vm.performerPitch else null,
            ),
            currentTimeMs = vm.currentTimeMs,
            config = PracticeReviewConfig.create(
                showGridLabels = vm.showGridLabels,
                showSolfegeLabels = vm.showSolfegeLabels,
                barPositionRatio = vm.barPositionRatio,
                timePerInchMs = vm.timePerInchMs,
            ),
            style = style,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.playing = !vm.playing }) {
                    Text(if (vm.playing) "Pause" else "Play")
                }
            }

            Text("Seek: ${vm.currentTimeMs / 1000}s / ${vm.totalDurationMs / 1000}s", style = MaterialTheme.typography.bodySmall)
            Slider(
                value = vm.currentTimeMs.toFloat() / vm.totalDurationMs,
                onValueChange = { vm.currentTimeMs = (it * vm.totalDurationMs).toLong(); vm.playing = false }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show Performer Pitch", modifier = Modifier.weight(1f))
                Switch(checked = vm.showPerformerPitch, onCheckedChange = { vm.showPerformerPitch = it })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show Grid Labels", modifier = Modifier.weight(1f))
                Switch(checked = vm.showGridLabels, onCheckedChange = { vm.showGridLabels = it })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show Solfege Labels", modifier = Modifier.weight(1f))
                Switch(checked = vm.showSolfegeLabels, onCheckedChange = { vm.showSolfegeLabels = it })
            }

            Text("Bar Position: ${"%.2f".format(vm.barPositionRatio)}")
            Slider(
                value = vm.barPositionRatio,
                onValueChange = { vm.barPositionRatio = it },
                valueRange = 0.1f..0.9f,
            )

            Text("Time per Inch: ${vm.timePerInchMs}ms")
            Slider(
                value = vm.timePerInchMs.toFloat(),
                onValueChange = { vm.timePerInchMs = it.toInt() },
                valueRange = 1000f..10000f,
            )
        }
    }
}
