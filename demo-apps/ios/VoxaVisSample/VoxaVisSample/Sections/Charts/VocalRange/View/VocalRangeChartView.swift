import SwiftUI
import voxavis

struct VocalRangeChartDemoView: View {
    @StateObject private var vm = VocalRangeChartDemoViewModel()
    @State private var startTime = Date()

    var body: some View {
        VStack(spacing: 16) {
            VocalRangeChartView(state: vm.state)
                .frame(height: 300)

            Divider()
            Text("Configuration").font(.headline)

            Toggle("Animate", isOn: $vm.animate)

            if !vm.animate {
                HStack {
                    Text("Pitch: \(Int(vm.currentPitchCents))¢")
                        .frame(width: 130, alignment: .leading)
                    Slider(value: Binding(
                        get: { Double(vm.currentPitchCents) },
                        set: { vm.setManualPitch(Float($0)) }
                    ), in: 200...2600)
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
