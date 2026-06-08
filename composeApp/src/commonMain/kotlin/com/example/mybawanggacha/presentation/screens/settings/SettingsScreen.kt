package com.example.mybawanggacha.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mybawanggacha.domain.settings.model.AiApiModel
import com.example.mybawanggacha.domain.settings.model.AiApiSettings
import com.example.mybawanggacha.domain.settings.model.AppColorScheme
import com.example.mybawanggacha.domain.settings.model.NetworkMode
import com.example.mybawanggacha.domain.settings.model.ThemeMode
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToGacha: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val systemDarkTheme = isSystemInDarkTheme()
    val isDarkMode = uiState.themeMode.resolve(systemDarkTheme)

    var selectedPane by remember { mutableStateOf(SettingsPane.Main) }

    MBGSideRailScaffold(
        selectedRailKey = "",
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.Search -> onNavigateToSearch()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.Gacha -> onNavigateToGacha()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
            }
        },
        topAction = {
            MBGRailBackButton(
                onClick = {
                    if (selectedPane == SettingsPane.Main) {
                        onNavigateBack()
                    } else {
                        selectedPane = SettingsPane.Main
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 4.dp, top = 32.dp, end = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedPane) {
                SettingsPane.Main -> {
                    SettingsMainMenu(
                        themeMode = uiState.themeMode,
                        isDarkMode = isDarkMode,
                        networkMode = uiState.networkMode,
                        appColorScheme = uiState.appColorScheme,
                        aiApiSettings = uiState.aiApiSettings,
                        requestUsage = uiState.requestUsage,
                        onPaneSelected = { selectedPane = it }
                    )
                }

                SettingsPane.Appearance -> {
                    SettingsPaneHeader(
                        title = "Appearance",
                        description = "Theme dan tampilan aplikasi."
                    )
                    SettingsThemeSection(
                        themeMode = uiState.themeMode,
                        isDarkMode = isDarkMode,
                        onThemeModeSelected = viewModel::setThemeMode
                    )

                    SettingsDivider()

                    SettingsColorSchemeSection(
                        selected = uiState.appColorScheme,
                        isDarkMode = isDarkMode,
                        onSelected = viewModel::setAppColorScheme
                    )
                }

                SettingsPane.DataAccess -> {
                    SettingsPaneHeader(
                        title = "Data & Offline",
                        description = "Atur network mode dan fallback cache."
                    )
                    SettingsNetworkSection(
                        networkMode = uiState.networkMode,
                        onNetworkModeSelected = viewModel::setNetworkMode
                    )
                }

                SettingsPane.Api -> {
                    SettingsPaneHeader(
                        title = "AI API",
                        description = "Pilih model dan simpan token API untuk fitur AI."
                    )
                    SettingsApiSection(
                        settings = uiState.aiApiSettings,
                        onModelSelected = viewModel::setAiApiModel,
                        onTokenChange = viewModel::setAiApiToken
                    )
                }

                SettingsPane.RequestUsage -> {
                    SettingsPaneHeader(
                        title = "Request Usage",
                        description = "Pantau pemakaian request Jikan."
                    )
                    SettingsJikanBudgetSection(requestUsage = uiState.requestUsage)
                }

                SettingsPane.About -> {
                    SettingsPaneHeader(
                        title = "About",
                        description = "Info aplikasi dan sumber data."
                    )
                    SettingsAboutSection(showTitle = false)
                }
            }
        }
    }
}


private enum class SettingsPane {
    Main,
    Appearance,
    DataAccess,
    Api,
    RequestUsage,
    About
}

@Composable
private fun SettingsMainMenu(
    themeMode: ThemeMode,
    isDarkMode: Boolean,
    networkMode: NetworkMode,
    appColorScheme: AppColorScheme,
    aiApiSettings: AiApiSettings,
    requestUsage: SettingsRequestUsageUiState,
    onPaneSelected: (SettingsPane) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        SettingsMenuRow(
            icon = Icons.Default.Verified,
            title = "Appearance",
            description = buildString {
                append(
                    when (themeMode) {
                        ThemeMode.System -> if (isDarkMode) "System theme: dark" else "System theme: light"
                        ThemeMode.Light -> "Light theme"
                        ThemeMode.Dark -> "Dark theme"
                    }
                )
                append(" • ")
                append(appColorScheme.label)
            },
            onClick = { onPaneSelected(SettingsPane.Appearance) }
        )

        SettingsMenuRow(
            icon = Icons.Default.Cloud,
            title = "Data & Offline",
            description = networkMode.description,
            onClick = { onPaneSelected(SettingsPane.DataAccess) }
        )

        SettingsMenuRow(
            icon = Icons.Default.Cloud,
            title = "AI API",
            description = "${aiApiSettings.model.label} • ${if (aiApiSettings.hasToken) "Token tersimpan" else "Token belum diisi"}",
            onClick = { onPaneSelected(SettingsPane.Api) }
        )

        SettingsMenuRow(
            icon = Icons.Default.Storage,
            title = "Request Usage",
            description = "${requestUsage.usedLastMinute}/${requestUsage.minuteLimit} request menit ini • ${requestUsage.remainingThisMinute} tersisa",
            onClick = { onPaneSelected(SettingsPane.RequestUsage) }
        )

        SettingsMenuRow(
            icon = Icons.Default.Info,
            title = "About",
            description = "Versi, app info, dan sumber data.",
            onClick = { onPaneSelected(SettingsPane.About) }
        )
    }
}

