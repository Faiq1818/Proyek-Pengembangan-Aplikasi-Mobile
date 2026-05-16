package com.example.mybawanggacha.presentation.screens.anime

import com.example.mybawanggacha.domain.model.AnimeSummary

sealed interface AnimeListUiState {
    data object Loading : AnimeListUiState

    data class Success(
        val title: String,
        val subtitle: String,
        val anime: List<AnimeSummary>
    ) : AnimeListUiState

    data class Error(val message: String) : AnimeListUiState
}
