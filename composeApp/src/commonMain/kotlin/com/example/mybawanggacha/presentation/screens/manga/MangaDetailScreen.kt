package com.example.mybawanggacha.presentation.screens.manga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mybawanggacha.domain.manga.model.MangaDetail
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MangaDetailScreen(
    malId: Int,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    viewModel: MangaDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(malId) {
        viewModel.fetchMangaDetail(malId)
    }

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.MangaList,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> Unit
            }
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        when (val state = uiState) {
            MangaDetailUiState.Loading -> LoadingIndicator()
            is MangaDetailUiState.Error -> ErrorState(
                message = state.message,
                onRetry = { viewModel.fetchMangaDetail(malId) }
            )
            is MangaDetailUiState.Success -> MangaDetailContent(manga = state.manga)
        }
    }
}

@Composable
private fun MangaDetailContent(manga: MangaDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 8.dp, top = 32.dp, end = 20.dp, bottom = 32.dp)
    ) {
        Text(
            text = manga.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        manga.englishTitle
            ?.takeIf { it.isNotBlank() && it != manga.title }
            ?.let { title ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }

        manga.japaneseTitle
            ?.takeIf { it.isNotBlank() }
            ?.let { title ->
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        Spacer(modifier = Modifier.height(20.dp))

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
                MangaInfoText("Score", manga.score?.toString() ?: "-")
                MangaInfoText("Rank", manga.rank?.let { "#$it" } ?: "-")
                MangaInfoText("Type", manga.type.orUnknown())
                MangaInfoText("Chapters", manga.chapters.formatNumber())
                MangaInfoText("Volumes", manga.volumes.formatNumber())
                MangaInfoText("Status", manga.status.orUnknown())
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        MangaDetailSection("Synopsis", manga.synopsis)
        MangaDetailSection("Background", manga.background)
        MangaDetailSection("Authors", manga.authors.joinToString().takeIf { it.isNotBlank() })
        MangaDetailSection("Serializations", manga.serializations.joinToString().takeIf { it.isNotBlank() })
        MangaDetailSection("Genres", manga.genres.joinToString().takeIf { it.isNotBlank() })
        MangaDetailSection("Themes", manga.themes.joinToString().takeIf { it.isNotBlank() })
    }
}

@Composable
private fun MangaInfoText(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun MangaDetailSection(title: String, body: String?) {
    val visibleBody = body?.takeIf { it.isNotBlank() } ?: return

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = visibleBody,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(18.dp))
}

private fun String?.orUnknown(): String = this?.takeIf { it.isNotBlank() } ?: "-"

private fun Int?.formatNumber(): String = this?.toString() ?: "-"
