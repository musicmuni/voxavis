package com.musicmuni.voxavis.sample.sections.recipes.view

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
fun RecipesListView(onBack: () -> Unit, onFeatureClick: (String) -> Unit) {
    BackHandler(onBack = onBack)
    val features = listOf(
        "Karaoke Screen" to "Full singing screen: canvas + seekbar + lyrics + tuning",
        "Smart Tanpura" to "Scrolling pitch monitor with tuning gauge + confidence",
        "Post-Session Summary" to "Score card, charts, and practice review combined",
        "Edge Cases" to "Empty data, silence gaps, extreme scales, tiny buffers",
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipes") },
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
