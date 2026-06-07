package com.example.mybawanggacha.presentation.screens.settings

import com.example.mybawanggacha.domain.settings.model.NetworkMode
import com.example.mybawanggacha.domain.settings.model.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val networkMode: NetworkMode = NetworkMode.Auto
)
