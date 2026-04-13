
import Foundation

struct LessonData: Codable {
    let durationMs: Int64
    let notes: [LessonNote]?
    let segments: [LessonSegment]?
    let referencePitch: [LessonPitchPoint]?
    let gridLines: [LessonGridLine]?
}

struct LessonNote: Codable {
    let startTimeMs: Int64
    let endTimeMs: Int64
    let cents: Double
    let label: String
    let segmentType: String
}

struct LessonSegment: Codable {
    let startTimeMs: Int64
    let endTimeMs: Int64
    let type: String
    let lyrics: String
}

struct LessonPitchPoint: Codable {
    let timestampMs: Int64
    let cents: Double
    let freqHz: Double
}

struct LessonGridLine: Codable {
    let cents: Double
    let label: String
    let isHighlighted: Bool
}

struct PerformancePitchData: Codable {
    let pitchPoints: [LessonPitchPoint]

    func getPitchAt(_ currentTimeMs: Int64) -> LessonPitchPoint? {
        return pitchPoints.first { $0.timestampMs >= currentTimeMs }
    }
}
