package com.musicmuni.voxavis.sample.sections.canvas.practice.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicmuni.voxavis.SingingPractice
import com.musicmuni.voxavis.features.SingingPracticeStyle
import com.musicmuni.voxavis.model.BallMode
import com.musicmuni.voxavis.model.PhaseProfile
import com.musicmuni.voxavis.model.SessionMode
import com.musicmuni.voxavis.model.SessionPhase
import com.musicmuni.voxavis.model.SingingPracticeConfig
import com.musicmuni.voxavis.model.SingingPracticeEvents
import com.musicmuni.voxavis.model.SingingPracticeResources
import com.musicmuni.voxavis.computePhaseState
import com.musicmuni.voxavis.model.PitchPoint
import com.musicmuni.voxavis.sample.sections.canvas.practice.viewmodel.PerformanceContourOption
import com.musicmuni.voxavis.sample.sections.canvas.practice.viewmodel.PracticeViewModel
import com.musicmuni.voxavis.sample.shared.CollapsibleSection
import com.musicmuni.voxavis.sample.shared.ColorPalette
import com.musicmuni.voxavis.sample.shared.DescribedSlider
import com.musicmuni.voxavis.sample.shared.DescribedSwitch
import com.musicmuni.voxavis.sample.shared.GestureEventLog
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.sample.shared.OptionChip

