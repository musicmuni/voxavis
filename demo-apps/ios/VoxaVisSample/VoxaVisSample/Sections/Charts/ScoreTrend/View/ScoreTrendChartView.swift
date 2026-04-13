import SwiftUI
import voxavis

struct ScoreTrendChartDemoView: View {
    @StateObject private var vm = ScoreTrendChartDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            ScoreTrendChartView(state: vm.state)
                .frame(height: 180)

            Divider()
            Text("Configuration").font(.headline)

            Toggle("Bezier Curve", isOn: $vm.bezierCurve)
            Toggle("Show Grid", isOn: $vm.showGrid)
            Toggle("Animate", isOn: $vm.animate)

            HStack(spacing: 12) {
                Button("Add Point") { vm.addPoint() }
                    .buttonStyle(.borderedProminent)
                Button("Reset") { vm.reset() }
                    .buttonStyle(.bordered)
            }
        }
    }
}
