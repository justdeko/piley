package com.dk.piley.ui.pile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun AddTaskField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onChange: (TextFieldValue) -> Unit,
    onDone: KeyboardActionScope.() -> Unit
) {
    val dim = LocalDim.current
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dim.large, start = dim.large, end = dim.large, top = dim.medium),
        value = value,
        onValueChange = onChange,
        placeholder = { Text(stringResource(R.string.add_task_placeholder)) },
        shape = MaterialTheme.shapes.large,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = onDone),
    )
}

@Preview
@Composable
fun AddTaskFieldPreview() {
    PileyTheme(useDarkTheme = true) {
        val text = TextFieldValue("hi there")
        AddTaskField(value = text, onChange = {}, onDone = {})
    }
}