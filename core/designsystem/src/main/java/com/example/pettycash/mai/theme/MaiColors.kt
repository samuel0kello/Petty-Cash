package com.example.pettycash.mai.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Values from the Figma "Colour roles" foundations (sys/* roles), Light and Dark.

internal val MaiLightColorScheme = lightColorScheme(
    primary = Color(0xFF006C4D),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF97F5CB),
    onPrimaryContainer = Color(0xFF002115),
    secondary = Color(0xFF4C6358),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE9DA),
    onSecondaryContainer = Color(0xFF092017),
    tertiary = Color(0xFF3E6374),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC1E8FC),
    onTertiaryContainer = Color(0xFF001F2A),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF5FBF5),
    onBackground = Color(0xFF171D1A),
    surface = Color(0xFFF5FBF5),
    onSurface = Color(0xFF171D1A),
    surfaceVariant = Color(0xFFE4EAE4),
    onSurfaceVariant = Color(0xFF404944),
    outline = Color(0xFF707973),
    outlineVariant = Color(0xFFBFC9C2),
    inverseSurface = Color(0xFF2C322E),
    inverseOnSurface = Color(0xFFECF2ED),
    inversePrimary = Color(0xFF7BD8B0),
    surfaceBright = Color(0xFFF5FBF5),
    surfaceDim = Color(0xFFD6DBD6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF5EF),
    surfaceContainer = Color(0xFFEAEFEA),
    surfaceContainerHigh = Color(0xFFE4EAE4),
    surfaceContainerHighest = Color(0xFFDEE4DE),
)

internal val MaiDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7BD8B0),
    onPrimary = Color(0xFF003826),
    primaryContainer = Color(0xFF005139),
    onPrimaryContainer = Color(0xFF97F5CB),
    secondary = Color(0xFFB3CCBE),
    onSecondary = Color(0xFF1F352B),
    secondaryContainer = Color(0xFF354B41),
    onSecondaryContainer = Color(0xFFCFE9DA),
    tertiary = Color(0xFFA6CCDF),
    onTertiary = Color(0xFF083544),
    tertiaryContainer = Color(0xFF254B5B),
    onTertiaryContainer = Color(0xFFC1E8FC),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0F1512),
    onBackground = Color(0xFFDEE4DE),
    surface = Color(0xFF0F1512),
    onSurface = Color(0xFFDEE4DE),
    surfaceVariant = Color(0xFF303632),
    onSurfaceVariant = Color(0xFFBFC9C2),
    outline = Color(0xFF8A938D),
    outlineVariant = Color(0xFF404944),
    inverseSurface = Color(0xFFDEE4DE),
    inverseOnSurface = Color(0xFF2C322E),
    inversePrimary = Color(0xFF006C4D),
    surfaceBright = Color(0xFF353B37),
    surfaceDim = Color(0xFF0F1512),
    surfaceContainerLowest = Color(0xFF0A0F0C),
    surfaceContainerLow = Color(0xFF171D1A),
    surfaceContainer = Color(0xFF1B211E),
    surfaceContainerHigh = Color(0xFF252B28),
    surfaceContainerHighest = Color(0xFF303632),
)

/** Status colours that have no Material `ColorScheme` role (success, warning, info). */
@Immutable
data class MaiExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
) {
    companion object {
        val light = MaiExtendedColors(
            success = Color(0xFF206D00),
            onSuccess = Color(0xFFFFFFFF),
            successContainer = Color(0xFFA2F87F),
            onSuccessContainer = Color(0xFF052100),
            warning = Color(0xFF8A5100),
            onWarning = Color(0xFFFFFFFF),
            warningContainer = Color(0xFFFFDCBD),
            onWarningContainer = Color(0xFF2C1600),
            info = Color(0xFF0957CE),
            onInfo = Color(0xFFFFFFFF),
            infoContainer = Color(0xFFDAE2FF),
            onInfoContainer = Color(0xFF001847),
        )

        val dark = MaiExtendedColors(
            success = Color(0xFF87DB66),
            onSuccess = Color(0xFF0D3900),
            successContainer = Color(0xFF165200),
            onSuccessContainer = Color(0xFFA2F87F),
            warning = Color(0xFFFFB86E),
            onWarning = Color(0xFF492900),
            warningContainer = Color(0xFF693C00),
            onWarningContainer = Color(0xFFFFDCBD),
            info = Color(0xFFB1C5FF),
            onInfo = Color(0xFF002C72),
            infoContainer = Color(0xFF0040A0),
            onInfoContainer = Color(0xFFDAE2FF),
        )
    }
}

internal val LocalMaiExtendedColors = staticCompositionLocalOf { MaiExtendedColors.light }

val MaiExtendedColors.Companion.current: MaiExtendedColors
    @Composable get() = LocalMaiExtendedColors.current
