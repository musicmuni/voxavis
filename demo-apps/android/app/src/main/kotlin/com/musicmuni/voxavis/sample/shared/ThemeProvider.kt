package com.musicmuni.voxavis.sample.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.musicmuni.voxavis.theme.VoxaVisPresets
import com.musicmuni.voxavis.theme.VoxaVisTheme

@Composable
fun ThemeProvider(content: @Composable () -> Unit) {
    val themeState = remember { ThemeSheetState() }

    val isDark = when (themeState.themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Dark -> true
        ThemeMode.Light -> false
        ThemeMode.Custom -> isSystemInDarkTheme()
    }

    // For System mode, resolve VoxaVis colors based on device theme
    val resolvedColors = if (themeState.themeMode == ThemeMode.System) {
        if (isDark) VoxaVisPresets.Dark else VoxaVisPresets.Light
    } else {
        themeState.resolvedColors()
    }

    CompositionLocalProvider(LocalThemeSheetState provides themeState) {
        VoxaVisTheme(
            colors = resolvedColors,
            darkTheme = isDark,
        ) {
            content()
        }
    }
}
