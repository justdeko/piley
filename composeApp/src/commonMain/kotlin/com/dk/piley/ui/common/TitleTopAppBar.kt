package com.dk.piley.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Top app bar with title
 *
 * @param textValue title text
 * @param canEdit if title should be displayed as editable (enabled vs. disabled)
 * @param justTitle whether it is just the title or an editable title
 * @param onEdit on title edit
 * @param icon title icon
 * @param contentDescription content description of the title icon
 * @param onButtonClick on icon click
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopAppBar(
    textValue: String,
    canEdit: Boolean = false,
    justTitle: Boolean = false,
    onEdit: (String) -> Unit = {},
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    contentDescription: String? = null,
    onButtonClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (justTitle) {
                Text(
                    textValue,
                    style = MaterialTheme.typography.headlineMedium
                )
            } else {
                EditableTitleText(textValue, canEdit, onEdit)
            }
        },
        navigationIcon = {
            IconButton(onClick = onButtonClick) {
                Icon(
                    imageVector = icon,
                    contentDescription,
                    modifier = Modifier.scale(
                        1.3F
                    ),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )
}
