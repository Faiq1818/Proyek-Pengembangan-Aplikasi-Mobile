package com.example.mybawanggacha.presentation.screens.gacha

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import com.example.mybawanggacha.domain.gacha.model.GachaMediaFormat
import com.example.mybawanggacha.domain.gacha.model.GachaMediaPool
import com.example.mybawanggacha.domain.gacha.model.GachaPreference
import com.example.mybawanggacha.domain.gacha.model.GachaResultItem
import com.example.mybawanggacha.domain.gacha.model.GachaResultMediaType
import com.example.mybawanggacha.domain.gacha.model.GachaStatusFilter
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GachaScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit,
    onNavigateToMangaDetail: (Int) -> Unit,
    viewModel: GachaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.Gacha,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.Search -> onNavigateToSearch()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
                MBGMainRailKey.Gacha -> Unit
            }
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        GachaContent(
            uiState = uiState,
            onPreferenceChange = viewModel::updatePreference,
            onRunGacha = viewModel::runGacha,
            onReroll = viewModel::runGacha,
            onAddToLibrary = viewModel::addResultToLibrary,
            onClearHistory = viewModel::clearHistory,
            onOpenDetail = { item ->
                when (item.mediaType) {
                    GachaResultMediaType.Anime -> onNavigateToAnimeDetail(item.malId)
                    GachaResultMediaType.Manga -> onNavigateToMangaDetail(item.malId)
                }
            }
        )
    }
}

@Composable
private fun GachaContent(
    uiState: GachaUiState,
    onPreferenceChange: ((GachaPreference) -> GachaPreference) -> Unit,
    onRunGacha: () -> Unit,
    onReroll: () -> Unit,
    onAddToLibrary: () -> Unit,
    onClearHistory: () -> Unit,
    onOpenDetail: (GachaResultItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 4.dp, top = 32.dp, end = 18.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item(contentType = "gacha_header") {
            GachaHeader()
        }

        item(contentType = "gacha_preferences") {
            GachaPreferencePanel(
                preference = uiState.preference,
                isLoading = uiState.isLoading,
                onPreferenceChange = onPreferenceChange,
                onRunGacha = onRunGacha
            )
        }

        uiState.errorMessage?.let { message ->
            item(contentType = "gacha_error") {
                GachaInlineMessage(
                    text = message,
                    isError = true
                )
            }
        }

        uiState.infoMessage?.let { message ->
            item(contentType = "gacha_info") {
                GachaInlineMessage(
                    text = message,
                    isError = false
                )
            }
        }

        item(contentType = "gacha_result") {
            val result = uiState.result
            if (result == null) {
                EmptyState(
                    title = "Belum ada hasil",
                    message = "Atur preferensi lalu tekan Gacha.",
                    modifier = Modifier.height(220.dp)
                )
            } else {
                GachaResultScreen(
                    item = result,
                    isLoading = uiState.isLoading,
                    onReroll = onReroll,
                    onAddToLibrary = onAddToLibrary,
                    onOpenDetail = { onOpenDetail(result) }
                )
            }
        }

        if (uiState.history.isNotEmpty()) {
            item(contentType = "gacha_history_header") {
                GachaHistoryHeader(
                    count = uiState.history.size,
                    onClearHistory = onClearHistory
                )
            }

            items(
                items = uiState.history.take(8),
                key = { history -> "${history.item.mediaType}:${history.item.malId}:${history.pickedAtEpochMillis}" },
                contentType = { "gacha_history_item" }
            ) { history ->
                GachaHistoryRow(
                    history = history,
                    onClick = { onOpenDetail(history.item) }
                )
            }
        }
    }
}