@Composable
fun PracticeView(vm: PracticeViewModel = viewModel()) {
    val themeSheet = LocalThemeSheetState.current
    val defaultStyle = SingingPracticeStyle.default()

    // --- Theme sheet: style controls ---
    DisposableEffect(Unit) {
        themeSheet.componentStyleContent = @Composable {
            val defaults = SingingPracticeStyle.default()
            ThemeSheetContent(vm, defaults)
        }
        onDispose { themeSheet.componentStyleContent = null }
    }

    // Reset time and buffer when mode changes
    LaunchedEffect(vm.sessionMode) {
        vm.currentTimeMs = 0L
        vm.performanceBuffer.clear()
    }

    // Animate time
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

    // Feed performance contour into buffer
    LaunchedEffect(vm.playing) {
        if (vm.playing) {
            while (true) {
                val phase = computePhaseState(
                    vm.segments, vm.currentTimeMs, vm.sessionMode,
                    vm.preSingPrepMs
                )
                if (phase.isSinging) {
                    MockData.feedContourToBuffer(vm.performancePitch, vm.performanceBuffer, vm.currentTimeMs)
                    vm.simulatedAccuracy = MockData.simulateAccuracy(vm.currentTimeMs)
                } else {
                    vm.performanceBuffer.add(PitchPoint.invalid(vm.currentTimeMs))
                    vm.simulatedAccuracy = 0f
                }
                kotlinx.coroutines.delay(16)
            }
        }
    }

    // --- Build style from defaults + overrides ---
    val style = defaultStyle.copy(
        ball = defaultStyle.ball.copy(
            radius = vm.customBallRadius?.dp ?: defaultStyle.ball.radius,
            activeColor = vm.customBallActiveColor ?: defaultStyle.ball.activeColor,
            idleColor = vm.customBallIdleColor ?: defaultStyle.ball.idleColor,
            warmAmberColor = vm.customBallWarmAmberColor ?: defaultStyle.ball.warmAmberColor,
            smoothingStiffness = vm.customSmoothingStiffness ?: defaultStyle.ball.smoothingStiffness,
            readyStiffness = vm.customReadyStiffness ?: defaultStyle.ball.readyStiffness,
            pulseScaleMin = vm.customPulseScaleMin ?: defaultStyle.ball.pulseScaleMin,
            pulseScaleMax = vm.customPulseScaleMax ?: defaultStyle.ball.pulseScaleMax,
            pulsePeriodMs = vm.customPulsePeriodMs ?: defaultStyle.ball.pulsePeriodMs,
            idlePulsePeriodMs = vm.customIdlePulsePeriodMs ?: defaultStyle.ball.idlePulsePeriodMs,
        ),
        referenceContour = defaultStyle.referenceContour.copy(
            color = vm.customContourColor ?: defaultStyle.referenceContour.color,
            width = vm.customContourWidth?.dp ?: defaultStyle.referenceContour.width,
            guideBackgroundWidthMultiplier = vm.customGuideBGWidthMultiplier ?: defaultStyle.referenceContour.guideBackgroundWidthMultiplier,
            guideBackgroundAlpha = vm.customGuideBGAlpha ?: defaultStyle.referenceContour.guideBackgroundAlpha,
            guideForegroundAlpha = vm.customGuideFGAlpha ?: defaultStyle.referenceContour.guideForegroundAlpha,
            headDotRadius = vm.customHeadDotRadius?.dp ?: defaultStyle.referenceContour.headDotRadius,
            headGlowRadius = vm.customHeadGlowRadius?.dp ?: defaultStyle.referenceContour.headGlowRadius,
            headGlowAlpha = vm.customHeadGlowAlpha ?: defaultStyle.referenceContour.headGlowAlpha,
            useConfidence = vm.customUseConfidence ?: defaultStyle.referenceContour.useConfidence,
        ),
        trail = defaultStyle.trail.copy(
            warmColor = vm.customPerformancePitchColor ?: defaultStyle.trail.warmColor,
            coolColor = vm.customTrailCoolColor ?: defaultStyle.trail.coolColor,
            durationSeconds = vm.customTrailDuration ?: defaultStyle.trail.durationSeconds,
            headWidth = vm.customTrailHeadWidth?.dp ?: defaultStyle.trail.headWidth,
            tailWidth = vm.customTrailTailWidth?.dp ?: defaultStyle.trail.tailWidth,
            headAlpha = vm.customTrailHeadAlpha ?: defaultStyle.trail.headAlpha,
        ),
        noteBars = defaultStyle.noteBars.copy(
            noteColor = vm.customNoteColor ?: defaultStyle.noteBars.noteColor,
            barHeight = vm.customBarHeight?.dp ?: defaultStyle.noteBars.barHeight,
            cornerRadius = vm.customBarCornerRadius?.dp ?: defaultStyle.noteBars.cornerRadius,
            labelAlpha = vm.customLabelAlpha ?: defaultStyle.noteBars.labelAlpha,
            labelFontSize = vm.customLabelFontSize?.sp ?: defaultStyle.noteBars.labelFontSize,
        ),
        nowLine = defaultStyle.nowLine.copy(
            color = vm.customNowLineColor ?: defaultStyle.nowLine.color,
            width = vm.customNowLineWidth?.dp ?: defaultStyle.nowLine.width,
            glowWidth = vm.customNowLineGlowWidth?.dp ?: defaultStyle.nowLine.glowWidth,
            peakAlpha = vm.customNowLinePeakAlpha ?: defaultStyle.nowLine.peakAlpha,
            glowAlpha = vm.customNowLineGlowAlpha ?: defaultStyle.nowLine.glowAlpha,
        ),
        scoreFlash = defaultStyle.scoreFlash.copy(
            textColor = vm.customScoreFlashTextColor ?: defaultStyle.scoreFlash.textColor,
            textSize = vm.customScoreFlashTextSize?.sp ?: defaultStyle.scoreFlash.textSize,
            fadeInMs = vm.customScoreFlashFadeInMs ?: defaultStyle.scoreFlash.fadeInMs,
            holdMs = vm.customScoreFlashHoldMs ?: defaultStyle.scoreFlash.holdMs,
            fadeOutMs = vm.customScoreFlashFadeOutMs ?: defaultStyle.scoreFlash.fadeOutMs,
        ),
        grid = defaultStyle.grid.copy(
            lineColor = vm.customGridLineColor ?: defaultStyle.grid.lineColor,
            highlightedLineColor = vm.customGridHighlightedLineColor ?: defaultStyle.grid.highlightedLineColor,
            lineWidth = vm.customGridLineWidth?.dp ?: defaultStyle.grid.lineWidth,
            labelTextColor = vm.customGridLabelTextColor ?: defaultStyle.grid.labelTextColor,
            labelTextSize = vm.customGridLabelTextSize?.sp ?: defaultStyle.grid.labelTextSize,
            highlightedLabelTextSize = vm.customGridHighlightedLabelTextSize?.sp ?: defaultStyle.grid.highlightedLabelTextSize,
            highlightedLabelTextColor = vm.customGridHighlightedLabelTextColor ?: defaultStyle.grid.highlightedLabelTextColor,
            labelBackgroundColor = vm.customGridLabelBackgroundColor ?: defaultStyle.grid.labelBackgroundColor,
        ),
        segmentBands = defaultStyle.segmentBands.copy(
            referenceColor = vm.customSegmentReferenceColor ?: defaultStyle.segmentBands.referenceColor,
            performanceColor = vm.customSegmentPerformanceColor ?: defaultStyle.segmentBands.performanceColor,
            commentaryColor = vm.customSegmentCommentaryColor ?: defaultStyle.segmentBands.commentaryColor,
        ),
        listen = defaultStyle.listen.copy(
            gridAlpha = vm.customListenGridAlpha ?: defaultStyle.listen.gridAlpha,
            contentAlpha = vm.customListenContentAlpha ?: defaultStyle.listen.contentAlpha,
            referenceAlpha = vm.customListenReferenceAlpha ?: defaultStyle.listen.referenceAlpha,
            ballMode = vm.customListenBallMode ?: defaultStyle.listen.ballMode,
            ballSize = vm.customListenBallSize ?: defaultStyle.listen.ballSize,
            trailAlpha = vm.customListenTrailAlpha ?: defaultStyle.listen.trailAlpha,
        ),
        sing = defaultStyle.sing.copy(
            gridAlpha = vm.customSingGridAlpha ?: defaultStyle.sing.gridAlpha,
            contentAlpha = vm.customSingContentAlpha ?: defaultStyle.sing.contentAlpha,
            referenceAlpha = vm.customSingReferenceAlpha ?: defaultStyle.sing.referenceAlpha,
            ballMode = vm.customSingBallMode ?: defaultStyle.sing.ballMode,
            ballSize = vm.customSingBallSize ?: defaultStyle.sing.ballSize,
            trailAlpha = vm.customSingTrailAlpha ?: defaultStyle.sing.trailAlpha,
        ),
        commentary = defaultStyle.commentary.copy(
            gridAlpha = vm.customCommentaryGridAlpha ?: defaultStyle.commentary.gridAlpha,
            contentAlpha = vm.customCommentaryContentAlpha ?: defaultStyle.commentary.contentAlpha,
            referenceAlpha = vm.customCommentaryReferenceAlpha ?: defaultStyle.commentary.referenceAlpha,
            ballMode = vm.customCommentaryBallMode ?: defaultStyle.commentary.ballMode,
            ballSize = vm.customCommentaryBallSize ?: defaultStyle.commentary.ballSize,
            trailAlpha = vm.customCommentaryTrailAlpha ?: defaultStyle.commentary.trailAlpha,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        SingingPractice(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            resources = SingingPracticeResources.create(
                mode = vm.sessionMode,
                trackLengthMs = vm.totalDurationMs,
                segments = vm.segments,
                notes = vm.notes,
                gridLines = vm.gridLines,
                referencePitch = vm.referencePitch,
            ),
            currentTimeMs = vm.currentTimeMs,
            performancePitch = vm.performanceBuffer,
            accuracy = vm.effectiveAccuracy,
            config = SingingPracticeConfig.create(
                pitchRange = vm.minPitchCents..vm.maxPitchCents,
                barPositionRatio = vm.barPositionRatio,
                timePerInchMs = vm.timePerInchMs,
                numLoops = vm.numLoops,
                prepDurationMs = vm.preSingPrepMs,
                showSolfegeLabels = vm.showSolfegeLabels,
                showGridLabels = vm.showGridLabels,
                pitchBallSilenceThresholdMs = vm.pitchBallSilenceThresholdMs,
            ),
            style = style,
            events = SingingPracticeEvents(
                onDoubleTapLeft = { vm.addGestureEvent("Double-tap left") },
                onDoubleTapRight = { vm.addGestureEvent("Double-tap right") },
                onTap = { vm.addGestureEvent("Tap") },
                onPitchOutOfBounds = { dir -> vm.addGestureEvent("Pitch ${dir.name}") },
            ),
            commentaryContent = if (vm.showCommentary) {
                @Composable {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Commentary", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Great job! Let's try the avaroha next.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            } else null,
        )

        // --- Scrollable controls ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // === Always visible: Phase state + Playback ===
            val phaseState = computePhaseState(
                vm.segments, vm.currentTimeMs, vm.sessionMode,
                vm.preSingPrepMs
            )
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Phase:", style = MaterialTheme.typography.labelMedium)
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = phaseChipColor(phaseState.current),
                ) {
                    val label = if (phaseState.current == SessionPhase.PREP) {
                        val from = phaseState.prepFrom?.name ?: "?"
                        val to = phaseState.prepTo?.name ?: "?"
                        "PREP ($from\u2009\u2192\u2009$to)"
                    } else {
                        phaseState.current.name
                    }
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
                Text(
                    "${(phaseState.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            GestureEventLog(
                events = vm.gestureEvents,
                modifier = Modifier.padding(vertical = 4.dp),
            )

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

            // === Always visible: Session Mode ===
            HorizontalDivider()
            Text("Session Mode", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OptionChip(
                    selected = vm.sessionMode == SessionMode.Singafter,
                    onClick = { vm.sessionMode = SessionMode.Singafter },
                    label = "Singafter"
                )
                OptionChip(
                    selected = vm.sessionMode == SessionMode.Singalong,
                    onClick = { vm.sessionMode = SessionMode.Singalong },
                    label = "Singalong"
                )
                OptionChip(
                    selected = vm.sessionMode == SessionMode.Exercise,
                    onClick = { vm.sessionMode = SessionMode.Exercise },
                    label = "Exercise"
                )
            }

            if (vm.sessionMode == SessionMode.Singafter) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Text("Commentary Content", modifier = Modifier.weight(1f))
                    Switch(checked = vm.showCommentary, onCheckedChange = { vm.showCommentary = it })
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()

            // === Collapsible: Performance Input ===
            CollapsibleSection(
                title = "Performance Input",
                description = "Simulated singing input fed to the visualizer.",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Performance Contour", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OptionChip(
                            selected = vm.performanceContourOption == PerformanceContourOption.CLOSE,
                            onClick = { vm.performanceContourOption = PerformanceContourOption.CLOSE },
                            label = "Close"
                        )
                        OptionChip(
                            selected = vm.performanceContourOption == PerformanceContourOption.LOOSE,
                            onClick = { vm.performanceContourOption = PerformanceContourOption.LOOSE },
                            label = "Loose"
                        )
                    }

                    DescribedSwitch(
                        label = "Manual Accuracy Override",
                        description = "Override simulation with a manual slider.",
                        checked = vm.useManualAccuracy,
                        onCheckedChange = { vm.useManualAccuracy = it },
                    )

                    if (vm.useManualAccuracy) {
                        DescribedSlider(
                            label = "Accuracy",
                            description = "Low = poor pitch matching. High = perfect singing.",
                            value = vm.manualAccuracy,
                            onValueChange = { vm.manualAccuracy = it },
                            valueRange = 0f..1f,
                            suffix = "%",
                            valueFormat = "%.0f",
                        )
                    } else {
                        Text(
                            "Simulated: ${(vm.simulatedAccuracy * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    DescribedSlider(
                        label = "Silence Threshold",
                        description = "Time before pitch ball hides. Low = responsive. High = lingers.",
                        value = vm.pitchBallSilenceThresholdMs.toFloat(),
                        onValueChange = { vm.pitchBallSilenceThresholdMs = it.toLong() },
                        valueRange = 100f..2000f,
                        suffix = "ms",
                        valueFormat = "%.0f",
                    )
                }
            }

            HorizontalDivider()

            // === Collapsible: Canvas Layout ===
            CollapsibleSection(
                title = "Canvas Layout",
                description = "How the pitch space is framed \u2014 zoom, density, and now-line placement.",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DescribedSlider(
                        label = "Bar Position",
                        description = "Now-line placement. Low = left (see future). High = right (see past).",
                        value = vm.barPositionRatio,
                        onValueChange = { vm.barPositionRatio = it },
                        valueRange = 0.1f..0.9f,
                        suffix = "",
                        valueFormat = "%.2f",
                    )
                    DescribedSlider(
                        label = "Time Per Inch",
                        description = "Time density. Low = zoomed in. High = zoomed out.",
                        value = vm.timePerInchMs.toFloat(),
                        onValueChange = { vm.timePerInchMs = it.toInt() },
                        valueRange = 1000f..6000f,
                        suffix = "ms",
                        valueFormat = "%.0f",
                    )
                    DescribedSlider(
                        label = "Min Pitch",
                        description = "Lower bound of visible pitch range.",
                        value = vm.minPitchCents,
                        onValueChange = { vm.minPitchCents = it },
                        valueRange = -1200f..0f,
                        suffix = "\u00A2",
                        valueFormat = "%.0f",
                    )
                    DescribedSlider(
                        label = "Max Pitch",
                        description = "Upper bound of visible pitch range.",
                        value = vm.maxPitchCents,
                        onValueChange = { vm.maxPitchCents = it },
                        valueRange = 0f..1800f,
                        suffix = "\u00A2",
                        valueFormat = "%.0f",
                    )
                }
            }

            HorizontalDivider()

            // === Collapsible: Labels & Visibility ===
            CollapsibleSection(
                title = "Labels & Visibility",
                description = "Informational overlays on the pitch canvas.",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DescribedSwitch(
                        label = "Grid Labels",
                        description = "Show note names (Sa, Re, Ga\u2026) along grid lines.",
                        checked = vm.showGridLabels,
                        onCheckedChange = { vm.showGridLabels = it },
                    )
                    if (vm.sessionMode == SessionMode.Exercise) {
                        DescribedSwitch(
                            label = "Solfege Labels",
                            description = "Show solfege names inside note bars.",
                            checked = vm.showSolfegeLabels,
                            onCheckedChange = { vm.showSolfegeLabels = it },
                        )
                    }
                }
            }

            HorizontalDivider()

            // === Collapsible: Phase Timing ===
            CollapsibleSection(
                title = "Phase Timing",
                description = "Pacing of transitions between session phases.",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DescribedSlider(
                        label = "Pre-Sing Prep",
                        description = "LISTEN\u2192SING transition. Low = snappy. High = gradual.",
                        value = vm.preSingPrepMs.toFloat(),
                        onValueChange = { vm.preSingPrepMs = it.toLong() },
                        valueRange = 50f..1000f,
                        suffix = "ms",
                        valueFormat = "%.0f",
                    )
                    DescribedSlider(
                        label = "Post-Sing Prep",
                        description = "SING\u2192LISTEN transition. Low = snappy. High = gradual.",
                        value = vm.postSingPrepMs.toFloat(),
                        onValueChange = { vm.postSingPrepMs = it.toLong() },
                        valueRange = 50f..1000f,
                        suffix = "ms",
                        valueFormat = "%.0f",
                    )
                    if (vm.sessionMode == SessionMode.Exercise) {
                        DescribedSlider(
                            label = "Loops",
                            description = "How many times the exercise repeats.",
                            value = vm.numLoops.toFloat(),
                            onValueChange = { vm.numLoops = it.toInt() },
                            valueRange = 1f..4f,
                            suffix = "",
                            valueFormat = "%.0f",
                        )
                    }
                }
            }

            HorizontalDivider()

            // === Collapsible: Phase Ambience ===
            CollapsibleSection(
                title = "Phase Ambience",
                description = "Visual atmosphere of each session phase \u2014 how prominent or subdued different elements feel.",
            ) {
                val activePhase = phaseState.current
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PhaseAmbienceSubSection(
                        title = "Listen",
                        description = "Teacher demonstration. Typically subdued with prominent reference pitch.",
                        isActive = activePhase == SessionPhase.LISTEN,
                        defaults = defaultStyle.listen,
                        gridAlpha = vm.customListenGridAlpha, onGridAlpha = { vm.customListenGridAlpha = it },
                        contentAlpha = vm.customListenContentAlpha, onContentAlpha = { vm.customListenContentAlpha = it },
                        referenceAlpha = vm.customListenReferenceAlpha, onReferenceAlpha = { vm.customListenReferenceAlpha = it },
                        ballMode = vm.customListenBallMode, onBallMode = { vm.customListenBallMode = it },
                        ballSize = vm.customListenBallSize, onBallSize = { vm.customListenBallSize = it },
                        trailAlpha = vm.customListenTrailAlpha, onTrailAlpha = { vm.customListenTrailAlpha = it },
                    )
                    PhaseAmbienceSubSection(
                        title = "Sing",
                        description = "Student performance. Typically vibrant with active pitch tracking.",
                        isActive = activePhase == SessionPhase.SING,
                        defaults = defaultStyle.sing,
                        gridAlpha = vm.customSingGridAlpha, onGridAlpha = { vm.customSingGridAlpha = it },
                        contentAlpha = vm.customSingContentAlpha, onContentAlpha = { vm.customSingContentAlpha = it },
                        referenceAlpha = vm.customSingReferenceAlpha, onReferenceAlpha = { vm.customSingReferenceAlpha = it },
                        ballMode = vm.customSingBallMode, onBallMode = { vm.customSingBallMode = it },
                        ballSize = vm.customSingBallSize, onBallSize = { vm.customSingBallSize = it },
                        trailAlpha = vm.customSingTrailAlpha, onTrailAlpha = { vm.customSingTrailAlpha = it },
                    )
                    PhaseAmbienceSubSection(
                        title = "Commentary",
                        description = "Feedback/scoring. Typically dimmed with overlaid content.",
                        isActive = activePhase == SessionPhase.COMMENTARY,
                        defaults = defaultStyle.commentary,
                        gridAlpha = vm.customCommentaryGridAlpha, onGridAlpha = { vm.customCommentaryGridAlpha = it },
                        contentAlpha = vm.customCommentaryContentAlpha, onContentAlpha = { vm.customCommentaryContentAlpha = it },
                        referenceAlpha = vm.customCommentaryReferenceAlpha, onReferenceAlpha = { vm.customCommentaryReferenceAlpha = it },
                        ballMode = vm.customCommentaryBallMode, onBallMode = { vm.customCommentaryBallMode = it },
                        ballSize = vm.customCommentaryBallSize, onBallSize = { vm.customCommentaryBallSize = it },
                        trailAlpha = vm.customCommentaryTrailAlpha, onTrailAlpha = { vm.customCommentaryTrailAlpha = it },
                    )
                }
            }
        }
    }
}

