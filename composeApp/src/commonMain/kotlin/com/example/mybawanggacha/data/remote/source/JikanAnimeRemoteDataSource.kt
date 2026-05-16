package com.example.mybawanggacha.data.remote.source

import com.example.mybawanggacha.data.remote.api.JikanService
import com.example.mybawanggacha.data.remote.dto.AnimeDetailResponse
import com.example.mybawanggacha.data.remote.dto.AnimeEpisodesResponse
import com.example.mybawanggacha.data.remote.dto.JikanAnimeListResponse
import com.example.mybawanggacha.data.remote.dto.JikanRecommendationsResponse
import com.example.mybawanggacha.data.remote.dto.JikanSeasonArchiveResponse
import com.example.mybawanggacha.data.remote.dto.RelationEntryPreviewResponse

class JikanAnimeRemoteDataSource(
    private val service: JikanService
) {
    suspend fun fetchRecommendations(): JikanRecommendationsResponse {
        return service.fetchAnimeRecommendations()
    }

    suspend fun fetchCurrentSeasonAnime(page: Int): JikanAnimeListResponse {
        return service.fetchCurrentSeasonAnime(page = page)
    }

    suspend fun fetchSeasonAnime(
        year: Int,
        season: String,
        page: Int
    ): JikanAnimeListResponse {
        return service.fetchSeasonAnime(
            year = year,
            season = season,
            page = page
        )
    }

    suspend fun fetchUpcomingSeasonAnime(page: Int): JikanAnimeListResponse {
        return service.fetchUpcomingSeasonAnime(page = page)
    }

    suspend fun fetchTopAnime(page: Int): JikanAnimeListResponse {
        return service.fetchTopAnime(page = page)
    }

    suspend fun fetchSeasonArchive(): JikanSeasonArchiveResponse {
        return service.fetchSeasonArchive()
    }

    suspend fun fetchAnimeFullDetail(id: Int): AnimeDetailResponse {
        return service.fetchAnimeFullDetail(id)
    }

    suspend fun fetchAnimeEpisodes(id: Int): AnimeEpisodesResponse {
        return service.fetchAnimeEpisodes(id)
    }

    suspend fun fetchRelationEntryPreview(
        id: Int,
        type: String?
    ): RelationEntryPreviewResponse {
        return service.fetchRelationEntryPreview(id = id, type = type)
    }
}
