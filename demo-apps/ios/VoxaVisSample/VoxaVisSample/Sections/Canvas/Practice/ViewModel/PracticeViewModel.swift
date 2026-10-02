import Foundation
import Combine
import voxavis

/// A sing-after lesson with a learner answering it.
final class PracticeViewModel: ObservableObject {
    let model = CanvasDemoModel(
        resources: DemoLesson.singAfter(),
        config: SingingPracticeConfig.create(minPitchCents: -300, maxPitchCents: 1500),
        singer: DemoLesson.learnerAnswers()
    )

    @Published var showReference = true { didSet { applyConfig() } }
    @Published var showGridLabels = true { didSet { applyConfig() } }
    @Published var showNoteNames = true { didSet { applyConfig() } }

    /// Visibility is configuration: the lesson (the resources) never changes
    /// when something is hidden, so the picture does not move.
    private func applyConfig() {
        model.canvasState.config = SingingPracticeConfig.create(
            minPitchCents: -300,
            maxPitchCents: 1500,
            showSolfegeLabels: showNoteNames,
            showGridLabels: showGridLabels,
            showReference: showReference
        )
    }
}
