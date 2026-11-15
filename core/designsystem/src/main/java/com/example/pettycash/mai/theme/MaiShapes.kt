package com.example.pettycash.mai.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** Radius tokens from the Figma foundations: none 0, xs 4, sm 8, md 12, lg 16, xl 28, full. */
@Immutable
data class MaiShapes(
    val none: Shape = androidx.compose.ui.graphics.RectangleShape,
    val xs: Shape = RoundedCornerShape(4.dp),
    val sm: Shape = RoundedCornerShape(8.dp),
    val md: Shape = RoundedCornerShape(12.dp),
    val lg: Shape = RoundedCornerShape(16.dp),
    val xl: Shape = RoundedCornerShape(28.dp),
    val full: Shape = CircleShape,
) {
    companion object
}

internal val LocalMaiShapes = staticCompositionLocalOf { MaiShapes() }

val MaiShapes.Companion.current: MaiShapes
    @Composable get() = LocalMaiShapes.current
