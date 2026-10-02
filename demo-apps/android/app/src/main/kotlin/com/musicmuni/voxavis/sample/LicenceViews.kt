package com.musicmuni.voxavis.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.musicmuni.voxavis.sample.VoxaVisLicence.Status

/** The licence state on the home screen. Nothing once the SDK is ready. */
@Composable
fun LicenceBanner() {
    val status = VoxaVisLicence.status
    if (status == Status.Ready) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        LicenceMessage(status, modifier = Modifier.padding(16.dp))
    }
}

/** Shown in place of a screen that draws a feature canvas, until the SDK is ready. */
@Composable
fun LicenceRequired() {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        LicenceMessage(VoxaVisLicence.status)
    }
}

@Composable
private fun LicenceMessage(status: Status, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (status) {
            Status.NotConfigured -> {
                Text("No VoxaVis licence key", style = MaterialTheme.typography.titleMedium)
                Text(
                    "The canvas screens and recipes need one. Charts, indicators and " +
                        "navigation work without it. Add this line to local.properties " +
                        "next to settings.gradle.kts, then rebuild:",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "voxavis.apiKey=YOUR_API_KEY",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Status.Starting -> {
                Text("Starting VoxaVis", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Registering this device with the licence server.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            is Status.Failed -> {
                Text(
                    "VoxaVis licence error",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(status.message, style = MaterialTheme.typography.bodyMedium)
            }
            Status.Ready -> Unit
        }
    }
}
