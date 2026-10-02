import SwiftUI
import voxavis

struct ConfigBuilderDemoView: View {
    @StateObject private var vm = ConfigBuilderViewModel()

    var body: some View {
        LicenceGate {
            VStack(spacing: 16) {
                SingingPracticeView(state: vm.model.canvasState)
                    .frame(height: 260)

                TransportControls(model: vm.model)

                Divider()
                Text("SingingPracticeConfig").font(.headline)

                HStack {
                    Text("Ahead of playhead: \(String(format: "%.2f", vm.barPositionRatio))")
                        .frame(width: 150, alignment: .leading)
                    Slider(value: $vm.barPositionRatio, in: 0.1...0.9)
                }

                HStack {
                    Text("Time per inch: \(Int(vm.timePerInchMs)) ms")
                        .frame(width: 150, alignment: .leading)
                    Slider(value: $vm.timePerInchMs, in: 1000...10000)
                }

                Toggle("Follow the phrase (viewport)", isOn: $vm.followPhrase)
                Toggle("Reference", isOn: $vm.showReference)
                Toggle("Grid labels", isOn: $vm.showGridLabels)
                Toggle("Note names", isOn: $vm.showNoteNames)
            }
            .task { await vm.model.run() }
        }
    }
}
