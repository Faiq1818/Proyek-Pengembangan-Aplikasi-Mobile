package com.example.mybawanggacha.presentation.screens.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.data.remote.api.JikanService
import com.example.mybawanggacha.data.remote.dto.AnimeEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AnimeHomeUiState {
    data object Loading : AnimeHomeUiState
    data class Success(val recommendations: List<AnimeEntry>) : AnimeHomeUiState
    data class Error(val message: String) : AnimeHomeUiState
}

class AnimeHomeViewModel(
    private val jikanService: JikanService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeHomeUiState>(AnimeHomeUiState.Loading)
    val uiState: StateFlow<AnimeHomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = AnimeHomeUiState.Loading

            runCatching {
                jikanService.fetch()
                    .data
                    .flatMap { it.entry }
                    .distinctBy { it.mal_id }
            }.onSuccess { recommendations ->
                _uiState.value = AnimeHomeUiState.Success(recommendations)
            }.onFailure { error ->
                _uiState.value = AnimeHomeUiState.Error(
                    error.message ?: "Gagal memuat rekomendasi anime"
                )
            }
        }
    }
}
