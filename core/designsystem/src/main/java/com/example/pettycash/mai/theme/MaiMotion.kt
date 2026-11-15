package com.example.pettycash.mai.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/** Durations (ms) and easings from the Figma "Motion tokens" foundations. */
@Immutable
data class MaiMotion(
    val short2: Int = 100,
    val short4: Int = 200,
    val medium2: Int = 300,
    val medium4: Int = 400,
    val long2: Int = 500,
    val easingEmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f),
    val easingEmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f),
    val easingStandard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f),
) {
    companion object
}

internal val LocalMaiMotion = staticCompositionLocalOf { MaiMotion() }

val MaiMotion.Companion.current: MaiMotion
    @Composable get() = LocalMaiMotion.current

/** Shared-axis horizontal slide used for "next step in a task" transitions (Motion pattern 1). */
fun maiSharedAxisX(
    easing: Easing,
    durationMillis: Int,
    offsetFraction: Float = 0.3f,
): (Int) -> IntOffset = { distance ->
    IntOffset((distance * offsetFraction).roundToInt(), 0)
}
