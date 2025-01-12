package com.dk.piley.ui.piles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dk.piley.model.pile.PileColor
import com.dk.piley.ui.common.LocalDim

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    selectedColor: PileColor,
    onSelected: (PileColor) -> Unit
) {
    var selected by remember { mutableStateOf(selectedColor) } // TODO fix this
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(LocalDim.current.large)
    ) {
        PileColor.entries.forEach { color ->
            ColorButton(
                color = color,
                isSelected = color == selected,
                onClick = {
                    selected = color
                    onSelected(color)
                }
            )
        }
    }
}

@Composable
fun ColorButton(color: PileColor, isSelected: Boolean, onClick: () -> Unit) {
    val border = if (isSelected) Modifier.border(
        2.dp,
        MaterialTheme.colorScheme.onSurface,
        CircleShape
    ) else Modifier
    Box(
        modifier = Modifier
            .size(24.dp)
            .then(border)
            .padding(1.dp)
            .clip(CircleShape)
            .background(color.toColor())
            .clickable { onClick() }
    )
}

@Composable
private fun PileColor.toColor(): Color {
    return when (this) {
        PileColor.NONE -> MaterialTheme.colorScheme.surfaceContainerLow
        PileColor.RED -> Color(0xFFEF476F)
        PileColor.YELLOW -> Color(0xFFFFD166)
        PileColor.GREEN -> Color(0xFF06D6A0)
        PileColor.BLUE -> Color(0xFF118AB2)
        PileColor.DARK_BLUE -> Color(0xFF073B4C)
    }
}

