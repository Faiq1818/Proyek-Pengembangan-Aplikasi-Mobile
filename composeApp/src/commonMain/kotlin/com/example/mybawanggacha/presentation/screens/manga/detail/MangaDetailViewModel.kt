package com.example.mybawanggacha.presentation.screens.manga.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.library.model.MediaType
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MangaDetailViewModel(
    private val mangaRepository: MangaRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MangaDetailUiState>(MangaDetailUiState.Loading)
    val uiState: StateFlow<MangaDetailUiState> = _uiState.asStateFlow()

    fun fetchMangaDetail(malId: Int) {
        viewModelScope.launch {
            _uiState.value = MangaDetailUiState.Loading

            runCatching {
                val manga = mangaRepository.getMangaDetail(malId)
                val existingEntry = libraryRepository.getEntry(
                    mediaId = manga.malId,
                    mediaType = MediaType.Manga
                )

                manga to existingEntry
            }.onSuccess { (manga, existingEntry) ->
                _uiState.value = MangaDetailUiState.Success(
                    manga = manga,
                    libraryEntryId = existingEntry?.id
                )
            }.onFailure { error ->
                _uiState.value = MangaDetailUiState.Error(
                    error.message ?: "Unknown error occurred"
                )
            }
        }
    }
}
