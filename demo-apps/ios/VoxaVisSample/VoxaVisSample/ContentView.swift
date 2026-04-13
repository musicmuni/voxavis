//
//  ContentView.swift
//  VoxaVisSample
//

import SwiftUI
import UIKit

// MARK: - Navigation Types

enum NavigationDestination: Hashable {
    case canvas
    case charts
    case indicators
    case navigation
    case canvasFeature(String)
    case chartsFeature(String)
    case indicatorsFeature(String)
    case navigationFeature(String)
}

// MARK: - Content View (Navigation Router)

struct ContentView: View {
    @State private var path: [NavigationDestination] = []

    var body: some View {
        NavigationStack(path: $path) {
            HomeView(onSelect: { destination in
                path.append(destination)
            })
            .navigationDestination(for: NavigationDestination.self) { dest in
                switch dest {
                case .canvas:
                    CanvasListView(onSelect: { feature in
                        path.append(.canvasFeature(feature))
                    })
                case .charts:
                    ChartsListView(onSelect: { feature in
                        path.append(.chartsFeature(feature))
                    })
                case .indicators:
                    IndicatorsListView(onSelect: { feature in
                        path.append(.indicatorsFeature(feature))
                    })
                case .navigation:
                    NavigationComponentsListView(onSelect: { feature in
                        path.append(.navigationFeature(feature))
                    })
                case .canvasFeature(let name):
                    CanvasFeatureView(featureName: name)
                case .chartsFeature(let name):
                    ChartsFeatureView(featureName: name)
                case .indicatorsFeature(let name):
                    IndicatorsFeatureView(featureName: name)
                case .navigationFeature(let name):
                    NavigationFeatureView(featureName: name)
                }
            }
        }
    }
}

// MARK: - Home View

struct HomeView: View {
    let onSelect: (NavigationDestination) -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                HomeCategoryCard(
                    icon: "waveform.path.ecg",
                    title: "Canvas",
                    subtitle: "Scrolling Pitch Canvas",
                    description: "Singing Session, Freestyle Pitch, Playback Review, Minimal, Config Builder",
                    color: .blue,
                    onTap: { onSelect(.canvas) }
                )

                HomeCategoryCard(
                    icon: "chart.bar.fill",
                    title: "Charts",
                    subtitle: "Offline Visualization",
                    description: "ScoreCard, RadarChart, NoteAccuracy, VocalRange, PitchScatter, ScoreTrend, MetricsList, RingMeter",
                    color: .green,
                    onTap: { onSelect(.charts) }
                )

                HomeCategoryCard(
                    icon: "gauge.with.needle.fill",
                    title: "Indicators",
                    subtitle: "Real-Time Feedback",
                    description: "TuningGauge, BeatIndicator, LevelMeter, ConfidenceMeter",
                    color: .orange,
                    onTap: { onSelect(.indicators) }
                )

                HomeCategoryCard(
                    icon: "text.below.photo.fill",
                    title: "Navigation",
                    subtitle: "Seek & Lyrics",
                    description: "SegmentedSeekBar, LyricsOverlay",
                    color: .purple,
                    onTap: { onSelect(.navigation) }
                )
            }
            .padding()
        }
        .navigationTitle("VoxaVis Demo")
    }
}

// MARK: - Home Category Card

struct HomeCategoryCard: View {
    let icon: String
    let title: String
    let subtitle: String
    let description: String
    let color: Color
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: 12) {
                HStack {
                    Image(systemName: icon)
                        .font(.system(size: 32))
                        .foregroundColor(color)
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundColor(.secondary)
                }

                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.title2)
                        .fontWeight(.semibold)
                        .foregroundColor(.primary)
                    Text(subtitle)
                        .font(.subheadline)
                        .foregroundColor(color)
                }

                Text(description)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)
            }
            .padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(UIColor.secondarySystemBackground))
            .cornerRadius(16)
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Feature Row

struct FeatureRow: View {
    let title: String
    let description: String

