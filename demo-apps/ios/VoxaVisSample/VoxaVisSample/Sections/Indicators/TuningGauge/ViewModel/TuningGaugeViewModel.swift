import Foundation
import Combine
import voxavis

final class TuningGaugeDemoViewModel: ObservableObject {
    @Published var centsOff: Float = 0
    @Published var confidence: Float = 0.8
    @Published var noteLabel: String = "Sa"
    @Published var animate: Bool = true

    lazy var state: TuningGaugeState = TuningGaugeState(
        centsOff: centsOff,
        noteLabel: noteLabel,
        confidence: confidence,
        inTuneThreshold: 10,
        gaugeRange: 50,
        inTuneColor: 0xFF4CAF50,
        slightlyOffColor: 0xFFFFEB3B,
        veryOffColor: 0xFFF44336,
        needleColor: 0xFFFFFFFF
    )

    func tick(timeMs: Int64) {
        guard animate else { return }
        centsOff = MockData.animatedValue(timeMs: timeMs, periodMs: 2000, amplitude: 30, offset: 0)
        confidence = MockData.animatedValue(timeMs: timeMs, periodMs: 3000, amplitude: 0.4, offset: 0.6).clamped(to: 0...1)
        state.centsOff = centsOff
        state.confidence = confidence
    }

    func updateNote(_ note: String) {
        noteLabel = note
        state.noteLabel = note
    }
}

private extension Float {
    func clamped(to range: ClosedRange<Float>) -> Float {
        Swift.min(Swift.max(self, range.lowerBound), range.upperBound)
    }
}
