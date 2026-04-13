import Foundation
import Combine
import voxavis

final class VocalRangeChartDemoViewModel: ObservableObject {
    @Published var currentPitchCents: Float = 1400
    @Published var animate: Bool = true

    lazy var state: VocalRangeChartState = VocalRangeChartState(
        range: MockData.vocalRangeData(),
        currentPitchCents: KotlinFloat(value: currentPitchCents),
        showOctaveMarkers: true,
        barColor: 0xFF4ECDC4,
        markerColor: 0xFFFF6B6B,
        textColor: 0xFFE0E0E0
    )

    func tick(timeMs: Int64) {
        guard animate else { return }
        currentPitchCents = MockData.animatedValue(timeMs: timeMs, periodMs: 3000, amplitude: 1200, offset: 1400)
        state.currentPitchCentsSwift = currentPitchCents
    }

    func setManualPitch(_ cents: Float) {
        currentPitchCents = cents
        state.currentPitchCentsSwift = cents
    }
}
