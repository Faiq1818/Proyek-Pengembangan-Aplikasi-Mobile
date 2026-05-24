package com.example.mybawanggacha.presentation.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.MediaSearchItem
import com.example.mybawanggacha.domain.search.model.SearchMediaType
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import com.example.mybawanggacha.presentation.components.PullRefreshContainer
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit,
    onNavigateToMangaDetail: (Int) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.Search,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.Search -> Unit
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
            }
        },
        topAction = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
    filters: MediaSearchFilters,
    uiState: SearchUiState,
    onFiltersChange: (MediaSearchFilters) -> Unit,
    onSearch: () -> Unit,
    onReset: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onItemClick: (MediaSearchItem) -> Unit
) {
    var showFilters by remember { mutableStateOf(false) }
    val activeLabels = remember(filters) { buildActiveFilterLabels(filters) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    val latestUiState by rememberUpdatedState(uiState)
    val latestOnLoadMore by rememberUpdatedState(onLoadMore)
    var loadMoreArmed by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 && lastVisibleIndex >= totalItems - 4
        }
            .distinctUntilChanged()
            .collect { isNearEnd ->
                if (!isNearEnd) {
                    loadMoreArmed = true
                    return@collect
                }

                val state = latestUiState
                if (
                    loadMoreArmed &&
                    state is SearchUiState.Success &&
                    state.canLoadMore &&
                    !state.isLoadingMore
                ) {
                    loadMoreArmed = false
                    latestOnLoadMore()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 4.dp, top = 32.dp, end = 18.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SearchHeader()
        }

        item {
            SearchCompactPanel(
                filters = filters,
                activeFilterCount = activeLabels.size,
                onFiltersChange = onFiltersChange,
                onOpenFilters = { showFilters = true },
                onSearch = onSearch
            )
        }

        if (activeLabels.isNotEmpty()) {
            item {
                ActiveFilterRow(
                    labels = activeLabels,
                    onReset = onReset
                )
            }
        }

        when (uiState) {
            SearchUiState.Idle -> item {
                EmptyState(
                    title = "Belum mencari",
                    message = "Isi query atau filter, lalu tekan Cari.",
                    modifier = Modifier.height(360.dp)
                )
            }

            SearchUiState.Loading -> item {
                LoadingIndicator(modifier = Modifier.height(360.dp))
            }

            is SearchUiState.Error -> item {
                ErrorState(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.height(360.dp)
                )
            }

            is SearchUiState.Success -> {
                if (uiState.items.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Tidak ada hasil",
                            message = "Coba longgarkan filter atau ubah kata kunci.",
                            modifier = Modifier.height(360.dp)
                        )
                    }
                } else {
                    items(
                        items = uiState.items,
                        key = { item -> "${item.mediaType}:${item.malId}" }
                    ) { item ->
                        SearchResultCard(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                    }

                    if (uiState.isLoadingMore) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Loading more...",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilters) {
        var draft by remember(showFilters) { mutableStateOf(filters) }

        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = sheetState
        ) {
            SearchFilterSheet(
                filters = draft,
                onFiltersChange = { draft = it },
                onReset = { draft = MediaSearchFilters(mediaType = draft.mediaType) },
                onApply = {
                    onFiltersChange(draft)
                    showFilters = false
                },
                onApplyAndSearch = {
                    onFiltersChange(draft)
                    showFilters = false
                    onSearch()
                }
            )
        }
    }
}

@Composable
private fun SearchHeader() {
    Column {
        Text(
            text = "Search",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Cari anime atau manga",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SearchCompactPanel(
    filters: MediaSearchFilters,
    activeFilterCount: Int,
    onFiltersChange: (MediaSearchFilters) -> Unit,
    onOpenFilters: () -> Unit,
    onSearch: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchMediaTypeDropdown(
                selected = filters.mediaType,
                onSelected = { mediaType ->
                    onFiltersChange(
                        filters.copy(
                            mediaType = mediaType,
                            type = null,
                            status = null,
                            rating = null,
                            orderBy = null,
                            sort = null
                        )
                    )
                }
            )

            OutlinedTextField(
                value = filters.query,
                onValueChange = { value -> onFiltersChange(filters.copy(query = value)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Query") },
                placeholder = { Text("Frieren, One Piece, Naruto...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    QuickFilterChip(
                        label = "SFW",
                        selected = filters.sfw,
                        onClick = { onFiltersChange(filters.copy(sfw = !filters.sfw)) }
                    )
                }
                item {
                    SearchDropdownChip(
                        label = "Type",
                        value = filters.type,
                        options = if (filters.mediaType == SearchMediaType.Anime) ANIME_TYPES else MANGA_TYPES,
                        onSelected = { value -> onFiltersChange(filters.copy(type = value)) }
                    )
                }
                item {
                    SearchDropdownChip(
                        label = "Status",
                        value = filters.status,
                        options = if (filters.mediaType == SearchMediaType.Anime) ANIME_STATUSES else MANGA_STATUSES,
                        onSelected = { value -> onFiltersChange(filters.copy(status = value)) }
                    )
                }
                item {
                    SearchDropdownChip(
                        label = "Sort",
                        value = filters.sort,
                        options = SORT_OPTIONS,
                        onSelected = { value -> onFiltersChange(filters.copy(sort = value)) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onOpenFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (activeFilterCount > 0) {
                            "Filter ($activeFilterCount)"
                        } else {
                            "Filter"
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Button(
                    onClick = onSearch,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cari")
                }
            }
        }
    }
}

@Composable
private fun ActiveFilterRow(
    labels: List<String>,
    onReset: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter aktif",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onReset) {
                Text("Reset")
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(labels) { label ->
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchFilterSheet(
    filters: MediaSearchFilters,
    onFiltersChange: (MediaSearchFilters) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    onApplyAndSearch: () -> Unit
) {
    var generalExpanded by remember { mutableStateOf(true) }
    var classificationExpanded by remember { mutableStateOf(true) }
    var scoreExpanded by remember { mutableStateOf(false) }
    var metadataExpanded by remember { mutableStateOf(false) }
    var sortingExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Advanced Filters",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Filter lengkap tetap ada, tapi disimpan di overlay agar screen utama tetap bersih.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            FilterSection(
                title = "General",
                subtitle = "limit, letter, SFW, unapproved",
                expanded = generalExpanded,
                onToggle = { generalExpanded = !generalExpanded }
            ) {
                SmallSearchTextField(
                    label = "Limit",
                    value = filters.limit,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(limit = value)) }
                )
                SmallSearchTextField(
                    label = "Letter",
                    value = filters.letter,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(letter = value)) }
                )
                ToggleRow(
                    label = "SFW only",
                    checked = filters.sfw,
                    onCheckedChange = { value -> onFiltersChange(filters.copy(sfw = value)) }
                )
                ToggleRow(
                    label = "Include unapproved entries",
                    checked = filters.unapproved,
                    onCheckedChange = { value -> onFiltersChange(filters.copy(unapproved = value)) }
                )
            }
        }

        item {
            FilterSection(
                title = "Classification",
                subtitle = "type, status, rating",
                expanded = classificationExpanded,
                onToggle = { classificationExpanded = !classificationExpanded }
            ) {
                SearchDropdown(
                    label = "Type",
                    value = filters.type,
                    options = if (filters.mediaType == SearchMediaType.Anime) ANIME_TYPES else MANGA_TYPES,
                    modifier = Modifier.fillMaxWidth(),
                    onSelected = { value -> onFiltersChange(filters.copy(type = value)) }
                )
                SearchDropdown(
                    label = "Status",
                    value = filters.status,
                    options = if (filters.mediaType == SearchMediaType.Anime) ANIME_STATUSES else MANGA_STATUSES,
                    modifier = Modifier.fillMaxWidth(),
                    onSelected = { value -> onFiltersChange(filters.copy(status = value)) }
                )
                if (filters.mediaType == SearchMediaType.Anime) {
                    SearchDropdown(
                        label = "Rating",
                        value = filters.rating,
                        options = ANIME_RATINGS,
                        modifier = Modifier.fillMaxWidth(),
                        onSelected = { value -> onFiltersChange(filters.copy(rating = value)) }
                    )
                }
            }
        }

        item {
            FilterSection(
                title = "Score & Date",
                subtitle = "score, min/max score, start/end date",
                expanded = scoreExpanded,
                onToggle = { scoreExpanded = !scoreExpanded }
            ) {
                SmallSearchTextField(
                    label = "Exact Score",
                    value = filters.score,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(score = value)) }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SmallSearchTextField(
                        label = "Min Score",
                        value = filters.minScore,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(minScore = value)) }
                    )
                    SmallSearchTextField(
                        label = "Max Score",
                        value = filters.maxScore,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(maxScore = value)) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SmallSearchTextField(
                        label = "Start Date",
                        value = filters.startDate,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(startDate = value)) }
                    )
                    SmallSearchTextField(
                        label = "End Date",
                        value = filters.endDate,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(endDate = value)) }
                    )
                }
            }
        }

        item {
            FilterSection(
                title = "Metadata",
                subtitle = if (filters.mediaType == SearchMediaType.Anime) {
                    "genres, excluded genres, producers"
                } else {
                    "genres, excluded genres, magazines"
                },
                expanded = metadataExpanded,
                onToggle = { metadataExpanded = !metadataExpanded }
            ) {
                SmallSearchTextField(
                    label = "Genre IDs",
                    value = filters.genres,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(genres = value)) }
                )
                SmallSearchTextField(
                    label = "Excluded Genre IDs",
                    value = filters.genresExclude,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(genresExclude = value)) }
                )
                SmallSearchTextField(
                    label = if (filters.mediaType == SearchMediaType.Anime) "Producer IDs" else "Magazine IDs",
                    value = if (filters.mediaType == SearchMediaType.Anime) filters.producers else filters.magazines,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value ->
                        if (filters.mediaType == SearchMediaType.Anime) {
                            onFiltersChange(filters.copy(producers = value))
                        } else {
                            onFiltersChange(filters.copy(magazines = value))
                        }
                    }
                )
            }
        }

        item {
            FilterSection(
                title = "Sorting",
                subtitle = "order by and direction",
                expanded = sortingExpanded,
                onToggle = { sortingExpanded = !sortingExpanded }
            ) {
                SearchDropdown(
                    label = "Order By",
                    value = filters.orderBy,
                    options = if (filters.mediaType == SearchMediaType.Anime) ANIME_ORDER_BY else MANGA_ORDER_BY,
                    modifier = Modifier.fillMaxWidth(),
                    onSelected = { value -> onFiltersChange(filters.copy(orderBy = value)) }
                )
                SearchDropdown(
                    label = "Sort",
                    value = filters.sort,
                    options = SORT_OPTIONS,
                    modifier = Modifier.fillMaxWidth(),
                    onSelected = { value -> onFiltersChange(filters.copy(sort = value)) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
                OutlinedButton(
                    onClick = onApply,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Terapkan")
                }
                Button(
                    onClick = onApplyAndSearch,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cari")
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    subtitle: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
                    )
                    content()
                }
            }
        }
    }
}