@Composable
private fun SettingsPaneHeader(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsMenuRow(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun SettingsThemeSection(
    themeMode: ThemeMode,
    isDarkMode: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (themeMode) {
                ThemeMode.System -> if (isDarkMode) "Mengikuti sistem: gelap" else "Mengikuti sistem: terang"
                ThemeMode.Light -> "Tema terang aktif"
                ThemeMode.Dark -> "Tema gelap aktif"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeChoiceChip(
                label = "System",
                selected = themeMode == ThemeMode.System,
                onClick = { onThemeModeSelected(ThemeMode.System) }
            )
            ThemeChoiceChip(
                label = "Light",
                selected = themeMode == ThemeMode.Light,
                onClick = { onThemeModeSelected(ThemeMode.Light) }
            )
            ThemeChoiceChip(
                label = "Dark",
                selected = themeMode == ThemeMode.Dark,
                onClick = { onThemeModeSelected(ThemeMode.Dark) }
            )
        }
    }
}


@Composable
private fun SettingsColorSchemeSection(
    selected: AppColorScheme,
    isDarkMode: Boolean,
    onSelected: (AppColorScheme) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Colorscheme",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = AppColorScheme.entries.toList(),
                key = { scheme -> scheme.name }
            ) { scheme ->
                SettingsColorSchemeRow(
                    scheme = scheme,
                    selected = scheme == selected,
                    isDarkMode = isDarkMode,
                    onClick = { onSelected(scheme) }
                )
            }
        }
    }
}

@Composable
private fun SettingsColorSchemeRow(
    scheme: AppColorScheme,
    selected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(230.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            },
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = scheme.label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                scheme.swatches(darkTheme = isDarkMode).forEach { hex ->
                    ColorSwatch(hex = hex)
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(hex: String) {
    Surface(
        modifier = Modifier.size(18.dp),
        shape = CircleShape,
        color = hex.toColor()
    ) {}
}

private fun String.toColor(): Color {
    val rgb = removePrefix("#").toLongOrNull(radix = 16) ?: 0L
    return Color(0xFF000000L or rgb)
}



@Composable
private fun SettingsApiSection(
    settings: AiApiSettings,
    onModelSelected: (AiApiModel) -> Unit,
    onTokenChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Model",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        AiModelDropdown(
            selected = settings.model,
            onSelected = onModelSelected
        )

        SettingsDivider()

        Text(
            text = "API token",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
            value = settings.token,
            onValueChange = onTokenChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Token") },
            placeholder = { Text("Gemini API key") },
            visualTransformation = PasswordVisualTransformation()
        )

        Text(
            text = if (settings.hasToken) {
                "Token tersimpan lokal. Kosongkan untuk fallback ke config platform."
            } else {
                "Kosong: memakai config platform jika tersedia."
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AiModelDropdown(
    selected: AiApiModel,
    onSelected: (AiApiModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = "${selected.label}  ·  ${selected.modelId}",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AiApiModel.entries.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = model.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = model.modelId,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelected(model)
                    }
                )
            }
        }
    }
}


@Composable
private fun SettingsNetworkSection(
    networkMode: NetworkMode,
    onNetworkModeSelected: (NetworkMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Network",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = networkMode.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NetworkChoiceChip(
                label = NetworkMode.Auto.label,
                selected = networkMode == NetworkMode.Auto,
                onClick = { onNetworkModeSelected(NetworkMode.Auto) }
            )
            NetworkChoiceChip(
                label = NetworkMode.OfflineOnly.label,
                selected = networkMode == NetworkMode.OfflineOnly,
                onClick = { onNetworkModeSelected(NetworkMode.OfflineOnly) }
            )
        }
    }
}

@Composable
private fun SettingsJikanBudgetSection(
    requestUsage: SettingsRequestUsageUiState
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Jikan Request Budget",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        SettingsRequestUsageCard(requestUsage = requestUsage)

    }
}

@Composable
private fun SettingsRequestUsageCard(
    requestUsage: SettingsRequestUsageUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Request terpakai",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${requestUsage.usedLastMinute}/${requestUsage.minuteLimit}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = requestUsage.minuteProgress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsUsageMetric(
                        label = "Menit ini",
                        value = "${requestUsage.remainingThisMinute} tersisa",
                        modifier = Modifier.weight(1f)
                    )
                    SettingsUsageMetric(
                        label = "Detik ini",
                        value = "${requestUsage.usedLastSecond}/${requestUsage.secondLimit}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = requestUsage.cooldownLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsUsageMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SettingsDivider() {
    Spacer(modifier = Modifier.height(24.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun SettingsAboutSection(
    showTitle: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showTitle) {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }

        SettingsInfoCard(
            icon = Icons.Default.Info,
            title = "MyBawangGacha",
            description = "Anime, manga, library dan gacha di 1 app."
        )

        SettingsInfoCard(
            icon = Icons.Default.Verified,
            title = "Version 1.0",
            description = "lebih tepatnya karena masih sprint 3, jadi v1.0-rc3 lmao."
        )

        SettingsInfoCard(
            icon = Icons.Default.Cloud,
            title = "Data source",
            description = "Anime dan manga metadata dari API Jikan, API MyAnimeList yang unofficial."
        )
    }
}

@Composable
private fun SettingsInfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ThemeChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) }
    )
}

@Composable
private fun NetworkChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) }
    )
}