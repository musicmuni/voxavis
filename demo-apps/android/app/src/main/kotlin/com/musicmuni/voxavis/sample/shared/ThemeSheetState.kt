package com.musicmuni.voxavis.sample.shared

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import com.musicmuni.voxavis.theme.VoxaVisColors
import com.musicmuni.voxavis.theme.VoxaVisPresets

enum class ThemeMode { System, Dark, Light, Custom }

class ThemeSheetState {
    // Tab 1: Presets
    var themeMode by mutableStateOf(ThemeMode.System)

    // Tab 2: Colors
    var customColors by mutableStateOf(VoxaVisColors())

    // Tab 3: Component-specific style controls (set by each demo)
    var componentStyleContent: (@Composable () -> Unit)? by mutableStateOf(null)

    // Bottom sheet visibility
    var showSheet by mutableStateOf(false)

    fun resolvedColors(): VoxaVisColors = when (themeMode) {
        ThemeMode.System -> VoxaVisPresets.Dark // actual resolution in ThemeProvider via isSystemInDarkTheme()
        ThemeMode.Dark -> VoxaVisPresets.Dark
        ThemeMode.Light -> VoxaVisPresets.Light
        ThemeMode.Custom -> customColors
    }

    fun resetColorsToPreset(mode: ThemeMode) {
        themeMode = mode
        when (mode) {
            ThemeMode.Dark -> customColors = VoxaVisPresets.Dark
            ThemeMode.Light -> customColors = VoxaVisPresets.Light
            ThemeMode.System, ThemeMode.Custom -> {}
        }
    }
}

val LocalThemeSheetState = compositionLocalOf { ThemeSheetState() }