@Composable
private fun SearchMediaTypeDropdown(
    selected: SearchMediaType,
    onSelected: (SearchMediaType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Media: ${selected.label}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SearchMediaType.entries.forEach { mediaType ->
                DropdownMenuItem(
                    text = { Text(mediaType.label) },
                    onClick = {
                        onSelected(mediaType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchDropdownChip(
    label: String,
    value: String?,
    options: List<SearchOption>,
    onSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val emptyLabel = defaultDropdownLabel(label)
    val text = options.firstOrNull { it.value == value }?.label ?: label

    Box {
        FilterChip(
            selected = value != null,
            onClick = { expanded = true },
            label = {
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(emptyLabel) },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )

            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelected(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchDropdown(
    label: String,
    value: String?,
    options: List<SearchOption>,
    modifier: Modifier = Modifier,
    onSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val emptyLabel = defaultDropdownLabel(label)
    val text = options.firstOrNull { it.value == value }?.label ?: emptyLabel

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$label: $text",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(emptyLabel) },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )

            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelected(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun defaultDropdownLabel(label: String): String {
    return when (label.trim().lowercase()) {
        "type" -> "Any type"
        "status" -> "Any status"
        "rating" -> "Any rating"
        "order by", "orderby", "order_by" -> "Relevance / Default"
        "sort" -> "Default"
        else -> "Default"
    }
}

@Composable
private fun QuickFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

@Composable
private fun SmallSearchTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        label = { Text(label) }
    )
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SearchResultCard(
    item: MediaSearchItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.64f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl.orEmpty(),
                contentDescription = item.title,
                modifier = Modifier
                    .width(74.dp)
                    .height(104.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildSearchSubtitle(item),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.mediaType.label} #${item.malId}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun buildActiveFilterLabels(filters: MediaSearchFilters): List<String> {
    return buildList {
        filters.query.takeIf { it.isNotBlank() }?.let { add("Query: $it") }
        filters.type?.takeIf { it.isNotBlank() }?.let { add("Type: $it") }
        filters.status?.takeIf { it.isNotBlank() }?.let { add("Status: $it") }
        filters.rating?.takeIf { it.isNotBlank() && filters.mediaType == SearchMediaType.Anime }?.let { add("Rating: $it") }
        filters.limit.takeIf { it.isNotBlank() && it != "12" }?.let { add("Limit: $it") }
        filters.score.takeIf { it.isNotBlank() }?.let { add("Exact Score: $it") }
        filters.minScore.takeIf { it.isNotBlank() }?.let { add("Min: $it") }
        filters.maxScore.takeIf { it.isNotBlank() }?.let { add("Max: $it") }
        filters.genres.takeIf { it.isNotBlank() }?.let { add("Genres: $it") }
        filters.genresExclude.takeIf { it.isNotBlank() }?.let { add("Exclude: $it") }
        filters.orderBy?.takeIf { it.isNotBlank() }?.let { add("Order: $it") }
        filters.sort?.takeIf { it.isNotBlank() }?.let { add("Sort: $it") }
        filters.letter.takeIf { it.isNotBlank() }?.let { add("Letter: $it") }
        filters.startDate.takeIf { it.isNotBlank() }?.let { add("Start: $it") }
        filters.endDate.takeIf { it.isNotBlank() }?.let { add("End: $it") }
        if (!filters.sfw) add("Adult allowed")
        if (filters.unapproved) add("Unapproved")
        if (filters.mediaType == SearchMediaType.Anime) {
            filters.producers.takeIf { it.isNotBlank() }?.let { add("Producers: $it") }
        } else {
            filters.magazines.takeIf { it.isNotBlank() }?.let { add("Magazines: $it") }
        }
    }
}

private fun buildSearchSubtitle(item: MediaSearchItem): String {
    val parts = buildList {
        item.type?.takeIf { it.isNotBlank() }?.let { add(it) }
        item.status?.takeIf { it.isNotBlank() }?.let { add(it) }
        item.score?.let { score -> add("Score $score") }
        item.rank?.let { rank -> add("Rank #$rank") }
        item.episodes?.let { episodes -> add("$episodes eps") }
        item.chapters?.let { chapters -> add("$chapters ch") }
        item.volumes?.let { volumes -> add("$volumes vol") }
    }
    return parts.joinToString(" • ").ifBlank { "Tidak ada metadata tambahan" }
}

private data class SearchOption(
    val value: String,
    val label: String
)

private val ANIME_TYPES = listOf(
    SearchOption("tv", "TV"),
    SearchOption("movie", "Movie"),
    SearchOption("ova", "OVA"),
    SearchOption("special", "Special"),
    SearchOption("ona", "ONA"),
    SearchOption("music", "Music"),
    SearchOption("cm", "CM"),
    SearchOption("pv", "PV"),
    SearchOption("tv_special", "TV Special")
)

private val MANGA_TYPES = listOf(
    SearchOption("manga", "Manga"),
    SearchOption("novel", "Novel"),
    SearchOption("lightnovel", "Light Novel"),
    SearchOption("oneshot", "One-shot"),
    SearchOption("doujin", "Doujin"),
    SearchOption("manhwa", "Manhwa"),
    SearchOption("manhua", "Manhua")
)

private val ANIME_STATUSES = listOf(
    SearchOption("airing", "Airing"),
    SearchOption("complete", "Complete"),
    SearchOption("upcoming", "Upcoming")
)

private val MANGA_STATUSES = listOf(
    SearchOption("publishing", "Publishing"),
    SearchOption("complete", "Complete"),
    SearchOption("hiatus", "Hiatus"),
    SearchOption("discontinued", "Discontinued"),
    SearchOption("upcoming", "Upcoming")
)

private val ANIME_RATINGS = listOf(
    SearchOption("g", "G - All Ages"),
    SearchOption("pg", "PG - Children"),
    SearchOption("pg13", "PG-13"),
    SearchOption("r17", "R - 17+"),
    SearchOption("r", "R+ - Mild Nudity"),
    SearchOption("rx", "Rx - Hentai")
)

private val ANIME_ORDER_BY = listOf(
    SearchOption("mal_id", "MAL ID"),
    SearchOption("title", "Title"),
    SearchOption("start_date", "Start Date"),
    SearchOption("end_date", "End Date"),
    SearchOption("episodes", "Episodes"),
    SearchOption("score", "Score"),
    SearchOption("scored_by", "Scored By"),
    SearchOption("rank", "Rank"),
    SearchOption("popularity", "Popularity"),
    SearchOption("members", "Members")
)

private val MANGA_ORDER_BY = listOf(
    SearchOption("mal_id", "MAL ID"),
    SearchOption("title", "Title"),
    SearchOption("start_date", "Start Date"),
    SearchOption("end_date", "End Date"),
    SearchOption("chapters", "Chapters"),
    SearchOption("volumes", "Volumes"),
    SearchOption("score", "Score"),
    SearchOption("scored_by", "Scored By"),
    SearchOption("rank", "Rank"),
    SearchOption("popularity", "Popularity")
)

private val SORT_OPTIONS = listOf(
    SearchOption("desc", "Desc"),
    SearchOption("asc", "Asc")
)