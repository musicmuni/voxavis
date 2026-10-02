import Foundation
import Combine
import voxavis

final class NoteAccuracyChartDemoViewModel: ObservableObject {
    @Published var noteDiameter: Float = 24
    @Published var gridLineCount: Int = 11

    // The dot fill is a gradient over each note's score (0 to 100); the
    // default bands run red, amber, green. Pass bandColors to change them.
    lazy var state: NoteAccuracyChartState = NoteAccuracyChartState.create(
        notes: MockData.noteAccuracyData(),
        gridLineCount: gridLineCount,
        noteDiameter: noteDiameter,
        noteSpacing: 17,
        flatLabel: "Flat",
        sharpLabel: "Sharp"
    )

    func randomize() {
        state.setNotes(notes: MockData.randomAccuracyData())
    }
}
