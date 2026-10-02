import Foundation
import Combine
import QuartzCore
import voxavis

/// Drives a practice canvas the way an app's player would.
///
/// The playhead is written into `canvasState.currentTimeMs` once per frame and
/// the canvas reads it while drawing, so a playing lesson does not re-render
/// the SwiftUI view or recompose the canvas. The learner's pitch goes into the
/// canvas's `CircularPitchBuffer` as it "arrives", the way a pitch detector's
/// callback would feed it.
final class CanvasDemoModel: ObservableObject {
    @Published var isPlaying = true
    /// Whole seconds, for the label under the canvas. Published once a second
    /// rather than every frame, so the SwiftUI view does not redraw at 60 Hz.
    @Published private(set) var positionSeconds: Int64 = 0

    let canvasState: VoxaVisState
    let durationMs: Int64
    private(set) var positionMs: Int64 = 0
    /// Called with every new playhead position, for anything else that follows
    /// the same clock (a segment bar, lyrics).
    var onPositionChanged: ((Int64) -> Void)?

    private let singer: [PitchPoint]
    private var nextSingerPoint = 0
    private let loops: Bool

    /// - Parameters:
    ///   - resources: The lesson.
    ///   - singer: The learner's pitch, timestamped on the canvas clock.
    ///   - durationMs: Where the playhead stops (or wraps, with `loops`).
    init(
        resources: SingingPracticeResources,
        config: SingingPracticeConfig = SingingPracticeConfig.create(),
        singer: [PitchPoint] = [],
        durationMs: Int64? = nil,
        loops: Bool = false
    ) {
        canvasState = VoxaVisState(initialResources: resources)
        canvasState.config = config
        canvasState.performancePitch = CircularPitchBuffer(capacity: 1000)
        self.singer = singer
        self.durationMs = durationMs ?? resources.trackLengthMs
        self.loops = loops
    }

    /// Advance the playhead by real elapsed time until the view goes away.
    @MainActor
    func run() async {
        var last = CACurrentMediaTime()
        while !Task.isCancelled {
            try? await Task.sleep(nanoseconds: 16_000_000)
            let now = CACurrentMediaTime()
            if isPlaying { advance(byMs: Int64((now - last) * 1000)) }
            last = now
        }
    }

    func advance(byMs delta: Int64) {
        var next = positionMs + delta
        if next >= durationMs {
            guard loops else { isPlaying = false; return }
            next %= durationMs
            nextSingerPoint = 0
        }
        setPosition(next)
        feedSinger(upTo: next)
    }

    /// Jump the playhead, as a seek bar or a skip button would.
    func seek(toMs target: Int64) {
        let clamped = max(0, min(durationMs, target))
        setPosition(clamped)
        nextSingerPoint = singer.firstIndex { $0.timestampMs >= clamped } ?? singer.count
    }

    private func setPosition(_ ms: Int64) {
        positionMs = ms
        canvasState.currentTimeMs = ms
        onPositionChanged?(ms)
        if ms / 1000 != positionSeconds { positionSeconds = ms / 1000 }
    }

    private func feedSinger(upTo ms: Int64) {
        guard let buffer = canvasState.performancePitch else { return }
        while nextSingerPoint < singer.count, singer[nextSingerPoint].timestampMs <= ms {
            buffer.addBlocking(point: singer[nextSingerPoint])
            nextSingerPoint += 1
        }
    }
}
