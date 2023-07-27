package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.common.ExpandableContent
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
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        icon,
                        modifier = Modifier.scale(1.3F),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }) {
            Column(modifier = modifier.fillMaxWidth()) {
                items()
            }
        }
        Divider(modifier = Modifier.padding(vertical = 4.dp))
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