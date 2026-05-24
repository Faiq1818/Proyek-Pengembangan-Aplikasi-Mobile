package com.example.mybawanggacha.data.repository.manga

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.remote.jikan.mapper.toDomain
import com.example.mybawanggacha.data.remote.jikan.mapper.toMangaDomainPage
import com.example.mybawanggacha.data.remote.jikan.source.JikanMangaRemoteDataSource
import com.example.mybawanggacha.domain.manga.model.MangaDetail
import com.example.mybawanggacha.domain.manga.model.MangaPage
import com.example.mybawanggacha.domain.manga.model.MangaSummary
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.withContext

class MangaRepositoryImpl(
    private val remoteDataSource: JikanMangaRemoteDataSource,
    private val dispatchers: AppDispatchers
) : MangaRepository {

    override suspend fun getTopMangaPage(page: Int): MangaPage = withContext(dispatchers.default) {
        remoteDataSource.fetchTopManga(page = page, type = "manga")
            .toMangaDomainPage(requestedPage = page)
    }

    override suspend fun getPopularMangaPage(page: Int): MangaPage = withContext(dispatchers.default) {
        remoteDataSource.fetchTopManga(page = page, filter = "bypopularity")
            .toMangaDomainPage(requestedPage = page)
    }

    override suspend fun getRecommendations(): List<MangaSummary> = withContext(dispatchers.default) {
        remoteDataSource.fetchMangaRecommendations()
            .data
            .flatMap { it.entry }
            .distinctBy { it.mal_id }
            .map { entry ->
                MangaSummary(
                    malId = entry.mal_id,
                    title = entry.title,
                    imageUrl = entry.images.jpg.large_image_url
                        ?: entry.images.jpg.image_url
                )
            }
    }

    override suspend fun getMangaDetail(malId: Int): MangaDetail = withContext(dispatchers.default) {
        remoteDataSource.fetchMangaFullDetail(malId).data.toDomain()
    }
}
