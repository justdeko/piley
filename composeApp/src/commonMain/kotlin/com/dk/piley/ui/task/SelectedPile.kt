package com.dk.piley.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.ui.common.DropDown
import com.dk.piley.ui.common.LocalDim

/**
 * Pile selection menu
 *
 * @param modifier generic modifier
 * @param pileNames list of pile names
 * @param selectedPileIndex initial pile index selection
 * @param onSelect on pile selection by index
 */
@Composable
fun SelectedPile(
    modifier: Modifier = Modifier,
    pileNames: List<String>,
    selectedPileIndex: Int,
    onSelect: (Int) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Current Pile",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = LocalDim.current.medium),
            textAlign = TextAlign.Start
        )
        DropDown(
            modifier = Modifier.padding(LocalDim.current.medium).fillMaxWidth(),
            value = pileNames.getOrElse(selectedPileIndex) { "" },
            dropdownValues = pileNames,
            label = null,
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            onIndexClick = {
                onSelect(it)
                expanded = false
            },
            onDismiss = { expanded = false }
        )
    }
}
