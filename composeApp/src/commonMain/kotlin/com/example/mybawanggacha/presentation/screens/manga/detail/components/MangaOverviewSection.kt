package com.example.mybawanggacha.presentation.screens.manga.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mybawanggacha.domain.manga.model.MangaDetail

@Composable
internal fun MangaOverviewSection(manga: MangaDetail) {
    MangaDetailSectionColumn(title = manga.title) {
        manga.englishTitle
            ?.takeIf { it.isNotBlank() && it != manga.title }
            ?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

        manga.japaneseTitle
            ?.takeIf { it.isNotBlank() }
            ?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = manga.imageUrl,
                contentDescription = manga.title,
                modifier = Modifier
                    .width(124.dp)
                    .height(178.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MangaInfoLine("Score", manga.score?.toString() ?: "Unknown")
                MangaInfoLine("Rank", manga.rank?.let { "#$it" } ?: "Unknown")
                MangaInfoLine("Type", manga.type.orUnknown())
                MangaInfoLine("Chapters", manga.chapters.formatNumber())
                MangaInfoLine("Volumes", manga.volumes.formatNumber())
                MangaInfoLine("Status", manga.status.orUnknown())
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        MangaDetailTextBlockIfNotEmpty(
            title = "Synopsis",
            body = manga.synopsis.orEmpty()
        )

        val chips = listOf(
            "Genres" to manga.genres,
            "Themes" to manga.themes,
            "Demographics" to manga.demographics
        ).mapNotNull { (label, values) ->
            values.takeIf { it.isNotEmpty() }?.joinToString()?.let { "$label: $it" }
        }

        if (chips.isNotEmpty()) {
            MangaDetailTextBlock(
                title = "Tags",
                body = chips.joinToString(separator = "\n")
            )
        }
    }
}

@Composable
private fun MangaInfoLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}