    var body: some View {
        VStack(alignment: .leading) {
            Text(title)
                .font(.headline)
                .foregroundColor(.primary)
            Text(description)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Category List Views

struct CanvasListView: View {
    let onSelect: (String) -> Void

    private let features = [
        ("Singing Session", "Full session with segments, notes, pitch tracking"),
        ("Freestyle Pitch", "Open-ended pitch exploration with pitch ball"),
        ("Playback Review", "Review recorded reference pitch playback"),
        ("Minimal Mode", "Grid lines + user pitch only"),
        ("Config Builder", "Interactive VoxaVisState configuration demo"),
    ]

    var body: some View {
        List {
            ForEach(features, id: \.0) { feature in
                Button { onSelect(feature.0) } label: {
                    FeatureRow(title: feature.0, description: feature.1)
                }
            }
        }
        .navigationTitle("Canvas")
    }
}

struct ChartsListView: View {
    let onSelect: (String) -> Void

    private let features = [
        ("ScoreCard", "Score display with rating"),
        ("RadarChart", "Multi-axis voice quality radar"),
        ("NoteAccuracyChart", "Per-note accuracy visualization"),
        ("VocalRangeChart", "Vocal range with live marker"),
        ("PitchScatterPlot", "Pitch contour scatter plot"),
        ("ScoreTrendChart", "Session score trend line"),
        ("MetricsList", "Formatted voice metrics table"),
        ("RingMeter", "Radial breath capacity gauge"),
    ]

    var body: some View {
        List {
            ForEach(features, id: \.0) { feature in
                Button { onSelect(feature.0) } label: {
                    FeatureRow(title: feature.0, description: feature.1)
                }
            }
        }
        .navigationTitle("Charts")
    }
}

struct IndicatorsListView: View {
    let onSelect: (String) -> Void

    private let features = [
        ("TuningGauge", "Needle gauge showing pitch accuracy"),
        ("BeatIndicator", "Visual beat indicator for metronome"),
        ("LevelMeter", "Volume unit meter with segments"),
        ("ConfidenceMeter", "Pitch detection confidence bar"),
    ]

    var body: some View {
        List {
            ForEach(features, id: \.0) { feature in
                Button { onSelect(feature.0) } label: {
                    FeatureRow(title: feature.0, description: feature.1)
                }
            }
        }
        .navigationTitle("Indicators")
    }
}

struct NavigationComponentsListView: View {
    let onSelect: (String) -> Void

    private let features = [
        ("SegmentedSeekBar", "Tappable segmented progress bar"),
        ("LyricsOverlay", "Scrolling lyrics with highlight"),
    ]

    var body: some View {
        List {
            ForEach(features, id: \.0) { feature in
                Button { onSelect(feature.0) } label: {
                    FeatureRow(title: feature.0, description: feature.1)
                }
            }
        }
        .navigationTitle("Navigation")
    }
}

// MARK: - Feature Detail Views (Routers)

struct CanvasFeatureView: View {
    let featureName: String

    var body: some View {
        ScrollView {
            featureContent
                .padding()
        }
        .navigationTitle(featureName)
        .navigationBarTitleDisplayMode(.inline)
    }

    @ViewBuilder
    private var featureContent: some View {
        switch featureName {
        case "Singing Session":
            PracticeDemoView()
        case "Freestyle Pitch":
            FreestyleDemoView()
        case "Playback Review":
            PlaybackDemoView()
        case "Minimal Mode":
            MinimalDemoView()
        case "Config Builder":
            ConfigBuilderDemoView()
        default:
            Text("Unknown feature: \(featureName)")
        }
    }
}

struct ChartsFeatureView: View {
    let featureName: String

    var body: some View {
        ScrollView {
            featureContent
                .padding()
        }
        .navigationTitle(featureName)
        .navigationBarTitleDisplayMode(.inline)
    }

    @ViewBuilder
    private var featureContent: some View {
        switch featureName {
        case "ScoreCard":
            ScoreCardDemoView()
        case "RadarChart":
            RadarChartDemoView()
        case "NoteAccuracyChart":
            NoteAccuracyChartDemoView()
        case "VocalRangeChart":
            VocalRangeChartDemoView()
        case "PitchScatterPlot":
            PitchScatterPlotDemoView()
        case "ScoreTrendChart":
            ScoreTrendChartDemoView()
        case "MetricsList":
            MetricsListDemoView()
        case "RingMeter":
            RingMeterDemoView()
        default:
            Text("Unknown feature: \(featureName)")
        }
    }
}

struct IndicatorsFeatureView: View {
    let featureName: String

    var body: some View {
        ScrollView {
            featureContent
                .padding()
        }
        .navigationTitle(featureName)
        .navigationBarTitleDisplayMode(.inline)
    }

    @ViewBuilder
    private var featureContent: some View {
        switch featureName {
        case "TuningGauge":
            TuningGaugeDemoView()
        case "BeatIndicator":
            BeatIndicatorDemoView()
        case "LevelMeter":
            LevelMeterDemoView()
        case "ConfidenceMeter":
            ConfidenceMeterDemoView()
        default:
            Text("Unknown feature: \(featureName)")
        }
    }
}

struct NavigationFeatureView: View {
    let featureName: String

    var body: some View {
        ScrollView {
            featureContent
                .padding()
        }
        .navigationTitle(featureName)
        .navigationBarTitleDisplayMode(.inline)
    }

    @ViewBuilder
    private var featureContent: some View {
        switch featureName {
        case "SegmentedSeekBar":
            SegmentedSeekBarDemoView()
        case "LyricsOverlay":
            LyricsOverlayDemoView()
        default:
            Text("Unknown feature: \(featureName)")
        }
    }
}

#Preview {
    ContentView()
}
