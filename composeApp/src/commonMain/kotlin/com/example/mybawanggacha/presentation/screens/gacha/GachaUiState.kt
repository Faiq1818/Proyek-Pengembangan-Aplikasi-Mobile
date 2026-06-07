package com.example.mybawanggacha.presentation.screens.gacha

import com.example.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import com.example.mybawanggacha.domain.gacha.model.GachaPreference
import com.example.mybawanggacha.domain.gacha.model.GachaResultItem

data class GachaUiState(
    val preference: GachaPreference = GachaPreference(),
    val result: GachaResultItem? = null,
    val history: List<GachaHistoryEntry> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)
