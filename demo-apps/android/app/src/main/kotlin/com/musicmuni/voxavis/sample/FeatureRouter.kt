package com.musicmuni.voxavis.sample

import androidx.compose.runtime.Composable
import com.musicmuni.voxavis.sample.sections.canvas.practice.view.PracticeView
import com.musicmuni.voxavis.sample.sections.canvas.freestyle.view.FreestyleView
import com.musicmuni.voxavis.sample.sections.canvas.playback.view.PlaybackView
import com.musicmuni.voxavis.sample.sections.canvas.minimal.view.MinimalView
import com.musicmuni.voxavis.sample.sections.canvas.configbuilder.view.ConfigBuilderView
import com.musicmuni.voxavis.sample.sections.canvas.scrollingmonitor.view.ScrollingMonitorView
import com.musicmuni.voxavis.sample.sections.charts.scorecard.view.ScoreCardView
import com.musicmuni.voxavis.sample.sections.charts.radarchart.view.RadarChartDemoView
import com.musicmuni.voxavis.sample.sections.charts.noteaccuracy.view.NoteAccuracyChartView
import com.musicmuni.voxavis.sample.sections.charts.vocalrange.view.VocalRangeChartView
import com.musicmuni.voxavis.sample.sections.charts.pitchscatter.view.PitchScatterPlotView
import com.musicmuni.voxavis.sample.sections.charts.scoretrend.view.ScoreTrendChartView
import com.musicmuni.voxavis.sample.sections.charts.metricslist.view.MetricsListDemoView
import com.musicmuni.voxavis.sample.sections.charts.ringmeter.view.RingMeterView
import com.musicmuni.voxavis.sample.sections.indicators.tuninggauge.view.TuningGaugeView
import com.musicmuni.voxavis.sample.sections.indicators.beatindicator.view.BeatIndicatorView
import com.musicmuni.voxavis.sample.sections.indicators.levelmeter.view.LevelMeterDemoView
import com.musicmuni.voxavis.sample.sections.indicators.confidencemeter.view.ConfidenceMeterView
import com.musicmuni.voxavis.sample.sections.navigation.segmentedseekbar.view.SegmentedSeekBarView
import com.musicmuni.voxavis.sample.sections.navigation.lyricsoverlay.view.LyricsOverlayView
import com.musicmuni.voxavis.sample.sections.recipes.karaoke.view.KaraokeRecipeView
import com.musicmuni.voxavis.sample.sections.recipes.tanpura.view.TanpuraRecipeView
import com.musicmuni.voxavis.sample.sections.recipes.summary.view.SummaryRecipeView
import com.musicmuni.voxavis.sample.sections.recipes.edgecases.view.EdgeCasesRecipeView

@Composable
fun FeatureRouter(category: String, feature: String, onBack: () -> Unit) {
    when (category) {
        "Canvas" -> when (feature) {
            "Singing Practice" -> PracticeView()
            "Instant Pitch Monitor" -> FreestyleView()
            "Scrolling Pitch Monitor" -> ScrollingMonitorView()
            "Practice Review" -> PlaybackView()
            "Custom Composition" -> MinimalView()
            "Config Builder" -> ConfigBuilderView()
        }
        "Charts" -> when (feature) {
            "ScoreCard" -> ScoreCardView()
            "RadarChart" -> RadarChartDemoView()
            "NoteAccuracyChart" -> NoteAccuracyChartView()
            "VocalRangeChart" -> VocalRangeChartView()
            "PitchScatterPlot" -> PitchScatterPlotView()
            "ScoreTrendChart" -> ScoreTrendChartView()
            "MetricsList" -> MetricsListDemoView()
            "RingMeter" -> RingMeterView()
        }
        "Indicators" -> when (feature) {
            "TuningGauge" -> TuningGaugeView()
            "BeatIndicator" -> BeatIndicatorView()
            "LevelMeter" -> LevelMeterDemoView()
            "ConfidenceMeter" -> ConfidenceMeterView()
        }
        "Navigation" -> when (feature) {
            "SegmentedSeekBar" -> SegmentedSeekBarView()
            "LyricsOverlay" -> LyricsOverlayView()
        }
        "Recipes" -> when (feature) {
            "Karaoke Screen" -> KaraokeRecipeView()
            "Smart Tanpura" -> TanpuraRecipeView()
            "Post-Session Summary" -> SummaryRecipeView()
            "Edge Cases" -> EdgeCasesRecipeView()
        }
    }
}
