package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.ui.common.TitleHeader

/**
 * User profile section
 *
 * @param modifier generic modifier
 * @param title section title
 * @param icon section icon
 * @param content section content
 */
@Composable
fun ProfileSection(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    OutlineCard(modifier = modifier.padding(horizontal = LocalDim.current.large)) {
        TitleHeader(
            modifier = Modifier.padding(
                start = LocalDim.current.large,
                bottom = LocalDim.current.medium
            ),
            title = title,
            icon = icon
        )
        content()
    }
}