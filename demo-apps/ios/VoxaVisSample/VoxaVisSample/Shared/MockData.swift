import Foundation
import voxavis

enum MockData {

    static let totalDurationMs: Int64 = 30000

    static func segments() -> [Segment] {
        return [
            Segment.create(
                startTimeMs: 0, endTimeMs: 5000,
                type: .teacher, lyrics: "Sa Re Ga Ma",
                score: 0.9, bestScore: 0.92
            ),
            Segment.create(
                startTimeMs: 5000, endTimeMs: 10000,
                type: .student, lyrics: "Pa Dha Ni Sa",
                score: 0.65, bestScore: 0.7
            ),
            Segment.create(
                startTimeMs: 10000, endTimeMs: 15000,
                type: .teacher, lyrics: "Sa Ni Dha Pa",
                score: 0.3, bestScore: 0.4
            ),
            Segment.create(
                startTimeMs: 15000, endTimeMs: 20000,
                type: .student, lyrics: "Ma Ga Re Sa",
                score: ScoreThresholds.notPracticed, bestScore: ScoreThresholds.notPracticed
            ),
            Segment.create(
                startTimeMs: 20000, endTimeMs: 25000,
                type: .teacher, lyrics: "Aaroha",
                score: ScoreThresholds.notPracticed, bestScore: 0.85
            ),
            Segment.create(
                startTimeMs: 25000, endTimeMs: 26000,
                type: .commentary, isGap: true
            ),
            Segment.create(
                startTimeMs: 26000, endTimeMs: 30000,
                type: .teacher, lyrics: "Avaroha",
                score: 0.95, bestScore: 0.95
            ),
        ]
    }

    static func noteAccuracyData() -> [AccuracyData] {
        return [
            AccuracyData(label: "Sa", targetPitchHz: 261.63, deviationPercent: -2, deviationRemark: .excellent),
            AccuracyData(label: "Re", targetPitchHz: 293.66, deviationPercent: 8, deviationRemark: .good),
            AccuracyData(label: "Ga", targetPitchHz: 329.63, deviationPercent: -15, deviationRemark: .fair),
            AccuracyData(label: "Ma", targetPitchHz: 349.23, deviationPercent: 25, deviationRemark: .needsWork),
            AccuracyData(label: "Pa", targetPitchHz: 392.00, deviationPercent: 3, deviationRemark: .excellent),
            AccuracyData(label: "Dha", targetPitchHz: 440.00, deviationPercent: -7, deviationRemark: .good),
            AccuracyData(label: "Ni", targetPitchHz: 493.88, deviationPercent: 12, deviationRemark: .fair),
        ]
    }

    static func scoreTrendData() -> [ChartPoint] {
        return [
            ChartPoint.create(value: 65, label: "65"),
            ChartPoint.create(value: 72, label: "72"),
            ChartPoint.create(value: 68, label: "68"),
            ChartPoint.create(value: 80, label: "80"),
            ChartPoint.create(value: 85, label: "85", isHighlighted: true),
            ChartPoint.create(value: 78, label: "78"),
            ChartPoint.create(value: 87, label: "87", isCurrent: true),
        ]
    }

    static func pitchContour(count: Int = 200) -> PitchContour {
        let points = (0..<count).map { i in
            let timeMs = Int64(i * 50)
            let baseCents = Float(600 + (i % 40) * 30)
            let jitter = Float(sin(Double(i) * 0.3) * 50)
            return PitchPoint.create(
                timestampMs: timeMs,
                freqHz: 261.63,
                cents: baseCents + jitter
            )
        }
        return PitchContour(points: points)
    }

    static func spiderMetrics() -> [RadarMetric] {
        return [
            RadarMetric.create(label: "Pitch", value: 0.85, bestValue: 0.80),
            RadarMetric.create(label: "Breath", value: 0.65, bestValue: 0.70),
            RadarMetric.create(label: "Range", value: 0.45, bestValue: 0.40),
            RadarMetric.create(label: "Agility", value: 0.72, bestValue: 0.68),
            RadarMetric.create(label: "Tone", value: 0.58),
        ]
    }

    static func voiceMetrics() -> [Metric] {
        return [
            Metric.create(label: "Vocal Range", value: "0.8", unit: "oct", bestValue: "1.2"),
            Metric.create(label: "Breath Control", value: "18", unit: "sec", bestValue: "22"),
            Metric.create(label: "Pitch Accuracy", value: "85", unit: "%", bestValue: "90"),
            Metric.create(label: "Agility", value: "72", unit: "%"),
        ]
    }

    static func vocalRangeData() -> RangeData {
        return RangeData(
            lowNoteCents: 200,
            highNoteCents: 2600,
            lowNoteLabel: "D3",
            highNoteLabel: "D5",
            octaveSpan: 2.0
        )
    }

    static func randomSpiderMetrics() -> [RadarMetric] {
        return [
            RadarMetric.create(label: "Pitch", value: Float.random(in: 0...1), bestValue: Float.random(in: 0.5...1)),
            RadarMetric.create(label: "Breath", value: Float.random(in: 0...1), bestValue: Float.random(in: 0.5...1)),
            RadarMetric.create(label: "Range", value: Float.random(in: 0...1), bestValue: Float.random(in: 0.5...1)),
            RadarMetric.create(label: "Agility", value: Float.random(in: 0...1), bestValue: Float.random(in: 0.5...1)),
            RadarMetric.create(label: "Tone", value: Float.random(in: 0...1)),
        ]
    }

    static func randomAccuracyData() -> [AccuracyData] {
        let notes = ["Sa", "Re", "Ga", "Ma", "Pa", "Dha", "Ni"]
        let freqs: [Float] = [261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88]
        let levels: [AccuracyLevel] = [.excellent, .good, .fair, .needsWork]
        return zip(notes, freqs).map { (name, freq) in
            AccuracyData(
                label: name,
                targetPitchHz: freq,
                deviationPercent: Float.random(in: -25...25),
                deviationRemark: levels.randomElement()!
            )
        }
    }

    static func randomMetrics() -> [Metric] {
        return [
            Metric.create(label: "Vocal Range", value: String(format: "%.1f", Float.random(in: 0...2)), unit: "oct", bestValue: "1.2"),
            Metric.create(label: "Breath Control", value: "\(Int.random(in: 10...25))", unit: "sec", bestValue: "22"),
            Metric.create(label: "Pitch Accuracy", value: "\(Int.random(in: 60...100))", unit: "%", bestValue: "90"),
            Metric.create(label: "Agility", value: "\(Int.random(in: 50...100))", unit: "%"),
        ]
    }

    static func animatedValue(timeMs: Int64, periodMs: Int64, amplitude: Float, offset: Float) -> Float {
        return Float(sin(Double(timeMs) / Double(periodMs) * 2.0 * .pi)) * amplitude + offset
    }
}
