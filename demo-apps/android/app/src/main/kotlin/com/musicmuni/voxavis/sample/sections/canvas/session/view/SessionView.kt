package com.musicmuni.voxavis.sample.sections.canvas.session.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.SegmentScrubber
import com.musicmuni.voxavis.SingingPractice
import com.musicmuni.voxavis.features.SingingPracticeStyle
import com.musicmuni.voxavis.model.SegmentChip
import com.musicmuni.voxavis.model.SingingPracticeConfig
import com.musicmuni.voxavis.navigation.SegmentScrubberStyle
import com.musicmuni.voxavis.primitives.ContourEmphasisStyle
import com.musicmuni.voxavis.primitives.TrailEmphasisStyle
import com.musicmuni.voxavis.sample.sections.canvas.session.viewmodel.SessionViewModel
import com.musicmuni.voxavis.sample.sections.canvas.session.viewmodel.Stage
import com.musicmuni.voxavis.sample.shared.DescribedSwitch
import com.musicmuni.voxavis.sample.shared.EveryFrame
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.sample.shared.OptionChip
import com.musicmuni.voxavis.sample.shared.ScoreBandModeChips
import com.musicmuni.voxavis.sample.shared.scoreBands
import com.musicmuni.voxavis.sample.shared.verdictFor

/**
 * A sing-after session: the teacher sings a phrase, the learner sings it back.
 *
 * Each phrase is a [com.musicmuni.voxavis.model.TakePlacement]: a slice of the
 * one recording, placed on a session clock that only moves forward, with the
 * count that opens it and the tala that runs under it. Repeating a phrase or
 * jumping to another one places another take further along that clock; the
 * take that is ending scrolls off to the left while the next arrives from the
 * right, so nothing cuts.
 */
@Composable
fun SessionView(vm: SessionViewModel = viewModel()) {
    // The host owns the clock: written once a frame, and handed to VoxaVis as a
    // reader it calls in draw (ADR-013), never as a Long that changes per frame.
    EveryFrame(running = vm.playing) { elapsedMs -> vm.onFrame(elapsedMs) }
    val sessionClock = remember(vm) { { vm.clockMs.longValue } }
    val lessonClock = remember(vm) { { vm.lessonTimeMs() } }

    // One score palette for everything on this screen that shows a score.
    val bands = scoreBands(vm.bandMode)

    val defaults = SingingPracticeStyle.default()
    val style = remember(defaults, vm.emphasiseHeldNotes) {
        if (vm.emphasiseHeldNotes) {
            defaults.copy(
                // Picks out the held stretches of the teacher's line (the
                // lesson's notes), and the stretches the learner holds steady.
                referenceContour = defaults.referenceContour.copy(emphasis = ContourEmphasisStyle()),
                trail = defaults.trail.copy(emphasis = TrailEmphasisStyle()),
            )
        } else {
            defaults
        }
    }
    val entryNoteCents = vm.entryNoteCents
    val config = remember(vm.started, entryNoteCents) {
        SingingPracticeConfig.create(
            pitchRange = vm.pitchRange,
            barPositionRatio = 0.5f,
            // Where the ball waits through the count: the note the take opens on.
            restingPitchCents = entryNoteCents,
            // Nothing is lit until the learner presses Play.
            sessionStarted = vm.started,
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)) {
        SingingPractice(
            modifier = Modifier.fillMaxWidth().height(240.dp),
            resources = vm.lesson,
            currentTimeMs = sessionClock,
            performancePitch = vm.performanceBuffer,
            config = config,
            style = style,
            // The words before a phrase. The copy is ours; when it shows, and for
            // how long, is the canvas's: it is the take's speechMs.
            speechContent = { SpeechCard(vm) },
            takes = vm.placements,
        )

        // The lesson's phrases, on the recording's own clock. Hollow is last
        // session, solid is this one; a dot means "coming back to this".
        val verdict = vm.visibleVerdict
        SegmentScrubber(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            segments = vm.phrases,
            totalDurationMs = MockData.SESSION_LENGTH_MS,
            currentTimeMs = lessonClock,
            style = SegmentScrubberStyle.default().copy(bands = bands),
            chip = verdict?.let {
                SegmentChip(it.phrase, verdictFor(it.score), bands.colorFor(it.score))
            },
            queuedIndices = vm.queued,
            focusedIndex = vm.currentTake?.phrase,
            onScrubPreview = { index, _ -> vm.scrubPreview = index },
            onSeekCommitted = { index, _ -> vm.jumpTo(index) },
        )

        StatusLine(vm)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { if (vm.playing) vm.playing = false else vm.play() }) {
                    Text(if (vm.playing) "Pause" else "Play")
                }
                OutlinedButton(onClick = { vm.restart() }) { Text("Restart") }
            }

            HorizontalDivider()
            Text("Sing each phrase", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OptionChip(
                    selected = vm.attemptsPerPhrase == 1,
                    onClick = { vm.changeAttempts(1) },
                    label = "Once",
                )
                OptionChip(
                    selected = vm.attemptsPerPhrase == 2,
                    onClick = { vm.changeAttempts(2) },
                    label = "Twice",
                )
            }

            DescribedSwitch(
                label = "Emphasise held notes",
                description = "Thickens the notes along the teacher's line and the steady parts of yours.",
                checked = vm.emphasiseHeldNotes,
                onCheckedChange = { vm.emphasiseHeldNotes = it },
            )

            Text("Score colours", style = MaterialTheme.typography.bodyMedium)
            ScoreBandModeChips(mode = vm.bandMode, onModeChange = { vm.bandMode = it })

            Text(
                "Tap or drag the phrase bar to practise another phrase from here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** What the learner is doing, in a line of its own so only it changes with the stage. */
@Composable
private fun StatusLine(vm: SessionViewModel) {
    val preview = vm.scrubPreview
    val take = vm.currentTake
    val text = when {
        preview != null -> "Release to practise phrase ${preview + 1}"
        take == null -> "Session over. Play to start again."
        else -> {
            val phrase = "Phrase ${take.phrase + 1} of ${MockData.SESSION_PHRASE_COUNT}" +
                if (take.attempt > 0) ", again" else ""
            val doing = when (vm.stage) {
                Stage.Words, Stage.Count -> "get ready"
                Stage.Listen -> "listen"
                Stage.Sing -> "your turn"
                Stage.Over -> ""
            }
            "$phrase: $doing"
        }
    }
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

/** The few words before a phrase, centred on the canvas. */
@Composable
private fun SpeechCard(vm: SessionViewModel) {
    val take = vm.currentTake ?: return
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    if (take.attempt > 0) "Once more" else "Phrase ${take.phrase + 1}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    vm.phrases[take.phrase].lyrics.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
