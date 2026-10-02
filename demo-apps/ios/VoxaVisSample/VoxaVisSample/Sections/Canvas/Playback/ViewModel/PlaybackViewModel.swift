import Foundation
import Combine
import voxavis

/// Reviewing a sung lesson: the canvas and a segment scrubber on one clock.
final class PlaybackViewModel: ObservableObject {
    let model: CanvasDemoModel
    let scrubber: SegmentScrubberState
    @Published var scrubLabel = "Drag along the bar to seek"

    init() {
        // The learner's turns carry this session's scores; the bar colours them.
        let scores: [Float] = [0.92, 0.64, 0.38]
        let lesson = DemoLesson.singAfter()
        var answer = 0
        let segments: [Segment] = lesson.segments.map { s in
            guard s.type == .performance else { return s }
            defer { answer += 1 }
            return Segment.create(
                startTimeMs: s.startTimeMs,
                endTimeMs: s.endTimeMs,
                type: s.type,
                lyrics: s.lyrics,
                score: scores[answer % scores.count]
            )
        }
        let resources = SingingPracticeResources.create(
            mode: lesson.mode,
            trackLengthMs: lesson.trackLengthMs,
            segments: segments,
            notes: lesson.notes,
            gridLines: lesson.gridLines,
            referencePitch: lesson.referencePitch
        )
        model = CanvasDemoModel(
            resources: resources,
            config: SingingPracticeConfig.create(minPitchCents: -300, maxPitchCents: 1500),
            singer: DemoLesson.learnerAnswers()
        )
        scrubber = SegmentScrubberState(
            segments: segments,
            totalDurationMs: resources.trackLengthMs,
            barHeight: 16,
            touchHeight: 48,
            interactive: true
        )

        // One clock: the canvas's playhead drives the scrubber's thumb...
        model.onPositionChanged = { [scrubber] ms in scrubber.currentTimeMs = ms }
        // ...and a committed scrub seeks the canvas.
        scrubber.onSeekCommitted { [weak self] index, timeMs in
            self?.model.seek(toMs: timeMs)
            self?.scrubLabel = "Seeked to segment \(index + 1) at \(timeMs / 1000)s"
        }
        scrubber.onScrubPreview { [weak self] index, _ in
            guard let index else { return }
            self?.scrubLabel = "Segment \(index + 1): \(segments[index].lyrics ?? "")"
        }
    }
}
