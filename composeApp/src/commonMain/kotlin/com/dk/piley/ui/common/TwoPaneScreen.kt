package com.dk.piley.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dk.piley.util.isTabletWide

// TODO see if it makes sense to replace with SupportingPaneScaffold
@Composable
fun TwoPaneScreen(
    modifier: Modifier = Modifier,
    mainContent: @Composable (Boolean) -> Unit,
    detailContent: @Composable () -> Unit
) {
    val isTabletWide = isTabletWide()
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            mainContent(isTabletWide)
        }
        if (isTabletWide) {
            Column(modifier = Modifier.weight(1f)) {
                detailContent()
            }
        }
    }
}