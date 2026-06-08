package com.example.mybawanggacha.domain.settings.repository

import com.example.mybawanggacha.domain.settings.model.AiApiModel
import com.example.mybawanggacha.domain.settings.model.AiApiSettings
import com.example.mybawanggacha.domain.settings.model.AppColorScheme
import com.example.mybawanggacha.domain.settings.model.NetworkMode
import com.example.mybawanggacha.domain.settings.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import com.example.mybawanggacha.domain.settings.model.AiPersonality

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    val networkMode: Flow<NetworkMode>
    val appColorScheme: Flow<AppColorScheme>
    val aiApiSettings: Flow<AiApiSettings>

    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setNetworkMode(networkMode: NetworkMode)
    suspend fun setAppColorScheme(appColorScheme: AppColorScheme)
    suspend fun setAiApiModel(aiApiModel: AiApiModel)
    suspend fun setAiApiPersonality(aiPersonality: AiPersonality)
    suspend fun setAiApiToken(token: String)
}
