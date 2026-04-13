import Foundation
import Combine
import voxavis

final class ScoreTrendChartDemoViewModel: ObservableObject {
    @Published var bezierCurve: Bool = true
    @Published var showGrid: Bool = true
    @Published var animate: Bool = true

    lazy var state: ScoreTrendChartState = ScoreTrendChartState(
        dataPoints: MockData.scoreTrendData(),
        bezierCurve: bezierCurve,
        showGrid: showGrid,
        yAxisInterval: 10,
        animate: animate,
        animationDurationPerPoint: 150,
        lineColor: 0xFF01C7FE,
        pointColor: 0xFFFFBF2C,
        gridColor: 0xFFBBBBBB
    )

    func addPoint() {
        let score = Float.random(in: 50...100)
        var points = MockData.scoreTrendData()
        points.append(ChartPoint.create(value: score, label: "\(Int(score))"))
        state.setDataPoints(points: points)
    }

    func reset() {
        state.setDataPoints(points: MockData.scoreTrendData())
    }
}
