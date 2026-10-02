package com.musicmuni.voxavis.sample.sections.navigation.segmentscrubber.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.musicmuni.voxavis.model.ScoreThresholds
import com.musicmuni.voxavis.model.Segment
import com.musicmuni.voxavis.sample.shared.MockData
import com.musicmuni.voxavis.theme.ScoreBandMode

/** A score the learner just earned on one phrase, and when. */
data class PhraseScored(val index: Int, val score: Float, val atMs: Long)

class SegmentScrubberViewModel : ViewModel() {

    val clockMs = mutableLongStateOf(0L)
    var playing by mutableStateOf(true)

    var crowded by mutableStateOf(false)
        private set
    var segments by mutableStateOf(MockData.scrubberPhrases(crowded = false))
        private set
    val totalDurationMs: Long get() = segments.last().endTimeMs + 500L

    var interactive by mutableStateOf(true)
    var focusCurrent by mutableStateOf(false)
    var bandMode by mutableStateOf(ScoreBandMode.Discrete)
    var queued by mutableStateOf(emptySet<Int>())
        private set
    var lastScored by mutableStateOf<PhraseScored?>(null)
        private set
    var preview by mutableStateOf<Int?>(null)

    /** The phrase under the playhead, or the last one it passed. */
    val currentIndex by derivedStateOf {
        val now = clockMs.longValue
        segments.indexOfLast { it.startTimeMs <= now }.coerceAtLeast(0)
    }

    /** The latest score, for as long as its chip stays up. */
    val visibleScore by derivedStateOf {
        lastScored?.takeIf { clockMs.longValue - it.atMs in 0L until CHIP_MS }
    }

    val events = emptyList<String>().toMutableStateList()

    fun showCrowded(value: Boolean) {
        crowded = value
        segments = MockData.scrubberPhrases(crowded = value)
        clockMs.longValue = 0L
        queued = emptySet()
        lastScored = null
    }

    /**
     * Moves the playhead and scores a phrase when the playhead plays through
     * its end. A phrase a seek jumped over was not sung, so it is not scored.
     * At the end of the lesson the pass starts again: what was scored becomes
     * last pass's memory, drawn hollow, and this pass starts with nothing.
     */
    fun onFrame(elapsedMs: Long) {
        val before = clockMs.longValue
        val now = before + elapsedMs
        if (now >= totalDurationMs) {
            segments = segments.map { segment ->
                if (segment.score >= 0f) {
                    segment.copy(previousScore = segment.score, score = ScoreThresholds.NOT_PRACTICED)
                } else {
                    segment
                }
            }
            clockMs.longValue = 0L
            return
        }
        clockMs.longValue = now
        segments.forEachIndexed { index, segment ->
            if (segment.score < 0f && segment.endTimeMs in (before + 1)..now) score(index, now)
        }
    }

    fun seekTo(index: Int) {
        clockMs.longValue = segments[index].startTimeMs
        log("Seek: phrase ${index + 1}")
    }

    fun log(event: String) {
        events.add(0, event)
        if (events.size > 5) events.removeRange(5, events.size)
    }

    private fun score(index: Int, now: Long) {
        val score = MockData.scrubberScore(index)
        segments = segments.mapIndexed { i, segment -> if (i == index) segment.copy(score = score) else segment }
        queued = if (score < 0.6f) queued + index else queued - index
        lastScored = PhraseScored(index, score, now)
    }

    private companion object {
        const val CHIP_MS = 1500L
    }
}
