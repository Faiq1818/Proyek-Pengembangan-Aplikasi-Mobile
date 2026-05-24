package com.example.mybawanggacha

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mybawanggacha.data.local.datastore.UserPreferences
import com.example.mybawanggacha.presentation.navigation.AppNavHost
import com.example.mybawanggacha.presentation.theme.MBGTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(
    onDarkThemeChange: (Boolean) -> Unit = {}
) {
    KoinContext {
        val userPreferences = koinInject<UserPreferences>()
        val systemDarkTheme = isSystemInDarkTheme()
        val darkModePreference by userPreferences.isDarkMode.collectAsState(initial = null)
        val isDarkMode = darkModePreference ?: systemDarkTheme

        LaunchedEffect(isDarkMode) {
            onDarkThemeChange(isDarkMode)
        }

        MBGTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
