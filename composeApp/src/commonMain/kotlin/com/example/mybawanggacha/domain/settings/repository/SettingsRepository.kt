package com.example.mybawanggacha.domain.settings.repository

import com.example.mybawanggacha.domain.settings.model.AppColorScheme
import com.example.mybawanggacha.domain.settings.model.NetworkMode
import com.example.mybawanggacha.domain.settings.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    val networkMode: Flow<NetworkMode>
    val appColorScheme: Flow<AppColorScheme>

    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setNetworkMode(networkMode: NetworkMode)
    suspend fun setAppColorScheme(appColorScheme: AppColorScheme)
}
