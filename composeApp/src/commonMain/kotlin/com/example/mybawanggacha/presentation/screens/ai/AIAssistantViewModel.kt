package com.example.mybawanggacha.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.ai.repository.AIRepository
import com.example.mybawanggacha.domain.ai.repository.ChatMessage
import com.example.mybawanggacha.domain.ai.repository.MessageSender
import com.example.mybawanggacha.domain.ai.repository.WritingStyle
import com.example.mybawanggacha.domain.note.usecase.GenerateIdeasUseCase
import com.example.mybawanggacha.domain.note.usecase.ImproveWritingUseCase
import com.example.mybawanggacha.domain.note.usecase.SummarizeNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.mybawanggacha.domain.settings.model.AiApiModel
import com.example.mybawanggacha.domain.settings.repository.SettingsRepository
import com.example.mybawanggacha.domain.settings.model.AiPersonality
import com.example.mybawanggacha.domain.ai.repository.AiChatSessionRepository

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val summarizeUseCase: SummarizeNoteUseCase,
    private val improveWritingUseCase: ImproveWritingUseCase,
    private val generateIdeasUseCase: GenerateIdeasUseCase,
    private val settingsRepository: SettingsRepository,
    private val chatSessionRepository: AiChatSessionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AIAssistantEvent>()
    val events: SharedFlow<AIAssistantEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            settingsRepository.aiApiSettings.collect { settings ->
                _uiState.update { state ->
                    state.copy(
                        aiApiModel = settings.model,
                        aiPersonality = settings.personality
                    )
                }
            }
        }
    }

    
    fun setInitialText(text: String?) {
        text?.let {
            _uiState.update { state -> state.copy(inputText = it) }
        }
    }
    
    fun configureSession(
        noteId: Long?,
        mediaId: Int?,
        mediaType: String?,
        mediaTitle: String?,
        context: String?
    ) {
        val sessionKey = buildSessionKey(
            noteId = noteId,
            mediaId = mediaId,
            mediaType = mediaType
        )

        if (_uiState.value.sessionKey == sessionKey && _uiState.value.animeContext == context) {
            return
        }

        _uiState.update { state ->
            state.copy(
                sessionKey = sessionKey,
                sessionTitle = mediaTitle,
                sessionMediaType = mediaType,
                animeContext = context,
                isLoadingSession = true,
                error = null
            )
        }

        viewModelScope.launch {
            val messages = chatSessionRepository.getMessages(sessionKey)
            _uiState.update { state ->
                state.copy(
                    chatHistory = messages,
                    isLoadingSession = false
                )
            }
        }
    }
    
    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }
    
    fun onActionSelected(action: AIAction) {
        _uiState.update { it.copy(selectedAction = action) }
    }
    
    fun executeAction() {
        val state = _uiState.value
        val messageText = state.inputText.trim()
        
        if (messageText.isBlank()) {
            _uiState.update { it.copy(error = "Masukkan teks terlebih dahulu") }
            return
        }
        
        val userMessage = ChatMessage(sender = MessageSender.USER, text = messageText)
        val updatedHistory = state.chatHistory + userMessage
        _uiState.update { 
            it.copy(
                inputText = "",
                isLoading = true,
                error = null,
                chatHistory = updatedHistory
            ) 
        }
        
        viewModelScope.launch {
            chatSessionRepository.appendMessage(state.sessionKey, userMessage)

            val contextPrompt = buildScreenContextPrompt(state.animeContext)
            val result = chat(updatedHistory, contextPrompt)
            
            result
                .onSuccess { output ->
                    val aiMessage = ChatMessage(sender = MessageSender.AI, text = output)
                    chatSessionRepository.appendMessage(state.sessionKey, aiMessage)
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            chatHistory = it.chatHistory + aiMessage
                        ) 
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Terjadi kesalahan"
                        ) 
                    }
                }
        }
    }
    
    fun resetSession() {
        val sessionKey = _uiState.value.sessionKey
        viewModelScope.launch {
            chatSessionRepository.clearSession(sessionKey)
            _uiState.update {
                it.copy(
                    chatHistory = emptyList(),
                    error = null,
                    isLoading = false
                )
            }
        }
    }

    fun copyResult(text: String) {
        viewModelScope.launch {
            _events.emit(AIAssistantEvent.CopyToClipboard(text))
        }
    }
    
    fun applyToNote(text: String) {
        viewModelScope.launch {
            _events.emit(AIAssistantEvent.ApplyToNote(text))
        }
    }
    
    fun onWritingStyleChange(style: WritingStyle) {
        _uiState.update { it.copy(writingStyle = style) }
    }
    
    fun onTargetLanguageChange(language: String) {
        _uiState.update { it.copy(targetLanguage = language) }
    }

    fun setAiApiModel(aiApiModel: AiApiModel) {
        viewModelScope.launch {
            settingsRepository.setAiApiModel(aiApiModel)
        }
    }

    private fun buildSessionKey(
        noteId: Long?,
        mediaId: Int?,
        mediaType: String?
    ): String {
        return when {
            mediaId != null && !mediaType.isNullOrBlank() -> {
                "${mediaType.uppercase()}:$mediaId"
            }
            noteId != null -> "NOTE:$noteId"
            else -> "GLOBAL"
        }
    }

    private fun buildScreenContextPrompt(animeContext: String?): String? {
        return animeContext
            ?.takeIf { it.isNotBlank() }
            ?.let { context ->
                """
                    Pengguna sedang membuka konteks anime/manga di aplikasi:
                    $context

                    Gunakan konteks ini hanya jika relevan dengan pertanyaan pengguna.
                    Jika data konteks tidak memuat informasi yang ditanyakan, jelaskan bahwa data tersebut tidak tersedia di layar ini.
                """.trimIndent()
            }
    }
    
    // ==================== AI OPERATIONS ====================
    
    private suspend fun summarize(text: String): Result<String> {
        return summarizeUseCase(text)
    }
    
    private suspend fun generateIdeas(topic: String): Result<String> {
        return generateIdeasUseCase(topic).map { ideas ->
            ideas.mapIndexed { index, idea -> "${index + 1}. $idea" }.joinToString("\n")
        }
    }
    
    private suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        return improveWritingUseCase(text, style)
    }
    
    private suspend fun translate(text: String, targetLanguage: String): Result<String> {
        return aiRepository.translate(text, targetLanguage)
    }
    
    private suspend fun suggestTitle(content: String): Result<String> {
        return aiRepository.suggestTitle(content)
    }
    
    private suspend fun chat(history: List<ChatMessage>, systemPrompt: String? = null): Result<String> {
        return aiRepository.chat(history, systemPrompt)
    }
}

