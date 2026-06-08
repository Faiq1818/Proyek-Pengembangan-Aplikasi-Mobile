package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.data.local.source.AnimeProgressLocalDataSource
import com.example.mybawanggacha.data.local.source.MangaDetailCacheLocalDataSource
import com.example.mybawanggacha.data.local.source.AnimeDetailCacheLocalDataSource
import com.example.mybawanggacha.data.local.source.MediaPageCacheLocalDataSource
import com.example.mybawanggacha.data.local.source.RelationPreviewCacheLocalDataSource
import com.example.mybawanggacha.data.repository.ai.AIRepositoryImpl
import com.example.mybawanggacha.data.repository.gacha.GachaRepositoryImpl
import com.example.mybawanggacha.data.repository.anime.AnimeRepositoryImpl
import com.example.mybawanggacha.data.repository.jikan.JikanCachePolicy
import com.example.mybawanggacha.data.repository.jikan.JikanRequestUsageRepositoryImpl
import com.example.mybawanggacha.data.repository.jikan.SettingsJikanCachePolicy
import com.example.mybawanggacha.data.repository.library.LibraryRepositoryImpl
import com.example.mybawanggacha.data.repository.manga.MangaRepositoryImpl
import com.example.mybawanggacha.data.repository.note.NoteRepositoryImpl
import com.example.mybawanggacha.data.repository.search.SearchRepositoryImpl
import com.example.mybawanggacha.data.repository.settings.SettingsRepositoryImpl
import com.example.mybawanggacha.domain.ai.repository.AIRepository
import com.example.mybawanggacha.domain.gacha.repository.GachaRepository
import com.example.mybawanggacha.domain.anime.repository.AnimeRepository
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import com.example.mybawanggacha.domain.note.repository.NoteRepository
import com.example.mybawanggacha.domain.search.repository.SearchRepository
import com.example.mybawanggacha.domain.settings.repository.JikanRequestUsageRepository
import com.example.mybawanggacha.domain.settings.repository.SettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.example.mybawanggacha.data.repository.ai.AiChatSessionRepositoryImpl
import com.example.mybawanggacha.domain.ai.repository.AiChatSessionRepository

val repositoryModule = module {
    singleOf(::AnimeDetailCacheLocalDataSource)
    singleOf(::AnimeProgressLocalDataSource)
    singleOf(::MangaDetailCacheLocalDataSource)
    singleOf(::MediaPageCacheLocalDataSource)
    singleOf(::RelationPreviewCacheLocalDataSource)
    single<JikanCachePolicy> { SettingsJikanCachePolicy(get()) }
    singleOf(::JikanRequestUsageRepositoryImpl) bind JikanRequestUsageRepository::class
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
    singleOf(::AiChatSessionRepositoryImpl) bind AiChatSessionRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    singleOf(::GachaRepositoryImpl) bind GachaRepository::class
    singleOf(::AnimeRepositoryImpl) bind AnimeRepository::class
    singleOf(::LibraryRepositoryImpl) bind LibraryRepository::class
    singleOf(::MangaRepositoryImpl) bind MangaRepository::class
    single<SearchRepository> {
        SearchRepositoryImpl(
            remoteDataSource = get(),
            dispatchers = get(),
            mediaPageCacheLocalDataSource = get(),
            cachePolicy = get()
        )
    }
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
}
