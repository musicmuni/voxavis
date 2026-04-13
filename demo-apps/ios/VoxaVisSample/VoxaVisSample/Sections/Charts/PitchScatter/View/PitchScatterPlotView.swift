import SwiftUI
import voxavis

struct PitchScatterPlotDemoView: View {
    @StateObject private var vm = PitchScatterPlotDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            PitchScatterPlotView(state: vm.state)
                .frame(height: 180)

            Divider()
            Text("Configuration").font(.headline)

            HStack {
                Text("Point Radius: \(String(format: "%.1f", vm.pointRadius))dp")
                    .frame(width: 150, alignment: .leading)
                Slider(value: $vm.pointRadius, in: 0.5...8)
            }

            Toggle("Connecting Line", isOn: $vm.showConnectingLine)
        }
    }
}
