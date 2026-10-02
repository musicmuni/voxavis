import Foundation
import Combine
import voxavis

/// Every behaviour setting of the practice canvas, live.
final class ConfigBuilderViewModel: ObservableObject {
    let model = CanvasDemoModel(
        resources: DemoLesson.singAfter(),
        singer: DemoLesson.learnerAnswers()
    )

    @Published var barPositionRatio: Float = 0.75 { didSet { applyConfig() } }
    @Published var timePerInchMs: Float = 3000 { didSet { applyConfig() } }
    @Published var followPhrase = false { didSet { applyConfig() } }
    @Published var showReference = true { didSet { applyConfig() } }
    @Published var showGridLabels = true { didSet { applyConfig() } }
    @Published var showNoteNames = true { didSet { applyConfig() } }

    init() { applyConfig() }

    private func applyConfig() {
        let fitAll: PracticeViewport = PracticeViewport.FitAll.shared
        let followPhraseViewport: PracticeViewport = PracticeViewport.FollowPhrase(
            minWindowCents: 1200,
            maxWindowCents: 2400,
            animationMs: 400,
            onResolved: nil,
            dwellMs: 500,
            trimBudget: 0.02
        )
        model.canvasState.config = SingingPracticeConfig.create(
            minPitchCents: -300,
            maxPitchCents: 1500,
            barPositionRatio: barPositionRatio,
            timePerInchMs: Int(timePerInchMs),
            showSolfegeLabels: showNoteNames,
            showGridLabels: showGridLabels,
            viewport: followPhrase ? followPhraseViewport : fitAll,
            showReference: showReference
        )
    }
}
