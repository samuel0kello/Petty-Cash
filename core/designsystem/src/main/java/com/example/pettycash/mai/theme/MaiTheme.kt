package com.example.pettycash.mai.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object MaiTheme {
    val colors: androidx.compose.material3.ColorScheme
        @Composable get() = MaterialTheme.colorScheme

    val extendedColors: MaiExtendedColors
        @Composable get() = LocalMaiExtendedColors.current

    val spacing: MaiSpacing
        @Composable get() = LocalMaiSpacing.current

    val shapes: MaiShapes
        @Composable get() = LocalMaiShapes.current

    val motion: MaiMotion
        @Composable get() = LocalMaiMotion.current

    val customTypography: MaiCustomTextStyles get() = MaiCustomTypography
}

@Composable
fun MaiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) MaiDarkColorScheme else MaiLightColorScheme
    val extendedColors = if (darkTheme) MaiExtendedColors.dark else MaiExtendedColors.light

    val shapes = MaiShapes()

    CompositionLocalProvider(
        LocalMaiExtendedColors provides extendedColors,
        LocalMaiSpacing provides MaiSpacing(),
        LocalMaiShapes provides shapes,
        LocalMaiMotion provides MaiMotion(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MaiTypography,
        ) {
            content()
        }
    }
}
