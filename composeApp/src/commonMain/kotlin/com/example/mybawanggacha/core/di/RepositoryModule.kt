package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.data.local.source.AnimeProgressLocalDataSource
import com.example.mybawanggacha.data.repository.AIRepositoryImpl
import com.example.mybawanggacha.data.repository.AnimeRepositoryImpl
import com.example.mybawanggacha.data.repository.LibraryRepositoryImpl
import com.example.mybawanggacha.data.repository.NoteRepositoryImpl
import com.example.mybawanggacha.domain.repository.AIRepository
import com.example.mybawanggacha.domain.repository.AnimeRepository
import com.example.mybawanggacha.domain.repository.LibraryRepository
import com.example.mybawanggacha.domain.repository.NoteRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AnimeProgressLocalDataSource)
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    singleOf(::AnimeRepositoryImpl) bind AnimeRepository::class
    singleOf(::LibraryRepositoryImpl) bind LibraryRepository::class
}
