package id.my.sinanonym.mybawanggacha.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiTokenUsageSnapshot
import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanRequestUsage
import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanServiceStatus
import id.my.sinanonym.mybawanggacha.domain.settings.model.JikanServiceStatusState
import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode
import id.my.sinanonym.mybawanggacha.domain.settings.repository.AiTokenUsageRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.JikanRequestUsageRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.JikanServiceStatusRepository
import id.my.sinanonym.mybawanggacha.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiPersonality

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val requestUsageRepository: JikanRequestUsageRepository,
    private val serviceStatusRepository: JikanServiceStatusRepository,
    private val aiTokenUsageRepository: AiTokenUsageRepository
) : ViewModel() {

    private val baseUiState = combine(
        combine(
            settingsRepository.themeMode,
            settingsRepository.networkMode,
            settingsRepository.appColorScheme,
            settingsRepository.aiApiSettings,
            requestUsageRepository.usage
        ) { themeMode, networkMode, appColorScheme, aiApiSettings, requestUsage ->
            SettingsUiState(
                themeMode = themeMode,
                networkMode = networkMode,
                appColorScheme = appColorScheme,
                aiApiSettings = aiApiSettings,
                requestUsage = requestUsage.toUiState()
            )
        },
        serviceStatusRepository.status
    ) { base, serviceStatus ->
        base.copy(
            requestUsage = base.requestUsage.copy(
                serviceStatus = serviceStatus.toUiState()
            )
        )
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        baseUiState,
        aiTokenUsageRepository.usage
    ) { base, aiTokenUsage ->
        base.copy(aiTokenUsage = aiTokenUsage.toUiState())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }

    fun setNetworkMode(networkMode: NetworkMode) {
        viewModelScope.launch {
            settingsRepository.setNetworkMode(networkMode)
        }
    }

    fun setAppColorScheme(appColorScheme: AppColorScheme) {
        viewModelScope.launch {
            settingsRepository.setAppColorScheme(appColorScheme)
        }
    }

    fun setAiApiModel(aiApiModel: AiApiModel) {
        viewModelScope.launch {
            settingsRepository.setAiApiModel(aiApiModel)
        }
    }

    fun setAiApiPersonality(aiPersonality: AiPersonality) {
        viewModelScope.launch {
            settingsRepository.setAiApiPersonality(aiPersonality)
        }
    }

    fun setAiApiToken(token: String) {
        viewModelScope.launch {
            settingsRepository.setAiApiToken(token)
        }
    }

    fun resetAiTokenUsage() {
        viewModelScope.launch {
            aiTokenUsageRepository.resetUsage()
        }
    }

    private fun JikanRequestUsage.toUiState(): SettingsRequestUsageUiState {
        return SettingsRequestUsageUiState(
            usedLastSecond = usedLastSecond,
            secondLimit = secondLimit,
            usedLastMinute = usedLastMinute,
            minuteLimit = minuteLimit,
            remainingThisMinute = remainingThisMinute,
            msUntilNextRequest = msUntilNextRequest
        )
    }

    private fun JikanServiceStatus.toUiState(): SettingsJikanServiceStatusUiState {
        return SettingsJikanServiceStatusUiState(
            label = when (state) {
                JikanServiceStatusState.Checking -> "checking"
                JikanServiceStatusState.Active -> "active"
                JikanServiceStatusState.Down -> "down"
            },
            isActive = isActive,
            isChecking = state == JikanServiceStatusState.Checking,
            statusCode = statusCode,
            type = type,
            message = message
        )
    }
}


private fun AiTokenUsageSnapshot.toUiState(): SettingsAiTokenUsageUiState {
    return SettingsAiTokenUsageUiState(
        entries = entries.map { entry ->
            SettingsAiModelTokenUsageUiState(
                model = entry.model,
                label = entry.model.label,
                modelId = entry.model.modelId,
                requestCount = entry.requestCount,
                promptTokens = entry.promptTokens,
                candidatesTokens = entry.candidatesTokens,
                thoughtsTokens = entry.thoughtsTokens,
                cachedContentTokens = entry.cachedContentTokens,
                totalTokens = entry.totalTokens,
                lastPromptTokens = entry.lastPromptTokens,
                lastCandidatesTokens = entry.lastCandidatesTokens,
                lastThoughtsTokens = entry.lastThoughtsTokens,
                lastCachedContentTokens = entry.lastCachedContentTokens,
                lastTotalTokens = entry.lastTotalTokens,
                inputTokenLimit = entry.inputTokenLimit,
                outputTokenLimit = entry.outputTokenLimit,
                appOutputTokenLimit = entry.appOutputTokenLimit,
                effectiveOutputTokenLimit = entry.effectiveOutputTokenLimit,
                updatedAtMillis = entry.updatedAtMillis
            )
        }
    )
}
