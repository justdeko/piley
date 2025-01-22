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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign

/**
 * Pile title pager
 *
 * @param modifier generic modifier
 * @param pileTitleList list of pile titles
 * @param selectedPageIndex selected page index of pager
 * @param onPageChanged on pile title pager page change
 * @param onClickTitle on current pile title click
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PileTitlePager(
    modifier: Modifier = Modifier,
    pileTitleList: List<String>,
    selectedPageIndex: Int = 0,
    onPageChanged: (Int) -> Unit = {},
    onClickTitle: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPageChanged(selectedPageIndex - 1) },
            enabled = selectedPageIndex != 0
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowLeft,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        onClickTitle()
                    },
                text = pileTitleList.getOrElse(index) { "" },
                style = MaterialTheme.typography.headlineLarge.copy(
                    hyphens = Hyphens.Auto,
                    lineBreak = LineBreak.Paragraph
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
        IconButton(
            onClick = { onPageChanged(selectedPageIndex + 1) },
            enabled = selectedPageIndex != pileTitleList.lastIndex
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowRight,
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
