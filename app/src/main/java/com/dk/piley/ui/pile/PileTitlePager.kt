package com.dk.piley.ui.pile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme

/**
 * Pile title pager
 *
 * @param modifier generic modifier
 * @param pileTitleList list of pile titles
 * @param selectedPageIndex selected page index of pager
 * @param onPageChanged on pile title pager page change
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PileTitlePager(
    modifier: Modifier = Modifier,
    pileTitleList: List<String>,
    selectedPageIndex: Int = 0,
    onPageChanged: (Int) -> Unit = {},
) {
    Row(
        modifier = modifier.padding(LocalDim.current.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPageChanged(selectedPageIndex - 1) },
            enabled = selectedPageIndex != 0
        ) {
            Icon(
                Icons.Filled.ArrowLeft,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = LocalContentColor.current.alpha),
                contentDescription = "switch to left pile"
            )
        }
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = selectedPageIndex,
            transitionSpec = {
                titleSlideAnimation(leftToRight = targetState > initialState).using(
                    SizeTransform(clip = false)
                )
            }, label = "pile slide animation"
        ) { index ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = pileTitleList.getOrElse(index) { "" },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
        IconButton(
            onClick = { onPageChanged(selectedPageIndex + 1) },
            enabled = selectedPageIndex != pileTitleList.lastIndex
        ) {
            Icon(
                Icons.Filled.ArrowRight,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = LocalContentColor.current.alpha),
                contentDescription = "switch to right pile"
            )
        }
    }
}

@ExperimentalAnimationApi
fun titleSlideAnimation(duration: Int = 500, leftToRight: Boolean = true): ContentTransform =
    (slideInHorizontally(animationSpec = tween(durationMillis = duration)) { width ->
        if (leftToRight) width else -width
    } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    )).togetherWith(slideOutHorizontally(animationSpec = tween(durationMillis = duration)) { width ->
        if (leftToRight) -width else width
    } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    ))


@Preview(showBackground = false)
@Composable
fun PileTitlePagerPreview() {
    PileyTheme(useDarkTheme = true) {
        PileTitlePager(
            modifier = Modifier.fillMaxWidth(),
            pileTitleList = listOf("Pile1", "Pile2", "Pile3", "Pile4"),
            selectedPageIndex = 2
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PileTitlePagerPreviewLeftDisabled() {
    PileyTheme(useDarkTheme = true) {
        PileTitlePager(
            modifier = Modifier.fillMaxWidth(),
            pileTitleList = listOf("Pile1", "Pile2", "Pile3", "Pile4"),
            selectedPageIndex = 0
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PileTitlePagerPreviewRightDisabled() {
    PileyTheme(useDarkTheme = true) {
        PileTitlePager(
            modifier = Modifier.fillMaxWidth(),
            pileTitleList = listOf("Pile1", "Pile2", "Pile3", "Pile4"),
            selectedPageIndex = 3
        )
    }
}