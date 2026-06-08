package com.example.mybawanggacha.data.repository.ai

import com.example.mybawanggacha.data.remote.gemini.api.GeminiService
import com.example.mybawanggacha.data.remote.gemini.api.SystemPrompts
import com.example.mybawanggacha.data.remote.gemini.dto.GeminiContent
import com.example.mybawanggacha.data.remote.gemini.dto.GeminiPart
import com.example.mybawanggacha.domain.ai.repository.AIRepository
import com.example.mybawanggacha.domain.ai.repository.ChatMessage
import com.example.mybawanggacha.domain.ai.repository.MessageSender
import com.example.mybawanggacha.domain.ai.repository.WritingStyle
import com.example.mybawanggacha.domain.settings.model.AiPersonality
import com.example.mybawanggacha.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class AIRepositoryImpl(
    private val geminiService: GeminiService,
    private val settingsRepository: SettingsRepository
) : AIRepository {

    override suspend fun summarize(text: String): Result<String> {
        val prompt = """
            Rangkum teks berikut:
            
            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = systemPrompt(SystemPrompts.SUMMARIZER)
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        val prompt = """
            Berikan 5 ide kreatif untuk topik: $topic
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = systemPrompt(SystemPrompts.IDEA_GENERATOR)
        ).map { response ->
            response.lines()
                .filter { it.isNotBlank() }
                .map { line ->
                    line.replace(Regex("^\\d+\\.\\s*"), "").trim()
                }
                .filter { it.isNotBlank() }
        }
    }

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        val styleInstruction = when (style) {
            WritingStyle.FORMAL -> "Gunakan gaya formal dan profesional."
            WritingStyle.CASUAL -> "Gunakan gaya santai dan friendly."
            WritingStyle.ACADEMIC -> "Gunakan gaya akademik dan ilmiah."
            WritingStyle.CREATIVE -> "Gunakan gaya kreatif dan menarik."
            WritingStyle.NEUTRAL -> "Gunakan gaya netral."
        }

        val prompt = """
            $styleInstruction
            
            Perbaiki tulisan berikut:
            
            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = systemPrompt(SystemPrompts.WRITING_IMPROVER)
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = """
            Terjemahkan ke bahasa $targetLanguage:
            
            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = systemPrompt(SystemPrompts.TRANSLATOR)
        )
    }

    override suspend fun chat(history: List<ChatMessage>, systemPrompt: String?): Result<String> {
        val contents = mutableListOf<GeminiContent>()
        val effectiveSystemPrompt = buildString {
            append(systemPrompt(SystemPrompts.APP_ASSISTANT))
            systemPrompt
                ?.takeIf { it.isNotBlank() }
                ?.let { prompt ->
                    append("\n\nKonteks layar saat ini:\n")
                    append(prompt.trim())
                }
        }

        contents.add(
            GeminiContent(
                parts = listOf(GeminiPart(text = effectiveSystemPrompt)),
                role = "user"
            )
        )
        contents.add(
            GeminiContent(
                parts = listOf(GeminiPart(text = "Siap. Saya akan membantu sebagai asisten MyBawangGacha dan memakai Markdown bila berguna.")),
                role = "model"
            )
        )

        history.forEach { message ->
            contents.add(
                GeminiContent(
                    parts = listOf(GeminiPart(text = message.text)),
                    role = if (message.sender == MessageSender.USER) "user" else "model"
                )
            )
        }

        return geminiService.generateContent(contents)
    }

    private suspend fun systemPrompt(basePrompt: String): String {
        val personality = settingsRepository.aiApiSettings.first().personality
        return buildString {
            append(basePrompt)
            append("\n\nGaya/personality aktif: ")
            append(personality.label)
            append("\n")
            append(personality.prompt)
            append("\nTetap prioritaskan instruksi tugas utama di atas personality.")
        }
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        val prompt = """
            Berikan saran judul untuk konten berikut:
            
            $content
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = systemPrompt(SystemPrompts.TITLE_SUGGESTER)
        ).map { it.trim().removeSurrounding("\"") }
    }
}