enum class AIAction(val displayName: String, val description: String) {
    SUMMARIZE("Ringkas", "Buat ringkasan dari teks"),
    GENERATE_IDEAS("Ide", "Generate ide berdasarkan topik"),
    IMPROVE_WRITING("Perbaiki", "Perbaiki tulisan"),
    TRANSLATE("Terjemah", "Terjemahkan ke bahasa lain"),
    SUGGEST_TITLE("Judul", "Sarankan judul"),
    CHAT("Tanya", "Tanya AI tentang apapun")
}


data class AIAssistantUiState(
    val inputText: String = "",
    val selectedAction: AIAction = AIAction.CHAT,
    val writingStyle: WritingStyle = WritingStyle.NEUTRAL,
    val targetLanguage: String = "English",
    val aiApiModel: AiApiModel = AiApiModel.Gemini35Flash,
    val aiPersonality: AiPersonality = AiPersonality.Default,
    val isLoading: Boolean = false,
    val isLoadingSession: Boolean = false,
    val sessionKey: String = "GLOBAL",
    val sessionTitle: String? = null,
    val sessionMediaType: String? = null,
    val result: String? = null,
    val error: String? = null,
    val chatHistory: List<ChatMessage> = emptyList(),
    val animeContext: String? = null
) {
    val canExecute: Boolean
        get() = inputText.isNotBlank() && !isLoading
}

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToNote(val text: String) : AIAssistantEvent
}
