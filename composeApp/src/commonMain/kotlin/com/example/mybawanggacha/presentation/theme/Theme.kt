package com.example.mybawanggacha.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.mybawanggacha.domain.settings.model.AppColorScheme
import com.example.mybawanggacha.presentation.theme.color.GruvboxColor
import com.example.mybawanggacha.presentation.theme.color.PakHabibColor

// ==================== COLOR SCHEMES ====================

private val CodeGeassLightColorScheme = lightColorScheme(
    primary = CodeGeassColors.CcPrimary,
    onPrimary = CodeGeassColors.CcOnPrimary,
    primaryContainer = CodeGeassColors.CcPrimaryContainer,
    onPrimaryContainer = CodeGeassColors.CcOnPrimaryContainer,
    secondary = CodeGeassColors.CcSecondary,
    onSecondary = CodeGeassColors.CcOnSecondary,
    secondaryContainer = CodeGeassColors.CcSecondaryContainer,
    onSecondaryContainer = CodeGeassColors.CcOnSecondaryContainer,
    tertiary = CodeGeassColors.CcTertiary,
    onTertiary = CodeGeassColors.CcOnTertiary,
    tertiaryContainer = CodeGeassColors.CcTertiaryContainer,
    onTertiaryContainer = CodeGeassColors.CcOnTertiaryContainer,
    error = CodeGeassColors.CcError,
    onError = CodeGeassColors.CcOnError,
    errorContainer = CodeGeassColors.CcErrorContainer,
    onErrorContainer = CodeGeassColors.CcOnErrorContainer,
    background = CodeGeassColors.CcBackground,
    onBackground = CodeGeassColors.CcOnBackground,
    surface = CodeGeassColors.CcSurface,
    onSurface = CodeGeassColors.CcOnSurface,
    surfaceVariant = CodeGeassColors.CcSurfaceVariant,
    onSurfaceVariant = CodeGeassColors.CcOnSurfaceVariant,
    outline = CodeGeassColors.CcOutline
)

private val CodeGeassDarkColorScheme = darkColorScheme(
    primary = CodeGeassColors.LelouchTertiaryContainer,
    onPrimary = CodeGeassColors.LelouchOnTertiaryContainer,
    primaryContainer = CodeGeassColors.LelouchPrimaryContainer,
    onPrimaryContainer = CodeGeassColors.LelouchOnPrimaryContainer,
    secondary = CodeGeassColors.LelouchSecondary,
    onSecondary = CodeGeassColors.LelouchOnSecondary,
    tertiary = CodeGeassColors.LelouchPrimary,
    onTertiary = CodeGeassColors.LelouchOnPrimary,
    background = CodeGeassColors.LelouchDeepBlack,
    onBackground = CodeGeassColors.LelouchOnBackground,
    surface = CodeGeassColors.LelouchSurfaceDark,
    onSurface = CodeGeassColors.LelouchOnSurface,
    surfaceVariant = CodeGeassColors.LelouchSurfaceVariant,
    onSurfaceVariant = CodeGeassColors.LelouchOnBackground,
    outline = CodeGeassColors.LelouchPrimary
)


private val PakHabibLightColorScheme = lightColorScheme(
    primary = PakHabibColor.PrimaryLight,
    onPrimary = PakHabibColor.OnPrimaryLight,
    primaryContainer = PakHabibColor.PrimaryContainerLight,
    onPrimaryContainer = PakHabibColor.OnPrimaryContainerLight,
    secondary = PakHabibColor.SecondaryLight,
    onSecondary = PakHabibColor.OnSecondaryLight,
    secondaryContainer = PakHabibColor.SecondaryContainerLight,
    onSecondaryContainer = PakHabibColor.OnSecondaryContainerLight,
    tertiary = PakHabibColor.TertiaryLight,
    onTertiary = PakHabibColor.OnTertiaryLight,
    tertiaryContainer = PakHabibColor.TertiaryContainerLight,
    onTertiaryContainer = PakHabibColor.OnTertiaryContainerLight,
    error = PakHabibColor.ErrorLight,
    onError = PakHabibColor.OnErrorLight,
    errorContainer = PakHabibColor.ErrorContainerLight,
    onErrorContainer = PakHabibColor.OnErrorContainerLight,
    background = PakHabibColor.BackgroundLight,
    onBackground = PakHabibColor.OnBackgroundLight,
    surface = PakHabibColor.SurfaceLight,
    onSurface = PakHabibColor.OnSurfaceLight,
    surfaceVariant = PakHabibColor.SurfaceVariantLight,
    onSurfaceVariant = PakHabibColor.OnSurfaceVariantLight,
    outline = PakHabibColor.OutlineLight
)

