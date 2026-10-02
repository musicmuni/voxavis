import SwiftUI
import voxavis

struct SegmentScrubberDemoView: View {
    @StateObject private var vm = SegmentScrubberDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            SegmentScrubberView(state: vm.state)
                .frame(height: 48)

            Text(vm.info)
                .font(.caption)
                .foregroundColor(.secondary)

            Button(action: { vm.isPlaying.toggle() }) {
                Image(systemName: vm.isPlaying ? "pause.fill" : "play.fill")
                    .font(.largeTitle)
            }

            Toggle("Spotlight segment 3 (focusedIndex)", isOn: $vm.spotlightThird)
            Toggle("Mark segments 2 and 4 as coming back (queuedIndices)", isOn: $vm.markQueued)
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 16_000_000)
                await MainActor.run { vm.tick() }
            }
        }
    }
}
