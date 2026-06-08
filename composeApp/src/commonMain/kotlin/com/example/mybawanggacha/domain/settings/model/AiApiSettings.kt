package com.example.mybawanggacha.domain.settings.model

enum class AiApiModel(
    val label: String,
    val modelId: String
) {
    Gemini35Flash("Gemini 3.5 Flash", "gemini-3.5-flash"),
    Gemini25Flash("Gemini 2.5 Flash", "gemini-2.5-flash"),
    Gemini25Pro("Gemini 2.5 Pro", "gemini-2.5-pro"),
    Gemini20Flash("Gemini 2.0 Flash", "gemini-2.0-flash");

    companion object {
        fun fromString(value: String?): AiApiModel {
            return entries.firstOrNull { model ->
                model.name.equals(value, ignoreCase = true) ||
                    model.modelId.equals(value, ignoreCase = true)
            } ?: Gemini35Flash
        }
    }
}

data class AiApiSettings(
    val model: AiApiModel = AiApiModel.Gemini35Flash,
    val token: String = ""
) {
    val hasToken: Boolean
        get() = token.isNotBlank()
}
