import Foundation
import Combine
import voxavis

final class FreestyleViewModel: ObservableObject {
    @Published var isPlaying = true
    @Published var currentTimeMs: Int64 = 0
    @Published var showPitchBall = true

    let canvasState: VoxaVisState

    init() {
        canvasState = VoxaVisState(
            sessionMode: SessionMode.singafter,
            minPitchCents: -600,
            maxPitchCents: 1400,
            trackLengthMs: Int64.max
        )
        canvasState.performancePitch = CircularPitchBuffer(capacity: 1000)
        let gridLines = [
            GridLine.create(cents: -200, label: "Sa", isHighlighted: true),
            GridLine.create(cents: 0, label: "Re"),
            GridLine.create(cents: 200, label: "Ga"),
            GridLine.create(cents: 500, label: "Ma"),
            GridLine.create(cents: 700, label: "Pa", isHighlighted: true),
            GridLine.create(cents: 900, label: "Dha"),
            GridLine.create(cents: 1100, label: "Ni"),
            GridLine.create(cents: 1200, label: "Sa'", isHighlighted: true),
        ]
        canvasState.setGridLines(gridLines: gridLines)
    }

    func tick() {
        guard isPlaying else { return }
        currentTimeMs += 16
        canvasState.currentTimeMs = currentTimeMs
    }
}
