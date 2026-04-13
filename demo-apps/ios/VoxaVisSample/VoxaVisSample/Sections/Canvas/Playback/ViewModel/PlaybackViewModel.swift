import Foundation
import Combine
import voxavis

final class PlaybackViewModel: ObservableObject {
    @Published var isPlaying = true
    @Published var currentTimeMs: Int64 = 0

    let canvasState: VoxaVisState
    private let lessonData = MockDataProvider.loadPlaybackLesson()

    init() {
        canvasState = VoxaVisState(
            sessionMode: SessionMode.singafter,
            minPitchCents: -600,
            maxPitchCents: 600,
            trackLengthMs: lessonData.durationMs
        )

        let notes = (lessonData.notes ?? []).map { n in
            ScoreNote.create(
                startTimeMs: n.startTimeMs,
                endTimeMs: n.endTimeMs,
                cents: Float(n.cents),
                label: n.label,
                segmentType: SegmentType.from(string: n.segmentType)
            )
        }
        canvasState.setNotes(notes: notes)

        let segments = (lessonData.segments ?? []).map { s in
            Segment.create(
                startTimeMs: s.startTimeMs,
                endTimeMs: s.endTimeMs,
                type: SegmentType.from(string: s.type),
                lyrics: s.lyrics
            )
        }
        canvasState.setSegments(segments: segments)

        let gridLines = (lessonData.gridLines ?? []).map { g in
            GridLine.create(
                cents: Float(g.cents),
                label: g.label,
                isHighlighted: g.isHighlighted
            )
        }
        canvasState.setGridLines(gridLines: gridLines)

        if let referencePitch = lessonData.referencePitch {
            let contourPoints = referencePitch.map { p in
                PitchPoint.create(
                    timestampMs: p.timestampMs,
                    freqHz: Float(p.freqHz),
                    cents: Float(p.cents)
                )
            }
            canvasState.referencePitch = PitchContour(points: contourPoints)
        }
    }

    var totalDurationMs: Int64 { lessonData.durationMs }

    func tick() {
        guard isPlaying, currentTimeMs < totalDurationMs else { return }
        currentTimeMs += 16
        canvasState.currentTimeMs = currentTimeMs
    }
}
