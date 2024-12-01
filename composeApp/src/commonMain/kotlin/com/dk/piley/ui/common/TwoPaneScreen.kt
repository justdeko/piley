package com.dk.piley.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass

// TODO see if it makes sense to replace with SupportingPaneScaffold
@Composable
fun TwoPaneScreen(
    modifier: Modifier = Modifier,
    masterContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit
) {
    val isTabletWide =
        currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            masterContent()
        }
        if (isTabletWide) {
            Column(modifier = Modifier.weight(1f)) {
                detailContent()
            }
        }
    }
}