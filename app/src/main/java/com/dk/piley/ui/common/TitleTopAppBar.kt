package com.dk.piley.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.theme.PileyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopAppBar(
    textValue: String,
    canDeleteOrEdit: Boolean = false,
    justTitle: Boolean = false,
    onEdit: (String) -> Unit = {},
    icon: ImageVector = Icons.Default.ArrowBack,
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
                EditableTitleText(textValue, canDeleteOrEdit, onEdit)
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

@Composable
@Preview
fun TitleTopAppBarPreview() {
    PileyTheme(useDarkTheme = true) {
        TitleTopAppBar(textValue = "some title", justTitle = true, onButtonClick = {})
    }
}

@Composable
@Preview
fun TitleTopAppBarDisabledPreview() {
    PileyTheme(useDarkTheme = true) {
        TitleTopAppBar(textValue = "some title", canDeleteOrEdit = false, onButtonClick = {})
    }
}