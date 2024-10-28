package com.dk.piley.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.util.descriptionCharacterLimit
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.add_description_hint

/**
 * Edit description field
 *
 * @param modifier default modifier
 * @param value description value
 * @param onChange on description change
 */
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
        placeholder = { Text(stringResource(Res.string.add_description_hint)) },
    )
}
