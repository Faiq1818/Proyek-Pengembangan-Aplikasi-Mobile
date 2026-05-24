package com.example.mybawanggacha.presentation.screens.anime.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.anime.repository.AnimeRepository
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class AnimeHomeViewModel(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeHomeUiState>(AnimeHomeUiState.Loading)
    val uiState: StateFlow<AnimeHomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = AnimeHomeUiState.Loading

            val homeState = supervisorScope {
                val recommendations = async {
                    runCatching { animeRepository.getRecommendations() }.getOrDefault(emptyList())
                }
                val randomAnime = async {
                    runCatching { animeRepository.getRandomAnime() }.getOrNull()
                }
                val randomManga = async {
                    runCatching { mangaRepository.getRandomManga() }.getOrNull()
                }
                val recentEpisodes = async {
                    runCatching { animeRepository.getRecentEpisodes() }.getOrDefault(emptyList())
                }

                AnimeHomeUiState.Success(
                    recommendations = recommendations.await(),
                    randomAnime = randomAnime.await(),
                    randomManga = randomManga.await(),
                    recentEpisodes = recentEpisodes.await()
                )
            }

            if (
                homeState.recommendations.isEmpty() &&
                homeState.randomAnime == null &&
                homeState.randomManga == null &&
                homeState.recentEpisodes.isEmpty()
            ) {
                _uiState.value = AnimeHomeUiState.Error("Gagal memuat data discovery dari Jikan")
            } else {
                _uiState.value = homeState
            }
        }
    }
}
