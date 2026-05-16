package com.example.mybawanggacha.data.remote.source

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.remote.api.JikanService
import com.example.mybawanggacha.data.remote.dto.AnimeDetailResponse
import com.example.mybawanggacha.data.remote.dto.AnimeEpisodesResponse
import com.example.mybawanggacha.data.remote.dto.JikanAnimeListResponse
import com.example.mybawanggacha.data.remote.dto.JikanRecommendationsResponse
import com.example.mybawanggacha.data.remote.dto.JikanSeasonArchiveResponse
import com.example.mybawanggacha.data.remote.dto.RelationEntryPreviewResponse
import kotlinx.coroutines.withContext

class JikanAnimeRemoteDataSource(
    private val service: JikanService,
    private val dispatchers: AppDispatchers
) {
    suspend fun fetchAnimeRecommendations(): JikanRecommendationsResponse = withContext(dispatchers.io) {
        service.fetchAnimeRecommendations()
    }

    suspend fun fetchCurrentSeasonAnime(page: Int): JikanAnimeListResponse = withContext(dispatchers.io) {
        service.fetchCurrentSeasonAnime(page = page)
    }

    suspend fun fetchSeasonAnime(
        year: Int,
        season: String,
        page: Int
    ): JikanAnimeListResponse = withContext(dispatchers.io) {
        service.fetchSeasonAnime(
            year = year,
            season = season,
            page = page
        )
    }

    suspend fun fetchUpcomingSeasonAnime(page: Int): JikanAnimeListResponse = withContext(dispatchers.io) {
        service.fetchUpcomingSeasonAnime(page = page)
    }

    suspend fun fetchTopAnime(page: Int): JikanAnimeListResponse = withContext(dispatchers.io) {
        service.fetchTopAnime(page = page)
    }

    suspend fun fetchSeasonArchive(): JikanSeasonArchiveResponse = withContext(dispatchers.io) {
        service.fetchSeasonArchive()
    }

    suspend fun fetchAnimeFullDetail(id: Int): AnimeDetailResponse = withContext(dispatchers.io) {
        service.fetchAnimeFullDetail(id)
    }

    suspend fun fetchAnimeEpisodes(id: Int): AnimeEpisodesResponse = withContext(dispatchers.io) {
        service.fetchAnimeEpisodes(id)
    }

    suspend fun fetchRelationEntryPreview(
        id: Int,
        type: String?
    ): RelationEntryPreviewResponse = withContext(dispatchers.io) {
        service.fetchRelationEntryPreview(id = id, type = type)
    }
}
