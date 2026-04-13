import Foundation
import Combine
import voxavis

final class BeatIndicatorDemoViewModel: ObservableObject {
    @Published var currentBeat: Int = 0
    @Published var beatsPerCycle: Int = 4
    @Published var running: Bool = true

    lazy var state: BeatIndicatorState = BeatIndicatorState(
        currentBeat: Int32(currentBeat),
        beatsPerCycle: Int32(beatsPerCycle),
        bpm: KotlinFloat(value: 120),
        beatColor: 0xFF4ECDC4,
        downbeatColor: 0xFFFFE66D,
        inactiveBeatColor: 0x40FFFFFF,
        beatSize: 12,
        downbeatSize: 16
    )

    func tick() {
        guard running else { return }
        currentBeat = (currentBeat + 1) % beatsPerCycle
        state.swiftCurrentBeat = currentBeat
    }

    func updateBeatsPerCycle(_ count: Int) {
        beatsPerCycle = count
        currentBeat = 0
        state = BeatIndicatorState(
            currentBeat: 0,
            beatsPerCycle: Int32(count),
            bpm: KotlinFloat(value: 120),
            beatColor: 0xFF4ECDC4,
            downbeatColor: 0xFFFFE66D,
            inactiveBeatColor: 0x40FFFFFF,
            beatSize: 12,
            downbeatSize: 16
        )
    }
}
