import Foundation
import Combine
import voxavis

/// Free singing over the scale: no lesson, just the learner's line.
final class FreestyleViewModel: ObservableObject {
    static let durationMs: Int64 = 180_000

    let model = CanvasDemoModel(
        resources: SingingPracticeResources.create(
            mode: .singalong,
            trackLengthMs: FreestyleViewModel.durationMs,
            // The learner's line is drawn only where the learner sings, so the
            // whole track is one learner's turn.
            segments: [Segment.create(startTimeMs: 0, endTimeMs: FreestyleViewModel.durationMs, type: .performance)],
            gridLines: DemoLesson.gridLines()
        ),
        config: SingingPracticeConfig.create(
            minPitchCents: -300,
            maxPitchCents: 1500,
            restingPitchCents: 0
        ),
        singer: DemoLesson.freeSinging(durationMs: FreestyleViewModel.durationMs)
    )
}
