package com.example.mybawanggacha.domain.manga.model

data class MangaSummary(
    val malId: Int,
    val title: String,
    val imageUrl: String?,
    val rank: Int? = null,
    val score: Double? = null,
    val type: String? = null
)

data class MangaPage(
    val items: List<MangaSummary>,
    val nextPage: Int?,
    val hasNextPage: Boolean
)

data class MangaDetail(
    val malId: Int,
    val url: String?,
    val imageUrl: String?,
    val title: String,
    val englishTitle: String?,
    val japaneseTitle: String?,
    val titleSynonyms: List<String>,
    val type: String?,
    val chapters: Int?,
    val volumes: Int?,
    val status: String?,
    val publishing: Boolean?,
    val published: String?,
    val score: Double?,
    val scoredBy: Int?,
    val rank: Int?,
    val popularity: Int?,
    val members: Int?,
    val favorites: Int?,
    val synopsis: String?,
    val background: String?,
    val authors: List<String>,
    val serializations: List<String>,
    val genres: List<String>,
    val explicitGenres: List<String>,
    val themes: List<String>,
    val demographics: List<String>
)
