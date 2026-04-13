import SwiftUI
import voxavis

struct ConfidenceMeterDemoView: View {
    @StateObject private var vm = ConfidenceMeterDemoViewModel()
    @State private var startTime = Date()

    var body: some View {
        VStack(spacing: 16) {
            if vm.isHorizontal {
                ConfidenceMeterView(state: vm.state)
                    .frame(height: 32)
            } else {
                ConfidenceMeterView(state: vm.state)
                    .frame(width: 32, height: 200)
            }

            Divider()
            Text("Configuration").font(.headline)

            Text("Orientation").font(.subheadline)
            HStack(spacing: 8) {
                OptionChipView(label: "Horizontal", selected: vm.isHorizontal) { vm.isHorizontal = true; vm.rebuildState() }
                OptionChipView(label: "Vertical", selected: !vm.isHorizontal) { vm.isHorizontal = false; vm.rebuildState() }
            }

            Toggle("Show Label", isOn: Binding(
                get: { vm.showLabel },
                set: { vm.showLabel = $0; vm.rebuildState() }
            ))

            Toggle("Animate", isOn: $vm.animate)

            if !vm.animate {
                HStack {
                    Text("Confidence: \(Int(vm.confidence * 100))%")
                        .frame(width: 140, alignment: .leading)
                    Slider(value: Binding(
                        get: { Double(vm.confidence) },
                        set: { vm.confidence = Float($0); vm.state.confidence = Float($0) }
                    ), in: 0...1)
                }
            }
        }
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(nanoseconds: 16_000_000)
                await MainActor.run {
                    let elapsed = Int64(Date().timeIntervalSince(startTime) * 1000)
                    vm.tick(timeMs: elapsed)
                }
            }
        }
    }
}
