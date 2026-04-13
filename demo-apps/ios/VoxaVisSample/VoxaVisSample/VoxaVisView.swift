import SwiftUI
import voxavis

/// A SwiftUI wrapper for the VoxaVis Compose component.
/// Uses VoxaVisState to bridge Swift state to Compose.
struct VoxaVisView: UIViewControllerRepresentable {
    /// The state object controlling the canvas.
    /// Create this in your parent view and update its properties.
    let state: VoxaVisState

    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController(state: state)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // State updates happen through VoxaVisState's reactive properties
        // No need to manually update here - Compose observes the StateFlows
    }
}

/// A simple demo view showing the canvas with auto-advancing time.
/// Creates a default state with singafter mode for demonstration.
struct VoxaVisDemoView: UIViewControllerRepresentable {
    private let state: VoxaVisState

    init() {
        self.state = VoxaVisState(
            sessionMode: SessionMode.singafter,
            minPitchCents: -600,
            maxPitchCents: 600,
            trackLengthMs: Int64.max
        )
    }

    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController(state: state)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

// MARK: - Preview

#Preview {
    VoxaVisDemoView()
        .frame(height: 300)
}
