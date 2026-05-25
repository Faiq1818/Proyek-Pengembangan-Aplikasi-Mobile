package com.example.mybawanggacha.data.repository.search

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.local.source.MediaPageCacheLocalDataSource
import com.example.mybawanggacha.data.remote.jikan.mapper.toSearchPage
import com.example.mybawanggacha.data.remote.jikan.source.JikanSearchRemoteDataSource
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.MediaSearchPage
import com.example.mybawanggacha.domain.search.model.SearchFilterMetadata
import com.example.mybawanggacha.domain.search.model.SearchMediaType
import com.example.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val SEARCH_METADATA_ANIME_CACHE_KEY = "search_metadata:anime"
private const val SEARCH_METADATA_MANGA_CACHE_KEY = "search_metadata:manga"

class SearchRepositoryImpl(
    private val remoteDataSource: JikanSearchRemoteDataSource,
    private val dispatchers: AppDispatchers,
    private val mediaPageCacheLocalDataSource: MediaPageCacheLocalDataSource? = null
) : SearchRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun search(
        filters: MediaSearchFilters,
        page: Int
    ): MediaSearchPage = withContext(dispatchers.default) {
        when (filters.mediaType) {
            SearchMediaType.Anime -> remoteDataSource
                .searchAnime(filters = filters, page = page)
                .toSearchPage(mediaType = SearchMediaType.Anime, requestedPage = page)

            SearchMediaType.Manga -> remoteDataSource
                .searchManga(filters = filters, page = page)
                .toSearchPage(mediaType = SearchMediaType.Manga, requestedPage = page)
        }
    }

    override suspend fun getFilterMetadata(
        mediaType: SearchMediaType
    ): SearchFilterMetadata = withContext(dispatchers.default) {
        val cacheKey = mediaType.metadataCacheKey()
        val cachedPayload = runCatching {
            mediaPageCacheLocalDataSource?.getPage(cacheKey)
        }.getOrNull()

        cachedPayload
            ?.takeIf { payload -> payload.isFresh() }
            ?.decodeMetadataOrNull()
            ?.let { metadata -> return@withContext metadata }

        runCatching {
            fetchRemoteFilterMetadata(mediaType).also { metadata ->
                runCatching {
                    mediaPageCacheLocalDataSource?.savePage(
                        cacheKey = cacheKey,
                        payloadJson = json.encodeToString(metadata)
                    )
                }
            }
        }.getOrElse { error ->
            cachedPayload?.decodeMetadataOrNull() ?: throw error
        }
    }

    private suspend fun fetchRemoteFilterMetadata(
        mediaType: SearchMediaType
    ): SearchFilterMetadata {
        return when (mediaType) {
            SearchMediaType.Anime -> remoteDataSource.getAnimeFilterMetadata()
            SearchMediaType.Manga -> remoteDataSource.getMangaFilterMetadata()
        }
    }

    private fun SearchMediaType.metadataCacheKey(): String {
        return when (this) {
            SearchMediaType.Anime -> SEARCH_METADATA_ANIME_CACHE_KEY
            SearchMediaType.Manga -> SEARCH_METADATA_MANGA_CACHE_KEY
        }
    }

    private fun com.example.mybawanggacha.data.local.source.CachedMediaPagePayload.decodeMetadataOrNull(): SearchFilterMetadata? {
        return runCatching {
            json.decodeFromString<SearchFilterMetadata>(payloadJson)
        }.getOrNull()
    }
}