package com.example.mybawanggacha.presentation.screens.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mybawanggacha.domain.search.model.SearchMediaType
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import com.example.mybawanggacha.presentation.components.PullRefreshContainer
import org.koin.compose.viewmodel.koinViewModel
import com.example.mybawanggacha.presentation.screens.search.components.SearchContent

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit,
    onNavigateToMangaDetail: (Int) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val filterMetadata by viewModel.filterMetadata.collectAsStateWithLifecycle()

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.Search,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.Search -> Unit
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.Gacha -> onNavigateToGacha()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
            }
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        PullRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize()
        ) {
            SearchContent(
                filters = filters,
                uiState = uiState,
                filterMetadata = filterMetadata,
                onFiltersChange = viewModel::updateFilters,
                onSearch = viewModel::submitSearch,
                onReset = viewModel::resetFilters,
                onRetry = viewModel::submitSearch,
                onLoadMore = viewModel::loadNextPage,
                onItemClick = { item ->
                    when (item.mediaType) {
                        SearchMediaType.Anime -> onNavigateToAnimeDetail(item.malId)
                        SearchMediaType.Manga -> onNavigateToMangaDetail(item.malId)
                    }
                }
            )
        }
    }
}