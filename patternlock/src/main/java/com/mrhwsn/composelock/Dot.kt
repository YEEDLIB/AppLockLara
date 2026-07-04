package com.mrhwsn.composelock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.geometry.Offset

/**
 * Represents a single dot in the pattern grid.
 * Overrides equals/hashCode to compare only by ID, preventing Animatable state from breaking collection checks.
 */
data class Dot(
    val id: Int,
    val offset: Offset,
    val size: Animatable<Float, AnimationVector1D>
)
