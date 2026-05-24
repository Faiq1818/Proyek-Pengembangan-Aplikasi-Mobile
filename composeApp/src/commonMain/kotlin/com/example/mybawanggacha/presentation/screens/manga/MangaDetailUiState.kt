package com.example.mybawanggacha.presentation.screens.manga

import com.example.mybawanggacha.domain.manga.model.MangaDetail

sealed interface MangaDetailUiState {
    data object Loading : MangaDetailUiState
    data class Success(val manga: MangaDetail) : MangaDetailUiState
    data class Error(val message: String) : MangaDetailUiState
}
