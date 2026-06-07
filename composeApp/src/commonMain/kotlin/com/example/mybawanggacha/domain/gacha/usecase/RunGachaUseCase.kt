package com.example.mybawanggacha.domain.gacha.usecase

import com.example.mybawanggacha.domain.gacha.model.GachaPreference
import com.example.mybawanggacha.domain.gacha.model.GachaResultItem
import com.example.mybawanggacha.domain.gacha.model.toGachaResultItem
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.MediaSearchItem
import com.example.mybawanggacha.domain.search.model.SearchMediaType
import com.example.mybawanggacha.domain.search.repository.SearchRepository
import kotlin.random.Random

class RunGachaUseCase(
    private val searchRepository: SearchRepository,
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(
        preference: GachaPreference,
        random: Random = Random.Default
    ): GachaResultItem {
        val knownKeys = if (preference.includeKnownItems) {
            emptySet()
        } else {
            libraryRepository.getEntries()
                .map { entry -> "${entry.mediaType.storageKey}:${entry.mediaId}" }
                .toSet()
        }

        val candidates = preference.mediaPool
            .searchMediaTypes()
            .filter { mediaType -> preference.format.supports(mediaType) }
            .flatMap { mediaType ->
                searchCandidates(
                    mediaType = mediaType,
                    preference = preference
                )
            }
            .filterByMinimumScore(preference.minScore)
            .filterNot { item ->
                val libraryMediaType = item.mediaType.toLibraryStorageKey()
                "$libraryMediaType:${item.malId}" in knownKeys
            }
            .distinctBy { item -> "${item.mediaType}:${item.malId}" }

        if (candidates.isEmpty()) {
            error("Tidak ada kandidat gacha yang cocok. Coba longgarkan filter.")
        }

        return candidates.random(random).toGachaResultItem()
    }

    private suspend fun searchCandidates(
        mediaType: SearchMediaType,
        preference: GachaPreference
    ): List<MediaSearchItem> {
        val filters = MediaSearchFilters(
            mediaType = mediaType,
            limit = "25",
            minScore = preference.minScore.trim(),
            status = preference.status.searchValueFor(mediaType),
            type = preference.format.searchValueFor(mediaType),
            sfw = true,
            genres = preference.genreIds.trim()
        )

        return searchRepository
            .search(filters = filters, page = 1)
            .items
    }

    private fun List<MediaSearchItem>.filterByMinimumScore(minScore: String): List<MediaSearchItem> {
        val minimum = minScore.trim().toDoubleOrNull() ?: return this
        return filter { item -> item.score != null && item.score >= minimum }
    }

    private fun SearchMediaType.toLibraryStorageKey(): String {
        return when (this) {
            SearchMediaType.Anime -> "ANIME"
            SearchMediaType.Manga -> "MANGA"
        }
    }
}
