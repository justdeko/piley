package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.common.ExpandableContent
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.common.TitleHeader
import com.dk.piley.ui.theme.PileyTheme

/**
 * Settings section
 *
 * @param modifier generic modifier
 * @param title section title
 * @param icon section icon
 * @param expandedState section expanded state
 * @param items section items
 */
@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    expandedState : MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
    items: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ExpandableContent(
            onHeaderClick = { expandedState.value = !expandedState.value },
            onArrowClick = { expandedState.value = !expandedState.value },
            expanded = expandedState.value,
            headerContent = {
                TitleHeader(
                    modifier = Modifier.padding(
                        horizontal = LocalDim.current.large,
                        vertical = LocalDim.current.medium
                    ),
                    title = title,
                    icon = icon,
                    titleColor = MaterialTheme.colorScheme.secondary
                )
            }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                items()
            }
        }
    }
}

@Preview
@Composable
fun SettingsSectionPreview() {
    var on by rememberSaveable { mutableStateOf(false) }

    @Composable
    fun settingsItem() {
        SwitchSettingsItem(title = "This is a switch setting",
            description = "With some description",
            value = on,
            onValueChange = { on = it })
    }
    PileyTheme(useDarkTheme = true) {
        SettingsSection(title = "This is a settings section", icon = Icons.Filled.Notifications) {
            settingsItem()
            settingsItem()
            settingsItem()
        }
    }
}