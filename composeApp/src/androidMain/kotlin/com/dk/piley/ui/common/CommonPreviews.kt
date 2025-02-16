package com.dk.piley.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.defaultPadding
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
@Preview
fun ReminderDatePickerPreview() {
    PileyTheme(useDarkTheme = true) {
        ReminderDatePicker(initialDate = LocalDate(2024, 10, 13), onDismiss = {}, onConfirm = {})
    }
}

@Composable
@Preview
fun ReminderTimePickerPreview() {
    PileyTheme(useDarkTheme = true) {
        ReminderTimePicker(initialTime = LocalTime(13, 24), onDismiss = {}, onConfirm = {})
    }
}

@Preview
@Composable
fun PreviewDropdown() {
    PileyTheme(useDarkTheme = true) {
        DropDown(value = "a", dropdownValues = listOf("a", "b", "c"), label = "select a letter")
    }
}

@Preview
@Composable
fun EditDescriptionFieldPreview() {
    PileyTheme(useDarkTheme = true) {
        val text = "hi there\nsdf\nsdf\nsdfiu\ndf\n6\n7 alsodo"
        EditDescriptionField(value = text, onChange = {})
    }
}

@Preview
@Composable
fun ExpandableContentPreview() {
    var expanded by remember { mutableStateOf(false) }
    PileyTheme(useDarkTheme = true) {
        Card {
            ExpandableContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultPadding(),
                expanded = expanded,
                onArrowClick = { expanded = !expanded },
                headerContent = {
                    Text(text = "some title")
                },
                expandedContent = {
                    Text("hi")
                }
            )
        }
    }
}

@Preview
@Composable
fun OutlineCardPreview() {
    PileyTheme(useDarkTheme = true) {
        OutlineCard {
            Text(text = "Some text", color = MaterialTheme.colorScheme.onBackground)
            Text(text = "Some other text", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    PileyTheme(useDarkTheme = true) {
        TextWithCheckbox(
            modifier = Modifier.fillMaxWidth(),
            description = "some description",
            checked = false
        )
    }
}

@Preview
@Composable
fun TitleHeaderPreview() {
    PileyTheme(useDarkTheme = true) {
        TitleHeader(
            modifier = Modifier.fillMaxWidth(),
            title = "Some title",
            icon = Icons.Default.Abc
        )
    }
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
        TitleTopAppBar(textValue = "some title", canEdit = false, onButtonClick = {})
    }
}

@Preview
@Composable
fun ButtonRowPreview() {
    PileyTheme(useDarkTheme = true) {
        TwoButtonRow(
            onRightClick = {},
            onLeftClick = {},
            rightText = "Confirm",
            leftText = "Cancel"
        )
    }
}