import SwiftUI
import voxavis

struct PracticeDemoView: View {
    @StateObject private var vm = PracticeViewModel()

    var body: some View {
        VStack(spacing: 16) {
            VoxaVisView(state: vm.canvasState)
                .frame(height: 250)

            // Controls
            HStack {
                Button(action: { vm.isPlaying.toggle() }) {
                    Image(systemName: vm.isPlaying ? "pause.fill" : "play.fill")
                        .font(.largeTitle)
                }
            }

            Slider(
                value: Binding(
                    get: { Double(vm.currentTimeMs) },
                    set: { vm.currentTimeMs = Int64($0); vm.canvasState.currentTimeMs = vm.currentTimeMs; vm.isPlaying = false }
                ),
                in: 0...Double(vm.totalDurationMs)
            )

            Text("\(vm.currentTimeMs / 1000)s / \(vm.totalDurationMs / 1000)s")
                .font(.caption)

            Divider()
            Text("Configuration").font(.headline)

            HStack(spacing: 8) {
                OptionChipView(label: "Notes", selected: vm.showNotes) { vm.showNotes.toggle() }
                OptionChipView(label: "Segments", selected: vm.showSegments) { vm.showSegments.toggle() }
                OptionChipView(label: "Ref Pitch", selected: vm.showRefPitch) { vm.showRefPitch.toggle() }
                OptionChipView(label: "Score Colors", selected: vm.showScoreColors) { vm.showScoreColors.toggle() }
            }
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 16_000_000)
                await MainActor.run { vm.tick() }
            }
        }
    }
}
