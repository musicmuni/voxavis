import Foundation
import Combine
import voxavis

final class LyricsOverlayDemoViewModel: ObservableObject {
    @Published var isPlaying: Bool = true
    @Published var currentTimeMs: Int64 = 0
    @Published var isExpanded: Bool = false
    @Published var visibleItemCount: Int = 3

    let totalDurationMs = MockData.totalDurationMs

    lazy var state: LyricsOverlayState = {
        let lyricsSegments = MockData.segments().filter { $0.lyrics != nil }
        let s = LyricsOverlayState(
            segments: lyricsSegments,
            currentTimeMs: 0,
            isExpanded: false,
            visibleItemCount: Int32(visibleItemCount)
        )
        s.onExpandToggle = { [weak self] in
            self?.isExpanded.toggle()
        }
        return s
    }()

    func tick() {
        guard isPlaying else { return }
        currentTimeMs = (currentTimeMs + 16) % totalDurationMs
        state.currentTimeMs = currentTimeMs
    }
}
