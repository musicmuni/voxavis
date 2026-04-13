import Foundation
import Combine
import voxavis

final class MinimalViewModel: ObservableObject {
    @Published var isPlaying = true
    @Published var currentTimeMs: Int64 = 0
    @Published var showGridLines = true

    let canvasState: VoxaVisState
    private let performancePitchData = MockDataProvider.loadPerformancePitch()

    init() {
        canvasState = VoxaVisState(
            sessionMode: SessionMode.singafter,
            minPitchCents: -200,
            maxPitchCents: 900,
            trackLengthMs: Int64.max
        )
        canvasState.performancePitch = CircularPitchBuffer(capacity: 1000)
        let gridLines = [
            GridLine.create(cents: 0, label: "Sa", isHighlighted: true),
            GridLine.create(cents: 700, label: "Pa", isHighlighted: true),
        ]
        canvasState.setGridLines(gridLines: gridLines)
    }

    func tick() {
        guard isPlaying else { return }
        currentTimeMs += 16
        canvasState.currentTimeMs = currentTimeMs
        if !performancePitchData.pitchPoints.isEmpty,
           let pitchPoint = performancePitchData.getPitchAt(currentTimeMs % performancePitchData.pitchPoints.last!.timestampMs),
           let buffer = canvasState.performancePitch {
            buffer.addBlocking(
                timestampMs: pitchPoint.timestampMs,
                freqHz: Float(pitchPoint.freqHz),
                cents: Float(pitchPoint.cents)
            )
        }
    }
}
