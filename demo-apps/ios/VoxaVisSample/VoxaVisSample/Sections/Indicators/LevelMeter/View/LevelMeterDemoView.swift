import SwiftUI
import voxavis

struct LevelMeterDemoView: View {
    @StateObject private var vm = LevelMeterDemoViewModel()
    @State private var startTime = Date()

    var body: some View {
        VStack(spacing: 16) {
            if vm.isVertical {
                LevelMeterView(state: vm.state)
                    .frame(width: 40, height: 200)
            } else {
                LevelMeterView(state: vm.state)
                    .frame(height: 32)
            }

            Divider()
            Text("Configuration").font(.headline)

            Text("Orientation").font(.subheadline)
            HStack(spacing: 8) {
                OptionChipView(label: "Vertical", selected: vm.isVertical) { vm.isVertical = true; vm.rebuildState() }
                OptionChipView(label: "Horizontal", selected: !vm.isVertical) { vm.isVertical = false; vm.rebuildState() }
            }

            Text("Segments").font(.subheadline)
            HStack(spacing: 8) {
                ForEach([0, 10, 20, 30], id: \.self) { count in
                    OptionChipView(label: count == 0 ? "Smooth" : "\(count)", selected: vm.segmentCount == count) {
                        vm.segmentCount = count; vm.rebuildState()
                    }
                }
            }

            Toggle("Animate", isOn: $vm.animate)

            if !vm.animate {
                HStack {
                    Text("Level: \(Int(vm.level * 100))%")
                        .frame(width: 120, alignment: .leading)
                    Slider(value: Binding(
                        get: { Double(vm.level) },
                        set: { vm.level = Float($0); vm.state.level = Float($0) }
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
