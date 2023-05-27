package com.dk.piley.ui.pile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.dk.piley.ui.theme.PileyTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PileTitlePager(
    modifier: Modifier = Modifier,
    pileTitleList: List<String>,
    onPageChanged: (Int) -> Unit = {},
) {
    val pagerState = rememberPagerState()
    LaunchedEffect(pagerState) {
        snapshotFlow {
            pagerState.currentPage
        }.distinctUntilChanged().collect { page ->
            onPageChanged(page)
        }
    }
    Box(modifier = modifier) {
        // TODO make infinite
        HorizontalPager(pageCount = pileTitleList.size, state = pagerState) { page ->
            Text(
                text = pileTitleList[page],
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .size(height = 50.dp, width = 80.dp)
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                            (pagerState.currentPage - page) + pagerState
                                .currentPageOffsetFraction
                            ).absoluteValue

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PileTitlePagerPreview() {
    PileyTheme(useDarkTheme = true) {
        PileTitlePager(
            modifier = Modifier.fillMaxWidth(),
            pileTitleList = listOf("Pile1", "Pile2", "Pile3", "Pile4"),
        )
    }
}