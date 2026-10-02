import SwiftUI
import voxavis

struct FreestyleDemoView: View {
    @StateObject private var vm = FreestyleViewModel()

    var body: some View {
        LicenceGate {
            VStack(spacing: 16) {
                SingingPracticeView(state: vm.model.canvasState)
                    .frame(height: 260)

                TransportControls(model: vm.model)

                Text("Between runs the ball waits on Sa (restingPitchCents).")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .task { await vm.model.run() }
        }
    }
}