// --- Phase Ambience sub-section (reused for Listen/Sing/Commentary) ---

@Composable
private fun PhaseAmbienceSubSection(
    title: String,
    description: String,
    isActive: Boolean,
    defaults: PhaseProfile,
    gridAlpha: Float?, onGridAlpha: (Float) -> Unit,
    contentAlpha: Float?, onContentAlpha: (Float) -> Unit,
    referenceAlpha: Float?, onReferenceAlpha: (Float) -> Unit,
    ballMode: BallMode?, onBallMode: (BallMode) -> Unit,
    ballSize: Float?, onBallSize: (Float) -> Unit,
    trailAlpha: Float?, onTrailAlpha: (Float) -> Unit,
) {
    val borderMod = if (isActive) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            .padding(start = 4.dp)
    } else {
        Modifier
    }

    CollapsibleSection(
        title = if (isActive) "$title (active)" else title,
        description = description,
        modifier = borderMod,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DescribedSlider(
                label = "Grid Opacity",
                description = "Low = hidden. High = fully visible.",
                value = gridAlpha ?: defaults.gridAlpha,
                onValueChange = onGridAlpha,
                valueRange = 0f..1f, suffix = "", valueFormat = "%.2f",
            )
            DescribedSlider(
                label = "Content Opacity",
                description = "Segment bands and note bars. Low = hidden. High = prominent.",
                value = contentAlpha ?: defaults.contentAlpha,
                onValueChange = onContentAlpha,
                valueRange = 0f..1f, suffix = "", valueFormat = "%.2f",
            )
            DescribedSlider(
                label = "Reference Opacity",
                description = "Reference pitch line. Low = hidden. High = bold.",
                value = referenceAlpha ?: defaults.referenceAlpha,
                onValueChange = onReferenceAlpha,
                valueRange = 0f..1f, suffix = "", valueFormat = "%.2f",
            )

            Text("Ball Mode", style = MaterialTheme.typography.bodyMedium)
            Text("Active = tracks pitch. Ready = idle pulse. Hidden = gone.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val current = ballMode ?: defaults.ballMode
                OptionChip(selected = current == BallMode.ACTIVE, onClick = { onBallMode(BallMode.ACTIVE) }, label = "Active")
                OptionChip(selected = current == BallMode.READY, onClick = { onBallMode(BallMode.READY) }, label = "Ready")
                OptionChip(selected = current == BallMode.HIDDEN, onClick = { onBallMode(BallMode.HIDDEN) }, label = "Hidden")
            }

            DescribedSlider(
                label = "Ball Size",
                description = "Radius multiplier. Low = small. High = large.",
                value = ballSize ?: defaults.ballSize,
                onValueChange = onBallSize,
                valueRange = 0.1f..2.0f, suffix = "x", valueFormat = "%.2f",
            )
            DescribedSlider(
                label = "Trail Opacity",
                description = "Performance trail. Low = hidden. High = full.",
                value = trailAlpha ?: defaults.trailAlpha,
                onValueChange = onTrailAlpha,
                valueRange = 0f..1f, suffix = "", valueFormat = "%.2f",
            )

        }
    }
}

