import Foundation
import Combine
import voxavis

/// The least a canvas needs: a time span, two lines and a singer.
final class MinimalViewModel: ObservableObject {
    static let durationMs: Int64 = 180_000

    let model = CanvasDemoModel(
        resources: SingingPracticeResources.create(
            mode: .singalong,
            trackLengthMs: MinimalViewModel.durationMs,
            // The learner's line is drawn only where the learner sings, so the
            // whole track is one learner's turn.
            segments: [Segment.create(startTimeMs: 0, endTimeMs: MinimalViewModel.durationMs, type: .performance)],
            gridLines: [
                GridLine.create(cents: 0, label: "Sa", isHighlighted: true, priority: GridLine.companion.PRIORITY_ANCHOR),
                GridLine.create(cents: 700, label: "Pa", isHighlighted: true),
                GridLine.create(cents: 1200, label: "Sa'", isHighlighted: true, priority: GridLine.companion.PRIORITY_ANCHOR),
            ]
        ),
        config: SingingPracticeConfig.create(minPitchCents: -200, maxPitchCents: 1400),
        singer: DemoLesson.freeSinging(durationMs: MinimalViewModel.durationMs)
    )
}
