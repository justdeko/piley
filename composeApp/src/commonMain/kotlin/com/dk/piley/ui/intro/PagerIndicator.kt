package com.dk.piley.ui.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.dk.piley.ui.common.LocalDim

/**
 * Pager indicator for intro screen
 *
 * @param modifier generic modifier
 * @param pagerState pager indicator state
 */
@Composable
fun PagerIndicator(modifier: Modifier = Modifier, pagerState: PagerState) {
    Row(
        modifier
            .height(LocalDim.current.extraLarge)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            Box(
                modifier = Modifier
                    .padding(LocalDim.current.small)
                    .clip(CircleShape)
                    .background(color)
                    .size(LocalDim.current.medium)

            )
        }
    }
}
