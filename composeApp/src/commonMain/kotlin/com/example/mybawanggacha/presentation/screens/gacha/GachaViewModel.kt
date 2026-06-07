package com.example.mybawanggacha.presentation.screens.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import com.example.mybawanggacha.domain.gacha.model.GachaMediaFormat
import com.example.mybawanggacha.domain.gacha.model.GachaPreference
import com.example.mybawanggacha.domain.gacha.model.GachaResultItem
import com.example.mybawanggacha.domain.gacha.model.GachaResultMediaType
import com.example.mybawanggacha.domain.gacha.repository.GachaRepository
import com.example.mybawanggacha.domain.gacha.usecase.RunGachaUseCase
import com.example.mybawanggacha.domain.library.model.LibraryEntry
import com.example.mybawanggacha.domain.library.model.LibraryStatus
import com.example.mybawanggacha.domain.library.model.UserProgress
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GachaViewModel(
    private val runGachaUseCase: RunGachaUseCase,
    private val gachaRepository: GachaRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GachaUiState())
    val uiState: StateFlow<GachaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gachaRepository.observeLastPreference().collect { preference ->
                _uiState.update { state ->
                    state.copy(preference = preference.withValidFormat())
                }
            }
        }

        viewModelScope.launch {
            gachaRepository.observeHistory().collect { history ->
                _uiState.update { state -> state.copy(history = history) }
            }
        }
    }

    fun updatePreference(transform: (GachaPreference) -> GachaPreference) {
        _uiState.update { state ->
            state.copy(
                preference = transform(state.preference).withValidFormat(),
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun runGacha() {
        val preference = _uiState.value.preference.withValidFormat()
        _uiState.update { state ->
            state.copy(
                preference = preference,
                isLoading = true,
                errorMessage = null,
                infoMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                gachaRepository.saveLastPreference(preference)
                runGachaUseCase(preference)
            }.onSuccess { item ->
                gachaRepository.saveHistoryEntry(GachaHistoryEntry(item = item))
                _uiState.update { state ->
                    state.copy(
                        result = item,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gacha gagal dijalankan."
                    )
                }
            }
        }
    }

    fun addResultToLibrary() {
        val item = _uiState.value.result ?: return

        viewModelScope.launch {
            runCatching {
                libraryRepository.upsertEntry(item.toLibraryEntry())
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        infoMessage = "Ditambahkan ke My Library",
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        errorMessage = error.message ?: "Gagal menambahkan ke My Library"
                    )
                }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            gachaRepository.clearHistory()
        }
    }

    private fun GachaPreference.withValidFormat(): GachaPreference {
        return if (format in GachaMediaFormat.availableFor(mediaPool)) {
            this
        } else {
            copy(format = GachaMediaFormat.Any)
        }
    }

    private fun GachaResultItem.toLibraryEntry(): LibraryEntry {
        return LibraryEntry(
            mediaId = malId,
            mediaType = toLibraryMediaType(),
            title = title,
            imageUrl = imageUrl,
            status = LibraryStatus.PlanToWatch,
            progress = UserProgress(
                current = 0,
                total = when (mediaType) {
                    GachaResultMediaType.Anime -> episodes
                    GachaResultMediaType.Manga -> chapters ?: volumes
                }
            ),
            notes = "Added from Gacha"
        )
    }
}
