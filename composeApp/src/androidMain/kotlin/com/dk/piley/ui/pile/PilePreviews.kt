package com.dk.piley.ui.pile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.piles.PileCard
import com.dk.piley.util.previewPileWithTasksList
import com.dk.piley.util.previewUpcomingTasksList

@Preview
@Composable
private fun PileCardPreview() {
    PileCard(
        pileWithTasks = previewPileWithTasksList.first(),
        selected = true,
    )
}

@Preview
@Composable
private fun PileCardPreviewExpanded() {
    PileCard(
        pileWithTasks = previewPileWithTasksList.first().copy(
            tasks = previewUpcomingTasksList.map { it.second }
        ),
        expandedMode = true,
    )
}