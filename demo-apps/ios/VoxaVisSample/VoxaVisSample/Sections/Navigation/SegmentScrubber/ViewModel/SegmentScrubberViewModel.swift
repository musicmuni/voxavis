import Foundation
import Combine
import voxavis

final class SegmentScrubberDemoViewModel: ObservableObject {
    @Published var isPlaying = true
    @Published var info = "Drag along the bar"
    @Published var spotlightThird = false {
        didSet { state.focusedIndex = spotlightThird ? KotlinInt(value: 2) : nil }
    }
    @Published var markQueued = false {
        didSet { state.queuedIndices = markQueued ? [KotlinInt(value: 1), KotlinInt(value: 3)] : [] }
    }

    let totalDurationMs = MockData.totalDurationMs
    private var positionMs: Int64 = 0

    lazy var state: SegmentScrubberState = {
        let s = SegmentScrubberState(
            segments: MockData.segments(),
            totalDurationMs: totalDurationMs,
            barHeight: 16,
            touchHeight: 48,
            interactive: true
        )
        s.onScrubPreview { [weak self] index, timeMs in
            guard let self, let index, let timeMs else { return }
            self.info = "Segment \(index + 1) at \(timeMs / 1000)s"
        }
        s.onSeekCommitted { [weak self] index, timeMs in
            guard let self else { return }
            self.positionMs = timeMs
            self.info = "Seeked to segment \(index + 1)"
        }
        return s
    }()

    func tick() {
        guard isPlaying else { return }
        positionMs = (positionMs + 16) % totalDurationMs
        state.currentTimeMs = positionMs
    }
}
