import SwiftUI
import voxavis

struct TuningGaugeDemoView: View {
    @StateObject private var vm = TuningGaugeDemoViewModel()
    @State private var startTime = Date()

    var body: some View {
        VStack(spacing: 16) {
            TuningGaugeView(state: vm.state)
                .frame(height: 160)

            Divider()
            Text("Configuration").font(.headline)

            Toggle("Animate", isOn: $vm.animate)

            if !vm.animate {
                HStack {
                    Text("Cents Off: \(Int(vm.centsOff))")
                        .frame(width: 130, alignment: .leading)
                    Slider(value: Binding(
                        get: { Double(vm.centsOff) },
                        set: { vm.centsOff = Float($0); vm.state.centsOff = Float($0) }
                    ), in: -50...50)
                }
                HStack {
                    Text("Confidence: \(Int(vm.confidence * 100))%")
                        .frame(width: 130, alignment: .leading)
                    Slider(value: Binding(
                        get: { Double(vm.confidence) },
                        set: { vm.confidence = Float($0); vm.state.confidence = Float($0) }
                    ), in: 0...1)
                }
            }

            Text("Note").font(.subheadline)
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(["Sa", "Re", "Ga", "Ma", "Pa", "Dha", "Ni"], id: \.self) { note in
                        OptionChipView(label: note, selected: vm.noteLabel == note) {
                            vm.updateNote(note)
                        }
                    }
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
