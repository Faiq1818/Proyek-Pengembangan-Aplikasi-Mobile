package com.example.mybawanggacha.data.remote.gemini.api

import com.example.mybawanggacha.core.network.ApiConfig
import com.example.mybawanggacha.data.local.datastore.UserPreferences
import com.example.mybawanggacha.data.remote.gemini.dto.GeminiContent
import com.example.mybawanggacha.data.remote.gemini.dto.GeminiPart
import com.example.mybawanggacha.data.remote.gemini.dto.GeminiRequest
import com.example.mybawanggacha.data.remote.gemini.dto.GeminiResponse
import com.example.mybawanggacha.data.remote.gemini.dto.GenerationConfig
import com.example.mybawanggacha.data.remote.gemini.dto.getErrorMessage
import com.example.mybawanggacha.data.remote.gemini.dto.getTextContent
import com.example.mybawanggacha.domain.settings.model.AiApiModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first

class GeminiService(
    private val client: HttpClient,
    private val userPreferences: UserPreferences
) {

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    }

    suspend fun generateContent(
        contents: List<GeminiContent>
    ): Result<String> = runCatching {
        val request = GeminiRequest(
            contents = contents,
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 8192
            )
        )

        val model = AiApiModel.fromString(userPreferences.aiApiModel.first())
        val apiKey = userPreferences.aiApiToken
            .first()
            .trim()
            .ifBlank { ApiConfig.geminiApiKey.trim() }

        if (apiKey.isBlank()) {
            throw IllegalStateException("AI API token belum diatur di Settings.")
        }

        val response: GeminiResponse = client.post("$BASE_URL/models/${model.modelId}:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", apiKey)
            setBody(request)
        }.body()

        response.getErrorMessage()?.let { errorMsg ->
            throw Exception(errorMsg)
        }

        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> {
        val contents = mutableListOf<GeminiContent>()

        if (systemPrompt != null) {
            contents.add(
                GeminiContent(
                    parts = listOf(GeminiPart(text = systemPrompt)),
                    role = "user"
                )
            )
            contents.add(
                GeminiContent(
                    parts = listOf(GeminiPart(text = "Baik, saya akan mengikuti instruksi tersebut.")),
                    role = "model"
                )
            )
        }

        contents.add(
            GeminiContent(
                parts = listOf(GeminiPart(text = prompt)),
                role = "user"
            )
        )

        return generateContent(contents)
    }
}

// ====================
// System Prompts
// ====================

object SystemPrompts {

    val SUMMARIZER = """
        Kamu adalah asisten yang ahli dalam merangkum teks.
        Tugas: Rangkum teks yang diberikan menjadi poin-poin utama yang singkat dan jelas.
        Rules:
        - Gunakan Bahasa Indonesia
        - Maksimal 3-5 poin utama
        - Setiap poin maksimal 1-2 kalimat
        - Fokus pada informasi paling penting
        - Jangan menambahkan informasi yang tidak ada di teks asli
    """.trimIndent()

    val IDEA_GENERATOR = """
        Kamu adalah asisten kreatif yang membantu mengembangkan ide.
        Tugas: Berikan 5 ide kreatif berdasarkan topik yang diberikan.
        Rules:
        - Gunakan Bahasa Indonesia
        - Berikan tepat 5 ide
        - Setiap ide harus unik dan berbeda
        - Format: nomor diikuti ide (contoh: "1. Ide pertama")
        - Ide harus praktis dan bisa diimplementasikan
    """.trimIndent()

    val WRITING_IMPROVER = """
        Kamu adalah editor profesional yang membantu memperbaiki tulisan.
        Tugas: Perbaiki tulisan yang diberikan tanpa mengubah makna aslinya.
        Rules:
        - Gunakan Bahasa Indonesia yang baik dan benar
        - Perbaiki grammar, ejaan, dan struktur kalimat
        - Pertahankan gaya dan tone asli penulis
        - Jangan menambahkan informasi baru
        - Berikan HANYA hasil tulisan yang sudah diperbaiki, tanpa penjelasan
    """.trimIndent()

    val TITLE_SUGGESTER = """
        Kamu adalah asisten yang membantu membuat judul menarik.
        Tugas: Berikan 1 saran judul yang singkat dan menarik berdasarkan konten yang diberikan.
        Rules:
        - Gunakan Bahasa Indonesia
        - Judul maksimal 5-7 kata
        - Judul harus mencerminkan isi konten
        - Berikan HANYA judul, tanpa penjelasan atau tanda kutip
    """.trimIndent()

    val TRANSLATOR = """
        Kamu adalah penerjemah profesional.
        Tugas: Terjemahkan teks yang diberikan ke bahasa target.
        Rules:
        - Pertahankan makna dan nuansa asli
        - Gunakan bahasa yang natural, bukan literal
        - Berikan HANYA hasil terjemahan, tanpa penjelasan
    """.trimIndent()
}
