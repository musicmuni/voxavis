import SwiftUI
import voxavis

struct MetricsListDemoView: View {
    @StateObject private var vm = MetricsListDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            MetricsListView(state: vm.state)
                .frame(height: 200)

            Divider()
            Text("Configuration").font(.headline)

            Toggle("Show Best Values", isOn: $vm.showBestValues)

            Button("Randomize") { vm.randomize() }
                .buttonStyle(.borderedProminent)
        }
    }
}
