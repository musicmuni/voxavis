import SwiftUI
import voxavis

struct ScoreCardDemoView: View {
    @StateObject private var vm = ScoreCardDemoViewModel()

    var body: some View {
        VStack(spacing: 16) {
            ScoreCardView(state: vm.state)
                .frame(height: 120)

            Divider()
            Text("Configuration").font(.headline)

            HStack {
                Text("Score: \(vm.score)")
                    .frame(width: 100, alignment: .leading)
                Slider(value: Binding(
                    get: { Double(vm.score) },
                    set: { vm.updateScore(Int($0)) }
                ), in: 0...100)
            }

            Text("Rating").font(.subheadline)
            HStack(spacing: 8) {
                ForEach(["EXCELLENT", "GOOD", "FAIR", "NEEDS WORK"], id: \.self) { r in
                    OptionChipView(label: r, selected: vm.rating == r) {
                        vm.rating = r
                        vm.state.rating = r
                    }
                }
            }
        }
    }
}
