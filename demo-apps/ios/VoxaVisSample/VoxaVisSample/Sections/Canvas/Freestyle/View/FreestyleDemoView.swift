import SwiftUI
import voxavis

struct FreestyleDemoView: View {
    @StateObject private var vm = FreestyleViewModel()

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

            Toggle("Show Pitch Ball", isOn: $vm.showPitchBall)
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 16_000_000)
                await MainActor.run { vm.tick() }
            }
        }
    }
}
