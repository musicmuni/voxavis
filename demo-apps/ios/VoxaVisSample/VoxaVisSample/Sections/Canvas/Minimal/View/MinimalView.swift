import SwiftUI
import voxavis

struct MinimalDemoView: View {
    @StateObject private var vm = MinimalViewModel()

    var body: some View {
        VStack(spacing: 16) {
            VoxaVisView(state: vm.canvasState)
                .frame(height: 250)

            HStack {
                Button(action: { vm.isPlaying.toggle() }) {
                    Image(systemName: vm.isPlaying ? "pause.fill" : "play.fill")
                        .font(.largeTitle)
                }
            }

            Toggle("Show Grid Lines", isOn: $vm.showGridLines)
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 16_000_000)
                await MainActor.run { vm.tick() }
            }
        }
    }
}
