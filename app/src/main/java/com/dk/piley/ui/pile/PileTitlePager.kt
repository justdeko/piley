package com.dk.piley.ui.pile

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
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun PileTitlePager(
    modifier: Modifier = Modifier,
    pileTitleList: List<String>,
    selectedPageIndex: Int = 0,
    onPageChanged: (Int) -> Unit = {},
) {
    Row(
        modifier = modifier.padding(vertical = 16.dp, horizontal = 8.dp),
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
        Text(
            text = pileTitleList.getOrElse(selectedPageIndex) { "" },
            modifier = Modifier
                .weight(1f),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
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