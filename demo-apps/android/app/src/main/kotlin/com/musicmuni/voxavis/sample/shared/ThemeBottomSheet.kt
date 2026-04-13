package com.musicmuni.voxavis.sample.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    themeState: ThemeSheetState,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        val pagerState = rememberPagerState(pageCount = { 3 })
        val scope = rememberCoroutineScope()
        val tabTitles = listOf("Presets", "Colors", "Style")

        Column {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title, style = MaterialTheme.typography.labelMedium) },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
            ) { page ->
                when (page) {
                    0 -> PresetsTab(themeState)
                    1 -> ColorsTab(themeState)
                    2 -> ComponentStyleTab(themeState)
                }
            }
        }
    }
}

@Composable
private fun PresetsTab(themeState: ThemeSheetState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Choose a preset theme", style = MaterialTheme.typography.titleMedium)
        Text(
            "Selecting a preset applies it app-wide. All components update instantly.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(4.dp))

        ThemeMode.entries.forEach { mode ->
            val label = when (mode) {
                ThemeMode.System -> "System"
                ThemeMode.Dark -> "Dark"
                ThemeMode.Light -> "Light"
                ThemeMode.Custom -> "Custom"
            }
            val description = when (mode) {
                ThemeMode.System -> "Follow device light/dark setting"
                ThemeMode.Dark -> "Default dark theme with teal/yellow accents"
                ThemeMode.Light -> "Light background with green/amber accents"
                ThemeMode.Custom -> "Customize via Colors tab"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                    Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                OptionChip(
                    selected = themeState.themeMode == mode,
                    onClick = { themeState.resetColorsToPreset(mode) },
                    label = if (themeState.themeMode == mode) "Active" else "Apply",
                )
            }
        }
    }
}

@Composable
private fun ColorsTab(themeState: ThemeSheetState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Theme Colors", style = MaterialTheme.typography.titleMedium)
        Text(
            "Change 5 color inputs and everything derives via M3. All components update live.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(4.dp))

        ColorPalette(
            label = "Primary",
            selectedColor = themeState.customColors.primary,
            onColorSelected = {
                themeState.customColors = themeState.customColors.copy(primary = it)
                themeState.themeMode = ThemeMode.Custom
            },
        )
        ColorPalette(
            label = "Secondary",
            selectedColor = themeState.customColors.secondary,
            onColorSelected = {
                themeState.customColors = themeState.customColors.copy(secondary = it)
                themeState.themeMode = ThemeMode.Custom
            },
        )
        ColorPalette(
            label = "Background",
            selectedColor = themeState.customColors.background,
            onColorSelected = {
                themeState.customColors = themeState.customColors.copy(background = it)
                themeState.themeMode = ThemeMode.Custom
            },
        )
        ColorPalette(
            label = "Positive",
            selectedColor = themeState.customColors.positive,
            onColorSelected = {
                themeState.customColors = themeState.customColors.copy(positive = it)
                themeState.themeMode = ThemeMode.Custom
            },
        )
        ColorPalette(
            label = "Negative",
            selectedColor = themeState.customColors.negative,
            onColorSelected = {
                themeState.customColors = themeState.customColors.copy(negative = it)
                themeState.themeMode = ThemeMode.Custom
            },
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { themeState.resetColorsToPreset(ThemeMode.Dark) }) {
                Text("Reset Dark")
            }
            OutlinedButton(onClick = { themeState.resetColorsToPreset(ThemeMode.Light) }) {
                Text("Reset Light")
            }
        }
    }
}

@Composable
private fun ComponentStyleTab(themeState: ThemeSheetState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Component Style", style = MaterialTheme.typography.titleMedium)

        val content = themeState.componentStyleContent
        if (content != null) {
            Text(
                "Override style properties for this specific component.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            content()
        } else {
            Text(
                "Navigate to a component demo to customize its style.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
