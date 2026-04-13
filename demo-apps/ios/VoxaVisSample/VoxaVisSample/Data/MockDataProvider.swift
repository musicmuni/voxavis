
import Foundation

class MockDataProvider {
    static func loadPracticeLesson() -> LessonData {
        return load("practice_lesson.json")
    }

    static func loadPerformancePitch() -> PerformancePitchData {
        return load("user_pitch_sample.json")
    }

    static func loadPlaybackLesson() -> LessonData {
        return load("playback_lesson.json")
    }

    private static func load<T: Decodable>(_ filename: String) -> T {
        let data: Data

        // Split filename into name and extension for proper Bundle API usage
        let name = (filename as NSString).deletingPathExtension
        let ext = (filename as NSString).pathExtension

        guard let file = Bundle.main.url(forResource: name, withExtension: ext.isEmpty ? nil : ext)
        else {
            fatalError("Couldn't find \(filename) in main bundle.")
        }

        do {
            data = try Data(contentsOf: file)
        } catch {
            fatalError("Couldn't load \(filename) from main bundle:\n\(error)")
        }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(T.self, from: data)
        } catch {
            fatalError("Couldn't parse \(filename) as \(T.self):\n\(error)")
        }
    }
}
