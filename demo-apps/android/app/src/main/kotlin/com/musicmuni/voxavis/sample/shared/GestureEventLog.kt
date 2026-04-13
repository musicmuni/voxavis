package com.musicmuni.voxavis.sample.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Displays a small scrollable log of recent gesture events.
 * Used to demonstrate callback wiring in demos.
 */
@Composable
fun GestureEventLog(
    events: List<String>,
    modifier: Modifier = Modifier,
    maxHeight: Int = 100,
) {
    if (events.isEmpty()) return
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Events", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn(
                modifier = Modifier.heightIn(max = maxHeight.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(events) { event ->
                    Text(
                        text = event,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
