import SwiftUI
import voxavis

struct ConfigBuilderDemoView: View {
    @StateObject private var vm = ConfigBuilderViewModel()

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

            Divider()
            Text("Config Builder").font(.headline)

            HStack {
                Text("Bar Position: \(String(format: "%.1f", vm.barPositionRatio))")
                    .frame(width: 150, alignment: .leading)
                Slider(value: $vm.barPositionRatio, in: 0.1...0.9)
            }

            HStack {
                Text("Time/Inch: \(Int(vm.timePerInchMs))ms")
                    .frame(width: 150, alignment: .leading)
                Slider(value: $vm.timePerInchMs, in: 1000...10000)
            }

            Divider()
            Text("Visibility").font(.subheadline)

            Toggle("Notes", isOn: $vm.showNotes)
            Toggle("Segments", isOn: $vm.showSegments)
            Toggle("Reference Pitch", isOn: $vm.showRefPitch)
            Toggle("Score Colors", isOn: $vm.showScoreColors)
            Toggle("Grid Lines", isOn: $vm.showGridLines)
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 16_000_000)
                await MainActor.run { vm.tick() }
            }
        }
    }
}
