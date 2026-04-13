import Foundation
import Combine
import voxavis

final class MetricsListDemoViewModel: ObservableObject {
    @Published var showBestValues: Bool = true

    lazy var state: MetricsListState = MetricsListState(
        metrics: MockData.voiceMetrics(),
        showBestValues: showBestValues,
        textColor: 0xFFE0E0E0,
        secondaryTextColor: 0xFF9E9E9E,
        dividerColor: 0x33FFFFFF
    )

    func randomize() {
        state.setMetrics(metrics: MockData.randomMetrics())
    }
}
