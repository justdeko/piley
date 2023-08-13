package com.dk.piley.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.descriptionCharacterLimit

@Composable
fun EditDescriptionField(
    modifier: Modifier = Modifier,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(LocalDim.current.medium),
        value = value,
        supportingText = {
            Text(
                text = "${value.length} / $descriptionCharacterLimit",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        },
        shape = MaterialTheme.shapes.large,
        onValueChange = onChange,
        singleLine = false,
        maxLines = 6,
        placeholder = { Text(stringResource(R.string.add_description_hint)) },
    )
}

@Preview
@Composable
fun EditDescriptionFieldPreview() {
    PileyTheme(useDarkTheme = true) {
        val text = "hi there\nsdf\nsdf\nsdfiu\ndf\n6\n7 alsodo"
        EditDescriptionField(value = text, onChange = {})
    }
}