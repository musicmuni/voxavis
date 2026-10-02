//
//  VoxaVisSampleApp.swift
//  VoxaVisSample
//

import SwiftUI
import voxavis

@main
struct VoxaVisSampleApp: App {
    init() {
        Licence.start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

/// Starts the VoxaVis licence once, at launch.
///
/// The feature components (the practice canvas, the pitch monitors and the
/// review) need it; charts, meters and navigation do not.
enum Licence {
    /// Why the licence did not start, or nil if it did.
    private(set) static var problem: String?

    static func start() {
        // Three ways to initialize VoxaVis:
        //   - VV.initialize(proxyEndpoint:): recommended for apps. Your backend
        //     holds the API key and registers the device; nothing secret ships.
        //   - VV.initializeWithAttestation(apiKey:): for apps without a backend.
        //   - VV.initializeForServer(apiKey:): what this demo uses, so it runs
        //     without a backend. Do not ship an API key inside an app.
        guard !Config.apiKey.isEmpty, Config.apiKey != "YOUR_API_KEY_HERE" else {
            problem = "Add your VoxaVis API key to Config.swift (copy Config.swift.template) to see this screen."
            return
        }
        do {
            try VV.initializeForServer(apiKey: Config.apiKey)
        } catch {
            problem = "VoxaVis did not start: \(error.localizedDescription)"
        }
    }
}

/// Shows its content once the licence has started, and why not otherwise.
struct LicenceGate<Content: View>: View {
    @ViewBuilder let content: () -> Content

    var body: some View {
        if VV.isInitialized {
            content()
        } else {
            VStack(spacing: 12) {
                Image(systemName: "key.fill")
                    .font(.largeTitle)
                    .foregroundColor(.secondary)
                Text(Licence.problem ?? "VoxaVis is not initialized.")
                    .multilineTextAlignment(.center)
                    .foregroundColor(.secondary)
            }
            .padding()
        }
    }
}
