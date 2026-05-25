package com.example.mybawanggacha.domain.search.repository

import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.MediaSearchPage
import com.example.mybawanggacha.domain.search.model.SearchFilterMetadata
import com.example.mybawanggacha.domain.search.model.SearchMediaType

interface SearchRepository {
    suspend fun search(
        filters: MediaSearchFilters,
        page: Int
    ): MediaSearchPage

    suspend fun getFilterMetadata(mediaType: SearchMediaType): SearchFilterMetadata
}
