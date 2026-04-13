import SwiftUI
import voxavis

struct LyricsOverlayDemoView: View {
    @StateObject private var vm = LyricsOverlayDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            LyricsOverlayView(state: vm.state)
                .frame(height: vm.isExpanded ? 200 : 100)

            Divider()
            Text("Configuration").font(.headline)

            Button(action: { vm.isPlaying.toggle() }) {
                Text(vm.isPlaying ? "Pause" : "Play")
            }
            .buttonStyle(.borderedProminent)

            Toggle("Expanded", isOn: Binding(
                get: { vm.isExpanded },
                set: { vm.isExpanded = $0; vm.state.isExpanded = $0 }
            ))

            HStack {
                Text("Visible Items: \(vm.visibleItemCount)")
                    .frame(width: 130, alignment: .leading)
                Slider(value: Binding(
                    get: { Double(vm.visibleItemCount) },
                    set: { vm.visibleItemCount = Int($0) }
                ), in: 1...5, step: 1)
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
