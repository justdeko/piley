package com.dk.piley.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.util.BigSpacer

/**
 * Title header with icon
 *
 * @param modifier default modifier
 * @param title header title
 * @param icon header icon
 * @param titleColor text color of title
 */
@Composable
fun TitleHeader(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    titleColor: Color = MaterialTheme.colorScheme.secondary
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            modifier = Modifier.scale(1.2F),
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null,
        )
        BigSpacer()
        Text(
            text = title,
            color = titleColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start
        )
    }
}
