import SwiftUI
import voxavis

struct PracticeDemoView: View {
    @StateObject private var vm = PracticeViewModel()

    var body: some View {
        LicenceGate {
            VStack(spacing: 16) {
                SingingPracticeView(state: vm.model.canvasState)
                    .frame(height: 260)

                TransportControls(model: vm.model)

                Divider()
                Text("Configuration").font(.headline)

                HStack(spacing: 8) {
                    OptionChipView(label: "Reference", selected: vm.showReference) { vm.showReference.toggle() }
                    OptionChipView(label: "Grid labels", selected: vm.showGridLabels) { vm.showGridLabels.toggle() }
                    OptionChipView(label: "Note names", selected: vm.showNoteNames) { vm.showNoteNames.toggle() }
                }
            }
            .task { await vm.model.run() }
        }
    }
}
