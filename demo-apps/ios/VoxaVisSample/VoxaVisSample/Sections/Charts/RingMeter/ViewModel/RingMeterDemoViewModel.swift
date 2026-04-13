import Foundation
import Combine
import voxavis

final class RingMeterDemoViewModel: ObservableObject {
    @Published var value: Float = 0.7
    @Published var animate: Bool = false

    lazy var state: RingMeterState = RingMeterState(
        value: value,
        displayText: "\(Int(value * 25))",
        unitText: "seconds",
        minRadius: 70,
        maxRadius: 110,
        innerRadius: 65,
        ringColor: 0xFFFFEB3B,
        innerColor: 0xFFFFFFFF
    )

    func tick(timeMs: Int64) {
        guard animate else { return }
        value = MockData.animatedValue(timeMs: timeMs, periodMs: 4000, amplitude: 0.4, offset: 0.5).clamped(to: 0...1)
        state.value = value
    }

    func setManualValue(_ newValue: Float) {
        value = newValue
        state.value = newValue
    }
}

private extension Float {
    func clamped(to range: ClosedRange<Float>) -> Float {
        return Swift.min(Swift.max(self, range.lowerBound), range.upperBound)
    }
}
