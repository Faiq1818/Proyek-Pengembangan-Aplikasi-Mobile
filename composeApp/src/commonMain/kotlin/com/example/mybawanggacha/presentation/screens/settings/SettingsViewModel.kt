package com.example.mybawanggacha.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.settings.model.NetworkMode
import com.example.mybawanggacha.domain.settings.model.ThemeMode
import com.example.mybawanggacha.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = kotlinx.coroutines.flow.combine(
        settingsRepository.themeMode,
        settingsRepository.networkMode
    ) { themeMode, networkMode ->
        SettingsUiState(
            themeMode = themeMode,
            networkMode = networkMode
        )
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }

    fun setNetworkMode(networkMode: NetworkMode) {
        viewModelScope.launch {
            settingsRepository.setNetworkMode(networkMode)
        }
    }
}
