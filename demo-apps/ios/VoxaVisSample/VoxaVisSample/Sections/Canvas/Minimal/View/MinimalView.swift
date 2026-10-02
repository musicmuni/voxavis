import SwiftUI
import voxavis

struct MinimalDemoView: View {
    @StateObject private var vm = MinimalViewModel()

    var body: some View {
        LicenceGate {
            VStack(spacing: 16) {
                SingingPracticeView(state: vm.model.canvasState)
                    .frame(height: 260)

                TransportControls(model: vm.model)
            }
            .task { await vm.model.run() }
        }
    }
}
