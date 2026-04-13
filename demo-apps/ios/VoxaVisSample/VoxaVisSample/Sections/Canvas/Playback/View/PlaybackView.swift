import SwiftUI
import voxavis

struct PlaybackDemoView: View {
    @StateObject private var vm = PlaybackViewModel()

    var body: some View {
        VStack(spacing: 16) {
            VoxaVisView(state: vm.canvasState)
                .frame(height: 250)

            HStack(spacing: 30) {
                Button(action: {
                    vm.currentTimeMs = max(0, vm.currentTimeMs - 5000)
                    vm.canvasState.currentTimeMs = vm.currentTimeMs
                }) {
                    Image(systemName: "gobackward.5")
                        .font(.title2)
                }

                Button(action: { vm.isPlaying.toggle() }) {
                    Image(systemName: vm.isPlaying ? "pause.fill" : "play.fill")
                        .font(.largeTitle)
                }

                Button(action: {
                    vm.currentTimeMs = min(vm.totalDurationMs, vm.currentTimeMs + 5000)
                    vm.canvasState.currentTimeMs = vm.currentTimeMs
                }) {
                    Image(systemName: "goforward.5")
                        .font(.title2)
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
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 16_000_000)
                await MainActor.run { vm.tick() }
            }
        }
    }
}
