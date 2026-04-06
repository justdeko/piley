package com.dk.piley.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import kotlin.math.roundToInt

/**
 * Expandable content
 *
 * @param modifier default modifier
 * @param expanded whether content is expanded
 * @param onHeaderClick when clicking on the header
 * @param onArrowClick when clicking on the expand arrow
 * @param headerContent content of the header
 * @param expandedContent content under the header that collapses
 */
@Composable
fun ExpandableContent(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onHeaderClick: (() -> Unit)? = null,
    onArrowClick: () -> Unit,
    headerContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    val arrowRotationDegree by animateFloatAsState(
        if (expanded) 90f else 0f, label = "animate arrow rotation"
    )
    Box(modifier = modifier.clickable {
        if (onHeaderClick != null) {
            onHeaderClick()
        }
    }) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    headerContent()
                }
                IconButton(
                    onClick = onArrowClick,
                    content = {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Expandable Arrow",
                            modifier = Modifier.rotate(arrowRotationDegree),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
            ExpandableBox(expanded = expanded) {
                expandedContent()
            }
        }
    }
}

@Composable
private fun ExpandableBox(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val expandProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        label = "expand"
    )
    Layout(
        content = {
            Box(
                modifier = Modifier.pointerInput(expanded) {
                    if (!expanded) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent().changes.forEach { it.consume() }
                            }
                        }
                    }
                }
            ) {
                content()
            }
        },
        modifier = modifier.clip(RectangleShape)
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        val height = (placeable.height * expandProgress).roundToInt()
        layout(placeable.width, height) {
            placeable.placeRelative(0, 0)
        }
    }
}
