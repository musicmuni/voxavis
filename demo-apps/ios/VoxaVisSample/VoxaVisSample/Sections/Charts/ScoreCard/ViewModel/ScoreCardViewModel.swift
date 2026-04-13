import Foundation
import Combine
import voxavis

final class ScoreCardDemoViewModel: ObservableObject {
    @Published var score: Int = 87
    @Published var rating: String = "EXCELLENT"

    lazy var state: ScoreCardState = ScoreCardState(
        score: Int32(score),
        maxScore: 100,
        rating: rating,
        ratingColor: 0xFF4CAF50,
        subtitle: "Demo session",
        scoreColor: 0xFFFFFFFF,
        textColor: 0xFFE0E0E0
    )

    func updateScore(_ newScore: Int) {
        score = newScore
        state.swiftScore = newScore
        rating = newScore >= 85 ? "EXCELLENT" : newScore >= 70 ? "GOOD" : newScore >= 50 ? "FAIR" : "NEEDS WORK"
        state.rating = rating
    }
}
