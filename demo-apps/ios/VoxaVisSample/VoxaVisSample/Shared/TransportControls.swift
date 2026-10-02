import SwiftUI

/// Play, pause and seek for a `CanvasDemoModel`.
struct TransportControls: View {
    @ObservedObject var model: CanvasDemoModel
    var skipMs: Int64? = nil

    var body: some View {
        VStack(spacing: 8) {
            HStack(spacing: 30) {
                if let skipMs {
                    Button { model.seek(toMs: model.positionMs - skipMs) } label: {
                        Image(systemName: "gobackward.5").font(.title2)
                    }
                }
                Button { model.isPlaying.toggle() } label: {
                    Image(systemName: model.isPlaying ? "pause.fill" : "play.fill").font(.largeTitle)
                }
                if let skipMs {
                    Button { model.seek(toMs: model.positionMs + skipMs) } label: {
                        Image(systemName: "goforward.5").font(.title2)
                    }
                }
            }
            Slider(
                value: Binding(
                    get: { Double(model.positionSeconds * 1000) },
                    set: { model.isPlaying = false; model.seek(toMs: Int64($0)) }
                ),
                in: 0...Double(max(model.durationMs, 1))
            )
            Text("\(model.positionSeconds)s / \(model.durationMs / 1000)s")
                .font(.caption)
                .foregroundColor(.secondary)
        }
    }
}
