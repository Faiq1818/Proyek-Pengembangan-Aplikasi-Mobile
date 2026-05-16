package com.example.mybawanggacha.presentation.screens.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mybawanggacha.domain.model.AnimeSummary
import com.example.mybawanggacha.presentation.components.AnimePosterCard
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnimeListScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit,
    viewModel: AnimeHomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.AnimeList,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.AnimeList -> Unit
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
            }
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, top = 32.dp, end = 18.dp)
        ) {
            Text(
                text = "Anime List",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Katalog rekomendasi anime dari Jikan. Status pribadi ada di My Library.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    AnimeHomeUiState.Loading -> LoadingIndicator()
                    is AnimeHomeUiState.Error -> ErrorState(
                        message = state.message,
                        onRetry = viewModel::refresh
                    )
                    is AnimeHomeUiState.Success -> AnimeGrid(
                        anime = state.recommendations,
                        onAnimeClick = onNavigateToAnimeDetail
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeGrid(
    anime: List<AnimeSummary>,
    onAnimeClick: (Int) -> Unit
) {
    if (anime.isEmpty()) {
        EmptyState(
            title = "Belum ada rekomendasi",
            message = "Jikan belum memberikan data anime. Coba refresh nanti."
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = anime,
            key = { it.malId }
        ) { item ->
            AnimePosterCard(
                title = item.title,
                imageUrl = item.imageUrl.orEmpty(),
                onClick = { onAnimeClick(item.malId) }
            )
        }
    }
}
