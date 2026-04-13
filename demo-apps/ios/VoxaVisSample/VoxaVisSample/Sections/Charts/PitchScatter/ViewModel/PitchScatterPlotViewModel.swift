import Foundation
import Combine
import voxavis

final class PitchScatterPlotDemoViewModel: ObservableObject {
    @Published var pointRadius: Float = 2
    @Published var showConnectingLine: Bool = true

    lazy var state: PitchScatterPlotState = PitchScatterPlotState(
        contour: MockData.pitchContour(),
        timeRangeStartMs: nil,
        timeRangeEndMs: nil,
        centsRangeMin: nil,
        centsRangeMax: nil,
        pointRadius: pointRadius,
        pointColor: 0xFFFFBF2C,
        showConnectingLine: showConnectingLine
    )
}
