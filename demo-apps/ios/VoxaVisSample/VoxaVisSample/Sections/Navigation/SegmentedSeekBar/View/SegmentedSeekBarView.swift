import SwiftUI
import voxavis

struct SegmentedSeekBarDemoView: View {
    @StateObject private var vm = SegmentedSeekBarDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            SegmentedSeekBarView(state: vm.state)
                .frame(height: 20)

            if !vm.lastTappedInfo.isEmpty {
                Text("Last tapped: \(vm.lastTappedInfo)")
                    .font(.caption)
            }

            Text("Time: \(vm.currentTimeMs / 1000)s / \(vm.totalDurationMs / 1000)s")
                .font(.caption)

            Divider()

            Button(action: { vm.isPlaying.toggle() }) {
                Text(vm.isPlaying ? "Pause" : "Play")
            }
            .buttonStyle(.borderedProminent)
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 16_000_000)
                await MainActor.run { vm.tick() }
            }
        }
    }
}
