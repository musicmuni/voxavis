package com.musicmuni.voxavis.sample.sections.navigation.view

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
fun NavigationListView(onBack: () -> Unit, onFeatureClick: (String) -> Unit) {
    BackHandler(onBack = onBack)
    val features = listOf(
        "SegmentedSeekBar" to "Tappable segmented progress bar",
        "LyricsOverlay" to "Scrolling lyrics with highlight",
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Navigation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { PaletteAction() },
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
