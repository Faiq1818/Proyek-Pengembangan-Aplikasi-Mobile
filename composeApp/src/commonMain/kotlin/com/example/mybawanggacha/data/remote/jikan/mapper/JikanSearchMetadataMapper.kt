package com.example.mybawanggacha.data.remote.jikan.mapper

import com.example.mybawanggacha.data.remote.jikan.dto.JikanFilterOptionDto
import com.example.mybawanggacha.data.remote.jikan.dto.JikanFilterOptionResponse
import com.example.mybawanggacha.domain.search.model.SearchFilterOption

internal fun JikanFilterOptionResponse.toSearchFilterOptions(): List<SearchFilterOption> {
    return data
        .map { option -> option.toSearchFilterOption() }
        .distinctBy { option -> option.id }
        .sortedWith(
            compareBy<SearchFilterOption> { option -> option.name.lowercase() }
                .thenBy { option -> option.id }
        )
}

private fun JikanFilterOptionDto.toSearchFilterOption(): SearchFilterOption {
    return SearchFilterOption(
        id = mal_id,
        name = name
            ?: titles.firstOrNull { title -> title.type.equals("Default", ignoreCase = true) }?.title
            ?: titles.firstOrNull()?.title
            ?: "#$mal_id"
    )
}
