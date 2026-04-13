import SwiftUI
import voxavis

struct NoteAccuracyChartDemoView: View {
    @StateObject private var vm = NoteAccuracyChartDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            NoteAccuracyChartView(state: vm.state)
                .frame(height: 250)

            Divider()
            Text("Configuration").font(.headline)

            HStack {
                Text("Dot Size: \(Int(vm.noteDiameter))dp")
                    .frame(width: 130, alignment: .leading)
                Slider(value: $vm.noteDiameter, in: 12...48)
            }

            HStack {
                Text("Grid Lines: \(vm.gridLineCount)")
                    .frame(width: 130, alignment: .leading)
                Slider(value: Binding(
                    get: { Double(vm.gridLineCount) },
                    set: { vm.gridLineCount = Int($0) }
                ), in: 3...21, step: 2)
            }

            Button("Randomize") { vm.randomize() }
                .buttonStyle(.borderedProminent)
        }
    }
}
