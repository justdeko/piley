package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.common.OutlineCard
import com.dk.piley.ui.common.TitleHeader

@Composable
fun ProfileSection(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    OutlineCard(modifier = modifier.padding(horizontal = 16.dp)) {
        TitleHeader(
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            title = title,
            icon = icon
        )
        content()
    }
}