import Foundation
import Combine
import voxavis

final class SegmentedSeekBarDemoViewModel: ObservableObject {
    @Published var isPlaying: Bool = true
    @Published var currentTimeMs: Int64 = 0
    @Published var lastTappedInfo: String = ""

    let totalDurationMs = MockData.totalDurationMs

    lazy var state: SegmentedSeekBarState = {
        let s = SegmentedSeekBarState(
            segments: MockData.segments(),
            totalDurationMs: totalDurationMs,
            currentTimeMs: 0,
            barHeight: 6,
            gapMinimumWeight: 0.002
        )
        s.onSegmentTapped { [weak self] index, forward in
            self?.lastTappedInfo = "Segment \(index) (\(forward ? "forward" : "backward"))"
        }
        return s
    }()

    func tick() {
        guard isPlaying else { return }
        currentTimeMs = (currentTimeMs + 16) % totalDurationMs
        state.currentTimeMs = currentTimeMs
    }
}
