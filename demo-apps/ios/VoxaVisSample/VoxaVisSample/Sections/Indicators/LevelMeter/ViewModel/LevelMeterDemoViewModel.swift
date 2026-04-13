import Foundation
import Combine
import voxavis

final class LevelMeterDemoViewModel: ObservableObject {
    @Published var level: Float = 0.5
    @Published var segmentCount: Int = 20
    @Published var isVertical: Bool = true
    @Published var animate: Bool = true

    lazy var state: LevelMeterState = LevelMeterState(
        level: level,
        peakHoldMs: 1000,
        segmentCount: Int32(segmentCount),
        orientation: isVertical ? Orientation.vertical : Orientation.horizontal,
        normalColor: 0xFF4CAF50,
        loudColor: 0xFFFFEB3B,
        clippingColor: 0xFFF44336,
        loudThreshold: 0.7,
        clippingThreshold: 0.9
    )

    func tick(timeMs: Int64) {
        guard animate else { return }
        level = MockData.animatedValue(timeMs: timeMs, periodMs: 600, amplitude: 0.4, offset: 0.5).clamped(to: 0...1)
        state.level = level
    }

    func rebuildState() {
        state = LevelMeterState(
            level: level,
            peakHoldMs: 1000,
            segmentCount: Int32(segmentCount),
            orientation: isVertical ? Orientation.vertical : Orientation.horizontal,
            normalColor: 0xFF4CAF50,
            loudColor: 0xFFFFEB3B,
            clippingColor: 0xFFF44336,
            loudThreshold: 0.7,
            clippingThreshold: 0.9
        )
    }
}

private extension Float {
    func clamped(to range: ClosedRange<Float>) -> Float {
        Swift.min(Swift.max(self, range.lowerBound), range.upperBound)
    }
}