private val PakHabibDarkColorScheme = darkColorScheme(
    primary = PakHabibColor.PrimaryDark,
    onPrimary = PakHabibColor.OnPrimaryDark,
    primaryContainer = PakHabibColor.PrimaryContainerDark,
    onPrimaryContainer = PakHabibColor.OnPrimaryContainerDark,
    secondary = PakHabibColor.SecondaryDark,
    onSecondary = PakHabibColor.OnSecondaryDark,
    secondaryContainer = PakHabibColor.SecondaryContainerDark,
    onSecondaryContainer = PakHabibColor.OnSecondaryContainerDark,
    tertiary = PakHabibColor.TertiaryDark,
    onTertiary = PakHabibColor.OnTertiaryDark,
    tertiaryContainer = PakHabibColor.TertiaryContainerDark,
    onTertiaryContainer = PakHabibColor.OnTertiaryContainerDark,
    error = PakHabibColor.ErrorDark,
    onError = PakHabibColor.OnErrorDark,
    errorContainer = PakHabibColor.ErrorContainerDark,
    onErrorContainer = PakHabibColor.OnErrorContainerDark,
    background = PakHabibColor.BackgroundDark,
    onBackground = PakHabibColor.OnBackgroundDark,
    surface = PakHabibColor.SurfaceDark,
    onSurface = PakHabibColor.OnSurfaceDark,
    surfaceVariant = PakHabibColor.SurfaceVariantDark,
    onSurfaceVariant = PakHabibColor.OnSurfaceVariantDark,
    outline = PakHabibColor.OutlineDark
)

private val GruvboxLightColorScheme = lightColorScheme(
    primary = GruvboxColor.PrimaryLight,
    onPrimary = GruvboxColor.OnPrimaryLight,
    primaryContainer = GruvboxColor.PrimaryContainerLight,
    onPrimaryContainer = GruvboxColor.OnPrimaryContainerLight,
    secondary = GruvboxColor.SecondaryLight,
    onSecondary = GruvboxColor.OnSecondaryLight,
    secondaryContainer = GruvboxColor.SecondaryContainerLight,
    onSecondaryContainer = GruvboxColor.OnSecondaryContainerLight,
    tertiary = GruvboxColor.TertiaryLight,
    onTertiary = GruvboxColor.OnTertiaryLight,
    tertiaryContainer = GruvboxColor.TertiaryContainerLight,
    onTertiaryContainer = GruvboxColor.OnTertiaryContainerLight,
    error = GruvboxColor.ErrorLight,
    onError = GruvboxColor.OnErrorLight,
    errorContainer = GruvboxColor.ErrorContainerLight,
    onErrorContainer = GruvboxColor.OnErrorContainerLight,
    background = GruvboxColor.BgLight,
    onBackground = GruvboxColor.OnBackgroundLight,
    surface = GruvboxColor.BgLightHard,
    onSurface = GruvboxColor.OnSurfaceLight,
    surfaceVariant = GruvboxColor.SurfaceLight,
    onSurfaceVariant = GruvboxColor.OnSurfaceVariantLight,
    outline = GruvboxColor.OutlineLight
)

private val GruvboxDarkColorScheme = darkColorScheme(
    primary = GruvboxColor.PrimaryDark,
    onPrimary = GruvboxColor.OnPrimaryDark,
    primaryContainer = GruvboxColor.PrimaryContainerDark,
    onPrimaryContainer = GruvboxColor.OnPrimaryContainerDark,
    secondary = GruvboxColor.SecondaryDark,
    onSecondary = GruvboxColor.OnSecondaryDark,
    secondaryContainer = GruvboxColor.SecondaryContainerDark,
    onSecondaryContainer = GruvboxColor.OnSecondaryContainerDark,
    tertiary = GruvboxColor.TertiaryDark,
    onTertiary = GruvboxColor.OnTertiaryDark,
    tertiaryContainer = GruvboxColor.TertiaryContainerDark,
    onTertiaryContainer = GruvboxColor.OnTertiaryContainerDark,
    error = GruvboxColor.ErrorDark,
    onError = GruvboxColor.OnErrorDark,
    errorContainer = GruvboxColor.ErrorContainerDark,
    onErrorContainer = GruvboxColor.OnErrorContainerDark,
    background = GruvboxColor.BgDarkHard,
    onBackground = GruvboxColor.OnBackgroundDark,
    surface = GruvboxColor.BgDark,
    onSurface = GruvboxColor.OnSurfaceDark,
    surfaceVariant = GruvboxColor.SurfaceDark,
    onSurfaceVariant = GruvboxColor.OnSurfaceVariantDark,
    outline = GruvboxColor.OutlineDark
)

// ==================== THEME ====================

@Composable
fun MBGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appColorScheme: AppColorScheme = AppColorScheme.CodeGeass,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appColorScheme) {
        AppColorScheme.CodeGeass -> if (darkTheme) CodeGeassDarkColorScheme else CodeGeassLightColorScheme
        AppColorScheme.PakHabib -> if (darkTheme) PakHabibDarkColorScheme else PakHabibLightColorScheme
        AppColorScheme.Gruvbox -> if (darkTheme) GruvboxDarkColorScheme else GruvboxLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
