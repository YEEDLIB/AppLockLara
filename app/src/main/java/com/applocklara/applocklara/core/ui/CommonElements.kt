package com.applocklara.applocklara.core.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val shapes: List<Shape> = listOf(
    CircleShape,
    RoundedCornerShape(8.dp),
    RoundedCornerShape(16.dp),
    CutCornerShape(8.dp),
    RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
    RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp),
    CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
).shuffled()
