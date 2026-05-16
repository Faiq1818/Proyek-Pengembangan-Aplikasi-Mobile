package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.core.network.HttpClientFactory
import com.example.mybawanggacha.data.remote.api.GeminiService
import com.example.mybawanggacha.data.remote.api.JikanService
import com.example.mybawanggacha.data.remote.source.JikanAnimeRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
    singleOf(::JikanService)
    singleOf(::JikanAnimeRemoteDataSource)
}
