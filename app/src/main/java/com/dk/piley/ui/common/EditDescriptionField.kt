package com.dk.piley.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun EditDescriptionField(
    modifier: Modifier = Modifier,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = value,
        shape = CutCornerShape(16.dp),
        onValueChange = onChange,
        singleLine = false,
        maxLines = 4,
        placeholder = { Text("Click here to add a description") },
    )
}

@Preview
@Composable
fun EditDescriptionFieldPreview() {
    PileyTheme(useDarkTheme = true) {
        val text = "hi there\nsdf\nsdf\nsdfiu\ndf"
        EditDescriptionField(value = text, onChange = {})
    }
}