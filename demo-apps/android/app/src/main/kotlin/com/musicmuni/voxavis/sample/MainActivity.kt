package com.musicmuni.voxavis.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musicmuni.voxavis.sample.sections.canvas.view.CanvasListView
import com.musicmuni.voxavis.sample.sections.charts.view.ChartsListView
import com.musicmuni.voxavis.sample.sections.indicators.view.IndicatorsListView
import com.musicmuni.voxavis.sample.sections.navigation.view.NavigationListView
import com.musicmuni.voxavis.sample.sections.recipes.view.RecipesListView
import com.musicmuni.voxavis.sample.shared.LocalThemeSheetState
import com.musicmuni.voxavis.sample.shared.ThemeBottomSheet
import com.musicmuni.voxavis.sample.shared.ThemeProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ThemeProvider {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VoxaVisSampleApp()
                }
            }
        }
    }
}

sealed class Screen {
    data object Home : Screen()
    data object CanvasList : Screen()
    data object ChartsList : Screen()
    data object IndicatorsList : Screen()
    data object NavigationList : Screen()
    data object RecipesList : Screen()
    data class Feature(val category: String, val name: String) : Screen()
}

data class CategoryItem(val title: String, val subtitle: String, val description: String)

@Composable
fun VoxaVisSampleApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val themeState = LocalThemeSheetState.current

    // Global theme bottom sheet
    if (themeState.showSheet) {
        ThemeBottomSheet(
            themeState = themeState,
            onDismiss = { themeState.showSheet = false },
        )
    }

    when (val screen = currentScreen) {
        Screen.Home -> HomeScreen(
            onCategoryClick = { category ->
                currentScreen = when (category) {
                    "Canvas" -> Screen.CanvasList
                    "Charts" -> Screen.ChartsList
                    "Indicators" -> Screen.IndicatorsList
                    "Navigation" -> Screen.NavigationList
                    "Recipes" -> Screen.RecipesList
                    else -> Screen.Home
                }
            }
        )
        Screen.CanvasList -> CanvasListView(
            onBack = { currentScreen = Screen.Home },
            onFeatureClick = { name -> currentScreen = Screen.Feature("Canvas", name) }
        )
        Screen.ChartsList -> ChartsListView(
            onBack = { currentScreen = Screen.Home },
            onFeatureClick = { name -> currentScreen = Screen.Feature("Charts", name) }
        )
        Screen.IndicatorsList -> IndicatorsListView(
            onBack = { currentScreen = Screen.Home },
            onFeatureClick = { name -> currentScreen = Screen.Feature("Indicators", name) }
        )
        Screen.NavigationList -> NavigationListView(
            onBack = { currentScreen = Screen.Home },
            onFeatureClick = { name -> currentScreen = Screen.Feature("Navigation", name) }
        )
        Screen.RecipesList -> RecipesListView(
            onBack = { currentScreen = Screen.Home },
            onFeatureClick = { name -> currentScreen = Screen.Feature("Recipes", name) }
        )
        is Screen.Feature -> FeatureScreen(
            category = screen.category,
            feature = screen.name,
            onBack = {
                currentScreen = when (screen.category) {
                    "Canvas" -> Screen.CanvasList
                    "Charts" -> Screen.ChartsList
                    "Indicators" -> Screen.IndicatorsList
                    "Navigation" -> Screen.NavigationList
                    "Recipes" -> Screen.RecipesList
                    else -> Screen.Home
                }
            }
        )
    }
}

@Composable
fun PaletteAction() {
    val themeState = LocalThemeSheetState.current
    IconButton(onClick = { themeState.showSheet = true }) {
        Icon(Icons.Default.Palette, contentDescription = "Theme")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onCategoryClick: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VoxaVis Demo") },
                actions = { PaletteAction() },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val categories = listOf(
                CategoryItem("Canvas", "Scrolling Pitch Canvas", "Singing Practice, Instant Pitch Monitor, Scrolling Pitch Monitor, Practice Review, Custom Composition, Config Builder"),
                CategoryItem("Charts", "Offline Visualization", "ScoreCard, RadarChart, NoteAccuracy, VocalRange, PitchScatter, ScoreTrend, MetricsList, RingMeter"),
                CategoryItem("Indicators", "Real-Time Feedback", "TuningGauge, BeatIndicator, LevelMeter, ConfidenceMeter"),
                CategoryItem("Navigation", "Seek & Lyrics", "SegmentedSeekBar, LyricsOverlay"),
                CategoryItem("Recipes", "Integration Patterns", "Karaoke Screen, Smart Tanpura, Post-Session Summary, Edge Cases"),
            )
            categories.forEach { cat ->
                CategoryCard(
                    title = cat.title,
                    subtitle = cat.subtitle,
                    description = cat.description,
                    onClick = { onCategoryClick(cat.title) }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, subtitle: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FeatureCard(title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureScreen(category: String, feature: String, onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(feature) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { PaletteAction() },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FeatureRouter(category = category, feature = feature, onBack = onBack)
        }
    }
}
