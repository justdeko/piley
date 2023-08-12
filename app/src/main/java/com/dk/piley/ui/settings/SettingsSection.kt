package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier, title: String, icon: ImageVector, items: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        ExpandableContent(
            onHeaderClick = { expanded = !expanded },
            onArrowClick = { expanded = !expanded },
            expanded = expanded,
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