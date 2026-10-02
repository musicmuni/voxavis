import Foundation
import Combine
import voxavis

/// One phrase drilled three times. Each take is the same recording placed
/// further along the canvas's clock, opened by a four-beat count and beaten
/// under by a four-beat cycle. The take that is ending scrolls away while the
/// next one arrives; nothing cuts.
final class TakesViewModel: ObservableObject {
    static let takes = 3
    static let beatMs = DemoLesson.noteMs
    static let countBeats: Int64 = 4

    let model: CanvasDemoModel

    init() {
        let phrase = DemoLesson.phrases[1]
        let recording = DemoLesson.singAfter(phrases: [phrase])
        let lengthMs = recording.trackLengthMs
        let beatMs = TakesViewModel.beatMs
        let countMs = TakesViewModel.countBeats * beatMs

        // The count, on the take's own clock: negative, ahead of where it starts.
        let count = LeadInMarks(marks: (1...TakesViewModel.countBeats).map { beat in
            LeadInMark.create(
                timeMs: -(TakesViewModel.countBeats - beat + 1) * beatMs,
                kind: .numeral,
                label: "\(beat)"
            )
        })
        let cycle = MetricLaneCycle.create(
            weights: [.primary, .plain, .secondary, .plain],
            beatMs: Float(beatMs)
        )

        // Each take sits one count after the previous one ends.
        var placements: [TakePlacement] = []
        var singer: [PitchPoint] = []
        var anchor = countMs
        for _ in 0..<TakesViewModel.takes {
            placements.append(TakePlacement.create(
                anchorMs: anchor,
                fromMs: 0,
                toMs: lengthMs,
                resources: recording,
                metricLane: cycle,
                leadIn: count
            ))
            // The learner answers in the second half of each take.
            singer += DemoLesson.sungLine(phrase, startMs: anchor + lengthMs / 2 + 90, centsOffset: 15)
            anchor += lengthMs + countMs
        }

        model = CanvasDemoModel(
            resources: recording,
            config: SingingPracticeConfig.create(minPitchCents: -300, maxPitchCents: 1500),
            singer: singer,
            durationMs: anchor
        )
        model.canvasState.takes = placements
    }
}
