import Foundation
import Combine
import voxavis

final class ConfidenceMeterDemoViewModel: ObservableObject {
    @Published var confidence: Float = 0.7
    @Published var isHorizontal: Bool = true
    @Published var showLabel: Bool = true
    @Published var animate: Bool = true

    lazy var state: ConfidenceMeterState = ConfidenceMeterState(
        confidence: confidence,
        orientation: isHorizontal ? Orientation.horizontal : Orientation.vertical,
        showLabel: showLabel,
        lowColor: 0xFFF44336,
        mediumColor: 0xFFFFEB3B,
        highColor: 0xFF4CAF50,
        lowThreshold: 0.3,
        highThreshold: 0.7
    )

    func tick(timeMs: Int64) {
        guard animate else { return }
        confidence = MockData.animatedValue(timeMs: timeMs, periodMs: 2500, amplitude: 0.45, offset: 0.5).clamped(to: 0...1)
        state.confidence = confidence
    }

    func rebuildState() {
        state = ConfidenceMeterState(
            confidence: confidence,
            orientation: isHorizontal ? Orientation.horizontal : Orientation.vertical,
            showLabel: showLabel,
            lowColor: 0xFFF44336,
            mediumColor: 0xFFFFEB3B,
            highColor: 0xFF4CAF50,
            lowThreshold: 0.3,
            highThreshold: 0.7
        )
    }
}

private extension Float {
    func clamped(to range: ClosedRange<Float>) -> Float {
        Swift.min(Swift.max(self, range.lowerBound), range.upperBound)
    }
}
