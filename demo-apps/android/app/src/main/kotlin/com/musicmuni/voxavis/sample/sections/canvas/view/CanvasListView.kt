package com.musicmuni.voxavis.sample.sections.canvas.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicmuni.voxavis.sample.FeatureCard
import com.musicmuni.voxavis.sample.PaletteAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasListView(onBack: () -> Unit, onFeatureClick: (String) -> Unit) {
    BackHandler(onBack = onBack)
    val features = listOf(
        "Singing Practice" to "Full session with modes, phases, gestures, loops",
        "Instant Pitch Monitor" to "Open-ended pitch with custom grid, tonic/fifth",
        "Scrolling Pitch Monitor" to "Live pitch with time scrolling, no scoring",
        "Practice Review" to "Post-session dual-contour playback",
        "Custom Composition" to "Build your own canvas from primitives + layouts",
        "Config Builder" to "Interactive SingingPractice parameter explorer",
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Canvas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(features) { (name, desc) ->
                FeatureCard(title = name, description = desc, onClick = { onFeatureClick(name) })
            }
        }
    }
}
