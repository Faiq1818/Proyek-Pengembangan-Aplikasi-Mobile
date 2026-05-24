package com.example.mybawanggacha.data.repository.jikan

import com.example.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import com.example.mybawanggacha.data.remote.jikan.dto.JikanRecommendationsResponse
import com.example.mybawanggacha.data.remote.jikan.dto.RelationEntryPreviewDto
import kotlinx.serialization.json.Json

internal object JikanResponseCacheCodec {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encodeAnimeList(response: JikanAnimeListResponse): String {
        return json.encodeToString(JikanAnimeListResponse.serializer(), response)
    }

    fun decodeAnimeList(value: String): JikanAnimeListResponse {
        return json.decodeFromString(JikanAnimeListResponse.serializer(), value)
    }

    fun encodeRecommendations(response: JikanRecommendationsResponse): String {
        return json.encodeToString(JikanRecommendationsResponse.serializer(), response)
    }

    fun decodeRecommendations(value: String): JikanRecommendationsResponse {
        return json.decodeFromString(JikanRecommendationsResponse.serializer(), value)
    }

    fun encodeRelationPreview(preview: RelationEntryPreviewDto): String {
        return json.encodeToString(RelationEntryPreviewDto.serializer(), preview)
    }

    fun decodeRelationPreview(value: String): RelationEntryPreviewDto {
        return json.decodeFromString(RelationEntryPreviewDto.serializer(), value)
    }
}
