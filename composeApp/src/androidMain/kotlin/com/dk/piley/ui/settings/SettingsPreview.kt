package com.dk.piley.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.compose.PreviewMainScreen
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.User
import com.dk.piley.ui.theme.PileyTheme


@Preview
@Composable
fun DeleteUserContentPreview() {
    PileyTheme(useDarkTheme = true) {
        DeleteUserContent(
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
fun DeleteUserOfflineContentPreview() {
    PileyTheme(useDarkTheme = true) {
        DeleteUserContent(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun EditUserContentPreview() {
    PileyTheme(useDarkTheme = true) {
        EditUserContent(
            modifier = Modifier.fillMaxWidth(),
            existingName = "Thomas"
        )
    }
}

@Preview
@Composable
fun EditUserContentOfflinePreview() {
    PileyTheme(useDarkTheme = true) {
        EditUserContent(
            modifier = Modifier.fillMaxWidth(),
            existingName = "Thomas"
        )
    }
}

@Preview
@Composable
fun SwitchSettingsItemPreview() {
    var on by rememberSaveable { mutableStateOf(false) }
    PileyTheme(useDarkTheme = true) {
        SwitchSettingsItem(
            title = "This is a switch setting",
            description = "With some description",
            value = on,
            onValueChange = { on = it }
        )
    }
}

@Preview
@Composable
fun DropdownSettingsItemPreview() {
    val options = PileMode.values().toList().map { it.name }
    var selectedOption by rememberSaveable { mutableStateOf(PileMode.FREE.name) }
    PileyTheme(useDarkTheme = true) {
        DropdownSettingsItem(
            title = "This is a dropdown setting",
            description = "With some description",
            optionLabel = "Pile Mode",
            selectedValue = selectedOption,
            values = options,
            onValueChange = { selectedOption = it }
        )
    }
}

@Preview
@Composable
fun TextSettingsItemPreview() {
    PileyTheme(useDarkTheme = true) {
        SettingsItem(
            title = "This is a settings item without anything",
            description = "And some description for it"
        )
    }
}

@Preview
@Composable
fun SliderSettingsItemPreview() {
    PileyTheme(useDarkTheme = true) {
        SliderSettingsItem(
            title = "This is a slider setting",
            description = "With some description",
            value = 15,
            range = Pair(15, 45),
            steps = 1,
            onValueChange = {}
        )
    }
}

@PreviewMainScreen
@Preview(heightDp = 1000)
@Composable
fun SettingsScreenPreview() {
    PileyTheme {
        Surface {
            val state = SettingsViewState(User(), loading = true)
            SettingsScreen(viewState = state)
        }
    }
}

@Preview
@Composable
fun AppInfoPreview() {
    PileyTheme(useDarkTheme = true) {
        AppInfo()
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