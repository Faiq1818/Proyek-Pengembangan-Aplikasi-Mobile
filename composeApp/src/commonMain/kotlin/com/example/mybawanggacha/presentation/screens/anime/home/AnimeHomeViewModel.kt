package com.example.mybawanggacha.presentation.screens.anime.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.anime.model.AnimeSummary
import com.example.mybawanggacha.domain.anime.repository.AnimeRepository
import com.example.mybawanggacha.domain.manga.model.MangaSummary
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
                    loadRandomAnimePicks(count = RANDOM_ANIME_PICK_COUNT)
                }
                val randomManga = async {
                    loadRandomMangaPicks(count = RANDOM_MANGA_PICK_COUNT)
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
                homeState.randomAnime.isEmpty() &&
                homeState.randomManga.isEmpty() &&
                homeState.recentEpisodes.isEmpty()
            ) {
                _uiState.value = AnimeHomeUiState.Error("Gagal memuat data discovery dari Jikan")
            } else {
                _uiState.value = homeState
            }
        }
    }

    private suspend fun loadRandomAnimePicks(count: Int): List<AnimeSummary> {
        return buildList {
            repeat(count) { index ->
                runCatching { animeRepository.getRandomAnime() }
                    .getOrNull()
                    ?.let { anime -> add(anime) }

                if (index != count - 1) {
                    delay(JIKAN_RANDOM_PICK_SPACING_MS)
                }
            }
        }.distinctBy { anime -> anime.malId }
    }

    private suspend fun loadRandomMangaPicks(count: Int): List<MangaSummary> {
        return buildList {
            repeat(count) { index ->
                runCatching { mangaRepository.getRandomManga() }
                    .getOrNull()
                    ?.let { manga -> add(manga) }

                if (index != count - 1) {
                    delay(JIKAN_RANDOM_PICK_SPACING_MS)
                }
            }
        }.distinctBy { manga -> manga.malId }
    }

    private companion object {
        const val RANDOM_ANIME_PICK_COUNT = 4
        const val RANDOM_MANGA_PICK_COUNT = 4
        const val JIKAN_RANDOM_PICK_SPACING_MS = 360L
    }
}
