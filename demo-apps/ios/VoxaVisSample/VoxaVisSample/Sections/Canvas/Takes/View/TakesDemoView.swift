import SwiftUI
import voxavis

struct TakesDemoView: View {
    @StateObject private var vm = TakesViewModel()

    var body: some View {
        LicenceGate {
            VStack(spacing: 16) {
                SingingPracticeView(state: vm.model.canvasState)
                    .frame(height: 260)

                TransportControls(model: vm.model)

                Text("Three takes of one phrase on one session clock, each opened by a count (LeadInMarks) over a beat cycle (MetricLaneCycle).")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .task { await vm.model.run() }
        }
    }
}
