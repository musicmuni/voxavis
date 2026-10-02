package com.musicmuni.voxavis.sample.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameMillis

/**
 * Calls [onFrame] once per displayed frame while [running] is true, with the
 * milliseconds since the previous frame.
 *
 * This is where a host writes its playhead: into snapshot state, once a frame.
 * VoxaVis then reads that state through a remembered reader (`{ clockMs.longValue }`)
 * inside its own draw, so a new frame moves a transform instead of recomposing
 * the canvas (ADR-013). A real app would take the time from its audio player
 * here instead of counting frames.
 */
@Composable
fun EveryFrame(running: Boolean, onFrame: (elapsedMs: Long) -> Unit) {
    val latest by rememberUpdatedState(onFrame)
    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        var previous = withFrameMillis { it }
        while (true) {
            withFrameMillis { now ->
                latest(now - previous)
                previous = now
            }
        }
    }
}
