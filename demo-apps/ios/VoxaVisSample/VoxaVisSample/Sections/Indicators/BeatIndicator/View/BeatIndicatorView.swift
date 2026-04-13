import SwiftUI
import voxavis

struct BeatIndicatorDemoView: View {
    @StateObject private var vm = BeatIndicatorDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            BeatIndicatorView(state: vm.state)
                .frame(height: 60)

            Divider()
            Text("Configuration").font(.headline)

            Text("Beats Per Cycle").font(.subheadline)
            HStack(spacing: 8) {
                ForEach([3, 4, 6, 7, 8], id: \.self) { count in
                    OptionChipView(label: "\(count)", selected: vm.beatsPerCycle == count) {
                        vm.updateBeatsPerCycle(count)
                    }
                }
            }

            Toggle("Running", isOn: $vm.running)
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 500_000_000)
                await MainActor.run { vm.tick() }
            }
        }
    }
}
