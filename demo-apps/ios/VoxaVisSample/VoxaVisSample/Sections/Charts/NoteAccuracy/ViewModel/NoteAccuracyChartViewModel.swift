import Foundation
import Combine
import voxavis

final class NoteAccuracyChartDemoViewModel: ObservableObject {
    @Published var noteDiameter: Float = 24
    @Published var gridLineCount: Int = 11

    lazy var state: NoteAccuracyChartState = NoteAccuracyChartState(
        notes: MockData.noteAccuracyData(),
        gridLineCount: Int32(gridLineCount),
        noteDiameter: noteDiameter,
        noteSpacing: 17,
        flatLabel: "Flat",
        sharpLabel: "Sharp",
        goodColor: 0xFF4CAF50,
        poorColor: 0xFFF44336
    )

    func randomize() {
        state.setNotes(notes: MockData.randomAccuracyData())
    }
}
