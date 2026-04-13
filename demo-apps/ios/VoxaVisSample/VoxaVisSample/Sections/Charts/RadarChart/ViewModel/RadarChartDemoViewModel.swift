import Foundation
import Combine
import voxavis

final class RadarChartDemoViewModel: ObservableObject {
    @Published var gridRingCount: Int = 4
    @Published var showBest: Bool = true

    lazy var state: RadarChartState = RadarChartState(
        metrics: MockData.spiderMetrics(),
        currentColor: 0xFF4ECDC4,
        currentColorAlpha: 0.3,
        currentStrokeColor: 0xFF4ECDC4,
        bestColor: 0xFFFFE66D,
        bestColorAlpha: 0.2,
        bestStrokeColor: 0xFFFFE66D,
        gridColor: 0x40FFFFFF,
        gridRingCount: Int32(gridRingCount)
    )

    func randomize() {
        state.setMetrics(metrics: MockData.randomSpiderMetrics())
    }
}
