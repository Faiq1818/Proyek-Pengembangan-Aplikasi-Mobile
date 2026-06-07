package com.example.mybawanggacha.domain.settings.model

enum class AppColorScheme(
    val label: String,
    val description: String,
    val lightSwatches: List<String>,
    val darkSwatches: List<String>
) {
    CodeGeass(
        label = "Code Geass",
        description = "C.C green untuk light, Lelouch purple-black untuk dark.",
        lightSwatches = listOf("#30746B", "#D1EF9D", "#697FA6", "#B3A45C", "#DFE3EB", "#F2F7FD"),
        darkSwatches = listOf("#9489CC", "#5051BF", "#F4E4BC", "#AD2748", "#2A2D35", "#0F0F12")
    ),
    PakHabib(
        label = "Pak Habib",
        description = "Material baseline: ungu aman, netral, tidak neko-neko.",
        lightSwatches = listOf("#6750A4", "#EADDFF", "#625B71", "#7D5260", "#E7E0EC", "#FFFBFE"),
        darkSwatches = listOf("#D0BCFF", "#4F378B", "#CCC2DC", "#EFB8C8", "#49454F", "#1C1B1F")
    ),
    Gruvbox(
        label = "Gruvbox",
        description = "Retro warm palette: earthy, low contrast, terminal friendly.",
        lightSwatches = listOf("#FBF1C7", "#79740E", "#B57614", "#427B58", "#D5C4A1", "#3C3836"),
        darkSwatches = listOf("#282828", "#B8BB26", "#FABD2F", "#8EC07C", "#504945", "#EBDBB2")
    );

    fun swatches(darkTheme: Boolean): List<String> {
        return if (darkTheme) darkSwatches else lightSwatches
    }

    companion object {
        fun fromString(value: String?): AppColorScheme {
            return entries.firstOrNull { scheme ->
                scheme.name.equals(value, ignoreCase = true)
            } ?: CodeGeass
        }
    }
}