// --- Theme sheet content (all style sections) ---

@Composable
private fun ThemeSheetContent(vm: PracticeViewModel, defaults: SingingPracticeStyle) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // 1. Pitch Ball
        CollapsibleSection(
            title = "Pitch Ball",
            description = "The animated ball that tracks the singer's pitch.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DescribedSlider(
                    label = "Radius", description = "Low = subtle dot. High = large orb.",
                    value = vm.customBallRadius ?: defaults.ball.radius.value,
                    onValueChange = { vm.customBallRadius = it },
                    valueRange = 4f..24f,
                )
                ColorPalette(label = "Active Color", selectedColor = vm.customBallActiveColor ?: defaults.ball.activeColor, onColorSelected = { vm.customBallActiveColor = it })
                ColorPalette(label = "Idle Color", selectedColor = vm.customBallIdleColor ?: defaults.ball.idleColor, onColorSelected = { vm.customBallIdleColor = it })
                ColorPalette(label = "Warm Amber", selectedColor = vm.customBallWarmAmberColor ?: defaults.ball.warmAmberColor, onColorSelected = { vm.customBallWarmAmberColor = it })
                DescribedSlider(
                    label = "Smoothing", description = "Low = jittery, responsive. High = smooth, laggy.",
                    value = vm.customSmoothingStiffness ?: defaults.ball.smoothingStiffness,
                    onValueChange = { vm.customSmoothingStiffness = it },
                    valueRange = 100f..5000f, suffix = "", valueFormat = "%.0f",
                )
                DescribedSlider(
                    label = "Ready Stiffness", description = "Reference-tracking smoothing. Low = loose. High = tight.",
                    value = vm.customReadyStiffness ?: defaults.ball.readyStiffness,
                    onValueChange = { vm.customReadyStiffness = it },
                    valueRange = 100f..1000f, suffix = "", valueFormat = "%.0f",
                )
                DescribedSlider(
                    label = "Pulse Min", description = "Minimum pulse scale. Low = dramatic shrink.",
                    value = vm.customPulseScaleMin ?: defaults.ball.pulseScaleMin,
                    onValueChange = { vm.customPulseScaleMin = it },
                    valueRange = 0.8f..1.0f, suffix = "x", valueFormat = "%.2f",
                )
                DescribedSlider(
                    label = "Pulse Max", description = "Maximum pulse scale. High = dramatic grow.",
                    value = vm.customPulseScaleMax ?: defaults.ball.pulseScaleMax,
                    onValueChange = { vm.customPulseScaleMax = it },
                    valueRange = 1.0f..1.3f, suffix = "x", valueFormat = "%.2f",
                )
                DescribedSlider(
                    label = "Pulse Period", description = "Active pulse speed. Low = rapid. High = slow.",
                    value = (vm.customPulsePeriodMs ?: defaults.ball.pulsePeriodMs).toFloat(),
                    onValueChange = { vm.customPulsePeriodMs = it.toInt() },
                    valueRange = 200f..2000f, suffix = "ms", valueFormat = "%.0f",
                )
                DescribedSlider(
                    label = "Idle Pulse", description = "Ready pulse speed. Low = anxious. High = calm.",
                    value = (vm.customIdlePulsePeriodMs ?: defaults.ball.idlePulsePeriodMs).toFloat(),
                    onValueChange = { vm.customIdlePulsePeriodMs = it.toInt() },
                    valueRange = 200f..3000f, suffix = "ms", valueFormat = "%.0f",
                )
            }
        }

        HorizontalDivider()

        // 2. Performance Trail
        CollapsibleSection(
            title = "Performance Trail",
            description = "Fading trail behind the pitch ball showing recent singing history.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorPalette(label = "Warm Color", selectedColor = vm.customPerformancePitchColor ?: defaults.trail.warmColor, onColorSelected = { vm.customPerformancePitchColor = it })
                ColorPalette(label = "Cool Color", selectedColor = vm.customTrailCoolColor ?: defaults.trail.coolColor, onColorSelected = { vm.customTrailCoolColor = it })
                DescribedSlider(
                    label = "Duration", description = "Trail length. Low = short tail. High = long history.",
                    value = vm.customTrailDuration ?: defaults.trail.durationSeconds,
                    onValueChange = { vm.customTrailDuration = it },
                    valueRange = 0.5f..5.0f, suffix = "s", valueFormat = "%.1f",
                )
                DescribedSlider(
                    label = "Head Width", description = "Leading edge thickness.",
                    value = vm.customTrailHeadWidth ?: defaults.trail.headWidth.value,
                    onValueChange = { vm.customTrailHeadWidth = it },
                    valueRange = 1f..12f,
                )
                DescribedSlider(
                    label = "Tail Width", description = "Trailing edge. Zero = tapers to point.",
                    value = vm.customTrailTailWidth ?: defaults.trail.tailWidth.value,
                    onValueChange = { vm.customTrailTailWidth = it },
                    valueRange = 0f..8f,
                )
                DescribedSlider(
                    label = "Head Alpha", description = "Leading opacity. Low = ghostly. High = solid.",
                    value = vm.customTrailHeadAlpha ?: defaults.trail.headAlpha,
                    onValueChange = { vm.customTrailHeadAlpha = it },
                    valueRange = 0f..1f, suffix = "", valueFormat = "%.2f",
                )
            }
        }

        HorizontalDivider()

        // 3. Reference Contour
        CollapsibleSection(
            title = "Reference Contour",
            description = "Teacher's reference pitch line shown during Listen and as a guide.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorPalette(label = "Color", selectedColor = vm.customContourColor ?: defaults.referenceContour.color, onColorSelected = { vm.customContourColor = it })
                DescribedSlider(
                    label = "Width", description = "Line thickness.",
                    value = vm.customContourWidth ?: defaults.referenceContour.width.value,
                    onValueChange = { vm.customContourWidth = it },
                    valueRange = 1f..8f,
                )
                DescribedSlider(
                    label = "Head Dot Radius", description = "Current-time dot size.",
                    value = vm.customHeadDotRadius ?: defaults.referenceContour.headDotRadius.value,
                    onValueChange = { vm.customHeadDotRadius = it },
                    valueRange = 1f..8f,
                )
                DescribedSlider(
                    label = "Head Glow Radius", description = "Glow around the dot.",
                    value = vm.customHeadGlowRadius ?: defaults.referenceContour.headGlowRadius.value,
                    onValueChange = { vm.customHeadGlowRadius = it },
                    valueRange = 1f..12f,
                )
                DescribedSlider(
                    label = "Head Glow Alpha", description = "Glow intensity. Low = none. High = bright halo.",
                    value = vm.customHeadGlowAlpha ?: defaults.referenceContour.headGlowAlpha,
                    onValueChange = { vm.customHeadGlowAlpha = it },
                    valueRange = 0f..1f, suffix = "", valueFormat = "%.2f",
                )
                DescribedSlider(
                    label = "Guide BG Width", description = "Translucent corridor width behind guide.",
                    value = vm.customGuideBGWidthMultiplier ?: defaults.referenceContour.guideBackgroundWidthMultiplier,
                    onValueChange = { vm.customGuideBGWidthMultiplier = it },
                    valueRange = 1f..6f, suffix = "x", valueFormat = "%.1f",
                )
                DescribedSlider(
                    label = "Guide BG Alpha", description = "Corridor opacity. Low = invisible. High = visible band.",
                    value = vm.customGuideBGAlpha ?: defaults.referenceContour.guideBackgroundAlpha,
                    onValueChange = { vm.customGuideBGAlpha = it },
                    valueRange = 0f..0.5f, suffix = "", valueFormat = "%.2f",
                )
                DescribedSlider(
                    label = "Guide FG Alpha", description = "Line opacity in guide role.",
                    value = vm.customGuideFGAlpha ?: defaults.referenceContour.guideForegroundAlpha,
                    onValueChange = { vm.customGuideFGAlpha = it },
                    valueRange = 0f..1f, suffix = "", valueFormat = "%.2f",
                )
                DescribedSwitch(
                    label = "Use Confidence",
                    description = "Line thickness varies with pitch detection confidence.",
                    checked = vm.customUseConfidence ?: defaults.referenceContour.useConfidence,
                    onCheckedChange = { vm.customUseConfidence = it },
                )
            }
        }

        HorizontalDivider()

        // 4. Note Bars (Exercise mode only)
        if (vm.sessionMode == SessionMode.Exercise) {
            CollapsibleSection(
                title = "Note Bars",
                description = "Rounded rectangles representing individual notes in Exercise mode.",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ColorPalette(label = "Note Color", selectedColor = vm.customNoteColor ?: defaults.noteBars.noteColor, onColorSelected = { vm.customNoteColor = it })
                    DescribedSlider(
                        label = "Bar Height", description = "Low = thin lines. High = thick blocks.",
                        value = vm.customBarHeight ?: defaults.noteBars.barHeight.value,
                        onValueChange = { vm.customBarHeight = it },
                        valueRange = 4f..24f,
                    )
                    DescribedSlider(
                        label = "Corner Radius", description = "Zero = sharp rectangles. High = pill-shaped.",
                        value = vm.customBarCornerRadius ?: defaults.noteBars.cornerRadius.value,
                        onValueChange = { vm.customBarCornerRadius = it },
                        valueRange = 0f..12f,
                    )
                    DescribedSlider(
                        label = "Label Alpha", description = "Solfege text opacity. Low = faded. High = crisp.",
                        value = vm.customLabelAlpha ?: defaults.noteBars.labelAlpha,
                        onValueChange = { vm.customLabelAlpha = it },
                        valueRange = 0f..1f, suffix = "", valueFormat = "%.2f",
                    )
                    DescribedSlider(
                        label = "Label Font Size", description = "Text size inside bars.",
                        value = vm.customLabelFontSize ?: defaults.noteBars.labelFontSize.value,
                        onValueChange = { vm.customLabelFontSize = it },
                        valueRange = 6f..16f, suffix = "sp", valueFormat = "%.0f",
                    )
                }
            }

            HorizontalDivider()
        }

        // 5. Now Line
        CollapsibleSection(
            title = "Now Line",
            description = "Vertical line marking the current playback position.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorPalette(label = "Color", selectedColor = vm.customNowLineColor ?: defaults.nowLine.color, onColorSelected = { vm.customNowLineColor = it })
                DescribedSlider(
                    label = "Width", description = "Line thickness.",
                    value = vm.customNowLineWidth ?: defaults.nowLine.width.value,
                    onValueChange = { vm.customNowLineWidth = it },
                    valueRange = 0.5f..4f,
                )
                DescribedSlider(
                    label = "Glow Width", description = "Glow spread. Zero = no glow.",
                    value = vm.customNowLineGlowWidth ?: defaults.nowLine.glowWidth.value,
                    onValueChange = { vm.customNowLineGlowWidth = it },
                    valueRange = 0f..60f,
                )
                DescribedSlider(
                    label = "Peak Alpha", description = "Center glow intensity.",
                    value = vm.customNowLinePeakAlpha ?: defaults.nowLine.peakAlpha,
                    onValueChange = { vm.customNowLinePeakAlpha = it },
                    valueRange = 0f..0.5f, suffix = "", valueFormat = "%.2f",
                )
                DescribedSlider(
                    label = "Glow Alpha", description = "Edge glow intensity.",
                    value = vm.customNowLineGlowAlpha ?: defaults.nowLine.glowAlpha,
                    onValueChange = { vm.customNowLineGlowAlpha = it },
                    valueRange = 0f..0.2f, suffix = "", valueFormat = "%.3f",
                )
            }
        }

        HorizontalDivider()

        // 6. Score Flash
        CollapsibleSection(
            title = "Score Flash",
            description = "Animated score display appearing between segments.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorPalette(label = "Text Color", selectedColor = vm.customScoreFlashTextColor ?: defaults.scoreFlash.textColor, onColorSelected = { vm.customScoreFlashTextColor = it })
                DescribedSlider(
                    label = "Text Size", description = "Low = subtle. High = dramatic.",
                    value = vm.customScoreFlashTextSize ?: defaults.scoreFlash.textSize.value,
                    onValueChange = { vm.customScoreFlashTextSize = it },
                    valueRange = 14f..48f, suffix = "sp", valueFormat = "%.0f",
                )
                DescribedSlider(
                    label = "Fade In", description = "Appearance speed.",
                    value = (vm.customScoreFlashFadeInMs ?: defaults.scoreFlash.fadeInMs).toFloat(),
                    onValueChange = { vm.customScoreFlashFadeInMs = it.toInt() },
                    valueRange = 50f..500f, suffix = "ms", valueFormat = "%.0f",
                )
                DescribedSlider(
                    label = "Hold", description = "Visible duration.",
                    value = (vm.customScoreFlashHoldMs ?: defaults.scoreFlash.holdMs).toFloat(),
                    onValueChange = { vm.customScoreFlashHoldMs = it.toInt() },
                    valueRange = 200f..1500f, suffix = "ms", valueFormat = "%.0f",
                )
                DescribedSlider(
                    label = "Fade Out", description = "Disappearance speed.",
                    value = (vm.customScoreFlashFadeOutMs ?: defaults.scoreFlash.fadeOutMs).toFloat(),
                    onValueChange = { vm.customScoreFlashFadeOutMs = it.toInt() },
                    valueRange = 100f..1000f, suffix = "ms", valueFormat = "%.0f",
                )
            }
        }

        HorizontalDivider()

        // 7. Grid Lines
        CollapsibleSection(
            title = "Grid Lines",
            description = "Horizontal pitch reference lines across the canvas.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorPalette(label = "Line Color", selectedColor = vm.customGridLineColor ?: defaults.grid.lineColor, onColorSelected = { vm.customGridLineColor = it })
                ColorPalette(label = "Highlighted Color", selectedColor = vm.customGridHighlightedLineColor ?: defaults.grid.highlightedLineColor, onColorSelected = { vm.customGridHighlightedLineColor = it })
                DescribedSlider(
                    label = "Line Width", description = "Line thickness.",
                    value = vm.customGridLineWidth ?: defaults.grid.lineWidth.value,
                    onValueChange = { vm.customGridLineWidth = it },
                    valueRange = 0.25f..2f,
                )
                ColorPalette(label = "Label Color", selectedColor = vm.customGridLabelTextColor ?: defaults.grid.labelTextColor, onColorSelected = { vm.customGridLabelTextColor = it })
                DescribedSlider(
                    label = "Label Size", description = "Default label font size.",
                    value = vm.customGridLabelTextSize ?: defaults.grid.labelTextSize.value,
                    onValueChange = { vm.customGridLabelTextSize = it },
                    valueRange = 8f..16f, suffix = "sp", valueFormat = "%.0f",
                )
                DescribedSlider(
                    label = "Highlighted Label Size", description = "Font size for highlighted labels.",
                    value = vm.customGridHighlightedLabelTextSize ?: defaults.grid.highlightedLabelTextSize.value,
                    onValueChange = { vm.customGridHighlightedLabelTextSize = it },
                    valueRange = 8f..20f, suffix = "sp", valueFormat = "%.0f",
                )
                ColorPalette(label = "Highlighted Label Color", selectedColor = vm.customGridHighlightedLabelTextColor ?: defaults.grid.highlightedLabelTextColor, onColorSelected = { vm.customGridHighlightedLabelTextColor = it })
                ColorPalette(label = "Label Background", selectedColor = vm.customGridLabelBackgroundColor ?: defaults.grid.labelBackgroundColor, onColorSelected = { vm.customGridLabelBackgroundColor = it })
            }
        }

        HorizontalDivider()

        // 8. Segment Bands
        CollapsibleSection(
            title = "Segment Bands",
            description = "Vertical color bands marking segment boundaries.",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorPalette(label = "Reference Color", selectedColor = vm.customSegmentReferenceColor ?: defaults.segmentBands.referenceColor, onColorSelected = { vm.customSegmentReferenceColor = it })
                ColorPalette(label = "Performance Color", selectedColor = vm.customSegmentPerformanceColor ?: defaults.segmentBands.performanceColor, onColorSelected = { vm.customSegmentPerformanceColor = it })
                ColorPalette(label = "Commentary Color", selectedColor = vm.customSegmentCommentaryColor ?: defaults.segmentBands.commentaryColor, onColorSelected = { vm.customSegmentCommentaryColor = it })
            }
        }
    }
}

private fun phaseChipColor(phase: SessionPhase): Color = when (phase) {
    SessionPhase.LISTEN -> Color(0xFF4A90D9)
    SessionPhase.SING -> Color(0xFF27AE60)
    SessionPhase.COMMENTARY -> Color(0xFF7F8C8D)
    SessionPhase.PREP -> Color(0xFF9B59B6)
}
