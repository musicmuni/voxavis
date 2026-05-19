import Foundation
import Combine
import voxavis

final class NoteAccuracyChartDemoViewModel: ObservableObject {
    @Published var noteDiameter: Float = 24
    @Published var gridLineCount: Int = 11

    // The dot fill is a gradient over each note's score; the default
    // accuracyRamp (red → amber → green) is used here.
    lazy var state: NoteAccuracyChartState = NoteAccuracyChartState(
        notes: MockData.noteAccuracyData(),
        gridLineCount: Int32(gridLineCount),
        noteDiameter: noteDiameter,
        noteSpacing: 17,
        flatLabel: "Flat",
        sharpLabel: "Sharp"
    )

    func randomize() {
        state.setNotes(notes: MockData.randomAccuracyData())
    }
}
