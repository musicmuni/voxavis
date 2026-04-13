import SwiftUI
import voxavis

struct RadarChartDemoView: View {
    @StateObject private var vm = RadarChartDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            RadarChartView(state: vm.state)
                .frame(width: 280, height: 280)

            Divider()
            Text("Configuration").font(.headline)

            HStack {
                Text("Grid Rings: \(vm.gridRingCount)")
                    .frame(width: 120, alignment: .leading)
                Slider(value: Binding(
                    get: { Double(vm.gridRingCount) },
                    set: { vm.gridRingCount = Int($0) }
                ), in: 2...8, step: 1)
            }

            Toggle("Show Best", isOn: $vm.showBest)

            Button("Randomize") { vm.randomize() }
                .buttonStyle(.borderedProminent)
        }
    }
}
