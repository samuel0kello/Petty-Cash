package com.example.pettycash.mai.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 4dp-rhythm spacing tokens (space/0 … space/64) from the Figma foundations. */
@Immutable
data class MaiSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val huge: Dp = 40.dp,
    val section: Dp = 48.dp,
    val jumbo: Dp = 64.dp,
) {
    companion object
}

internal val LocalMaiSpacing = staticCompositionLocalOf { MaiSpacing() }

val MaiSpacing.Companion.current: MaiSpacing
    @Composable get() = LocalMaiSpacing.current