@Composable
private fun GachaHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Gacha",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pick acak dari Jikan berdasarkan preferensi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GachaPreferencePanel(
    preference: GachaPreference,
    isLoading: Boolean,
    onPreferenceChange: ((GachaPreference) -> GachaPreference) -> Unit,
    onRunGacha: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GachaMediaPoolRow(
            selected = preference.mediaPool,
            onSelected = { mediaPool ->
                onPreferenceChange { current -> current.copy(mediaPool = mediaPool) }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CompactTextField(
                label = "Genre IDs",
                value = preference.genreIds,
                modifier = Modifier.weight(1f),
                onValueChange = { value ->
                    onPreferenceChange { current -> current.copy(genreIds = value) }
                }
            )
            CompactTextField(
                label = "Min score",
                value = preference.minScore,
                modifier = Modifier.weight(1f),
                onValueChange = { value ->
                    onPreferenceChange { current -> current.copy(minScore = value) }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GachaDropdown(
                label = "Type",
                value = preference.format.label,
                options = GachaMediaFormat.availableFor(preference.mediaPool),
                optionLabel = { it.label },
                modifier = Modifier.weight(1f),
                onSelected = { format ->
                    onPreferenceChange { current -> current.copy(format = format) }
                }
            )
            GachaDropdown(
                label = "Status",
                value = preference.status.label,
                options = GachaStatusFilter.entries,
                optionLabel = { it.label },
                modifier = Modifier.weight(1f),
                onSelected = { status ->
                    onPreferenceChange { current -> current.copy(status = status) }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f))
                .clickable {
                    onPreferenceChange { current ->
                        current.copy(includeKnownItems = !current.includeKnownItems)
                    }
                }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Include saved/read/watched",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Off = item di My Library tidak ikut dipilih.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Switch(
                checked = preference.includeKnownItems,
                onCheckedChange = { value ->
                    onPreferenceChange { current -> current.copy(includeKnownItems = value) }
                }
            )
        }

        Button(
            onClick = onRunGacha,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(if (isLoading) "Rolling..." else "Gacha")
        }
    }
}

@Composable
private fun GachaMediaPoolRow(
    selected: GachaMediaPool,
    onSelected: (GachaMediaPool) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GachaMediaPool.entries.forEach { pool ->
            FilterChip(
                selected = selected == pool,
                onClick = { onSelected(pool) },
                label = { Text(pool.label) }
            )
        }
    }
}

@Composable
private fun CompactTextField(
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
        label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    )
}

@Composable
private fun <T> GachaDropdown(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$label: $value",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
internal fun GachaResultScreen(
    item: GachaResultItem,
    isLoading: Boolean,
    onReroll: () -> Unit,
    onAddToLibrary: () -> Unit,
    onOpenDetail: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl.orEmpty(),
                contentDescription = item.title,
                modifier = Modifier
                    .width(86.dp)
                    .height(122.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
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
                    text = item.metadataLine(),
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReroll,
                        enabled = !isLoading
                    ) {
                        Text("Reroll")
                    }
                    OutlinedButton(onClick = onOpenDetail) {
                        Text("Detail")
                    }
                }

                Button(
                    onClick = onAddToLibrary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add to list")
                }
            }
        }
    }
}

@Composable
private fun GachaInlineMessage(
    text: String,
    isError: Boolean
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = if (isError) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun GachaHistoryHeader(
    count: Int,
    onClearHistory: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "History ($count)",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onClearHistory) {
            Text("Clear")
        }
    }
}

@Composable
private fun GachaHistoryRow(
    history: GachaHistoryEntry,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = history.item.mediaType.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(48.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = history.item.metadataLine(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    }
}

private fun GachaResultItem.metadataLine(): String {
    return buildList {
        type?.takeIf { it.isNotBlank() }?.let { add(it) }
        status?.takeIf { it.isNotBlank() }?.let { add(it) }
        score?.let { add("★ ${it.toString().take(4)}") }
        episodes?.let { add("$it eps") }
        chapters?.let { add("$it ch") }
        volumes?.let { add("$it vol") }
    }.joinToString(" • ").ifBlank { "No metadata" }
}
