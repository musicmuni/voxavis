import Foundation
import voxavis

/// A sing-after lesson built in code: the teacher sings a phrase, the learner
/// answers it. Your app would build the same types from its own lesson data
/// and its pitch detector; nothing here is specific to the demo except the
/// numbers.
enum DemoLesson {

    /// The scale, in cents above the tonic.
    static let scale: [(name: String, cents: Float)] = [
        ("Sa", 0), ("Re", 200), ("Ga", 400), ("Ma", 500),
        ("Pa", 700), ("Dha", 900), ("Ni", 1100), ("Sa'", 1200),
    ]

    /// Phrases as indices into `scale`.
    static let phrases: [[Int]] = [
        [0, 1, 2, 1, 0],
        [2, 3, 4, 3, 2],
        [4, 5, 7, 5, 4],
    ]

    static let noteMs: Int64 = 600

    /// One line per note of the scale. The tonics are anchors, so their names
    /// stay when the canvas is too short for every label.
    static func gridLines() -> [GridLine] {
        scale.map { note in
            let isTonic = note.cents == 0 || note.cents == 1200
            return GridLine.create(
                cents: note.cents,
                label: note.name,
                isHighlighted: isTonic || note.cents == 700,
                priority: isTonic ? GridLine.companion.PRIORITY_ANCHOR : GridLine.companion.PRIORITY_DEFAULT
            )
        }
    }

    /// The lesson: each phrase sung by the teacher, then the same length left
    /// for the learner.
    static func singAfter(phrases: [[Int]] = phrases) -> SingingPracticeResources {
        var segments: [Segment] = []
        var notes: [ScoreNote] = []
        var contour: [PitchPoint] = []
        var t: Int64 = 0
        for phrase in phrases {
            let length = Int64(phrase.count) * noteMs
            let words = phrase.map { scale[$0].name }.joined(separator: " ")
            segments.append(Segment.create(startTimeMs: t, endTimeMs: t + length, type: .reference, lyrics: words))
            notes += phraseNotes(phrase, startMs: t, type: .reference)
            contour += sungLine(phrase, startMs: t)
            segments.append(Segment.create(startTimeMs: t + length, endTimeMs: t + 2 * length, type: .performance, lyrics: words))
            notes += phraseNotes(phrase, startMs: t + length, type: .performance)
            t += 2 * length
        }
        return SingingPracticeResources.create(
            mode: .singafter,
            trackLengthMs: t,
            segments: segments,
            notes: notes,
            gridLines: gridLines(),
            referencePitch: PitchContourData.create(points: contour)
        )
    }

    /// What a learner might sing back: each answer a little late and a little
    /// off, as a pitch detector would report it.
    static func learnerAnswers(phrases: [[Int]] = phrases) -> [PitchPoint] {
        var points: [PitchPoint] = []
        var t: Int64 = 0
        for (i, phrase) in phrases.enumerated() {
            let length = Int64(phrase.count) * noteMs
            let drift = Float([12, -25, 40][i % 3])
            points += sungLine(phrase, startMs: t + length + 90, centsOffset: drift)
            t += 2 * length
        }
        return points
    }

    /// Someone singing freely up and down the scale for `durationMs`, with a
    /// breath between runs. The canvas has no reference to compare it with.
    static func freeSinging(durationMs: Int64) -> [PitchPoint] {
        let run = [0, 1, 2, 3, 4, 5, 6, 7, 6, 5, 4, 3, 2, 1, 0]
        let runMs = Int64(run.count) * noteMs
        let breathMs: Int64 = 1_200
        var points: [PitchPoint] = []
        var t: Int64 = 500
        while t + runMs < durationMs {
            points += sungLine(run, startMs: t)
            t += runMs + breathMs
        }
        return points
    }

    /// Phrase notes as the canvas draws them under the line.
    static func phraseNotes(_ phrase: [Int], startMs: Int64, type: SegmentType) -> [ScoreNote] {
        phrase.enumerated().map { i, degree in
            ScoreNote.create(
                startTimeMs: startMs + Int64(i) * noteMs,
                endTimeMs: startMs + Int64(i + 1) * noteMs,
                cents: scale[degree].cents,
                label: scale[degree].name,
                segmentType: type
            )
        }
    }

    /// A sung line through the phrase: a short glide into each note and a
    /// little vibrato on it, one point every 20 ms.
    static func sungLine(_ phrase: [Int], startMs: Int64, centsOffset: Float = 0) -> [PitchPoint] {
        var points: [PitchPoint] = []
        let step: Int64 = 20
        let glideMs: Int64 = 80
        for (i, degree) in phrase.enumerated() {
            let target = scale[degree].cents + centsOffset
            let from = i == 0 ? target : scale[phrase[i - 1]].cents + centsOffset
            var ms: Int64 = 0
            while ms < noteMs {
                let glide = min(1, Float(ms) / Float(glideMs))
                let vibrato = Float(sin(Double(ms) / 1000 * 2 * .pi * 5.5)) * 15 * glide
                let cents = from + (target - from) * glide + vibrato
                points.append(PitchPoint.create(
                    timestampMs: startMs + Int64(i) * noteMs + ms,
                    freqHz: 261.63 * Float(pow(2, Double(cents) / 1200)),
                    cents: cents
                ))
                ms += step
            }
        }
        return points
    }
}
