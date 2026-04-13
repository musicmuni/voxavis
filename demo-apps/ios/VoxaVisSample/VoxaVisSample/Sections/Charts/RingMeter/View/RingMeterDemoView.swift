import SwiftUI
import voxavis

struct RingMeterDemoView: View {
    @StateObject private var vm = RingMeterDemoViewModel()
    @State private var startTime = Date()

    var body: some View {
        VStack(spacing: 16) {
            RingMeterView(state: vm.state)
                .frame(width: 240, height: 240)

            Divider()
            Text("Configuration").font(.headline)

            HStack {
                Text("Value: \(String(format: "%.2f", vm.value))")
                    .frame(width: 120, alignment: .leading)
                Slider(value: Binding(
                    get: { Double(vm.value) },
                    set: { vm.setManualValue(Float($0)); vm.animate = false }
                ), in: 0...1)
            }

            Toggle("Animate", isOn: $vm.animate)
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
