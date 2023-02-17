package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.R
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.settings.DropdownSettingsItem
import com.dk.piley.ui.settings.SliderSettingsItem
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun PileDetailSettings(
    modifier: Modifier = Modifier,
    viewState: PileDetailViewState,
    onSetPileMode: (PileMode) -> Unit = {},
    onSetPileLimit: (Int) -> Unit = {},
) {
    val pileModeValues = stringArrayResource(R.array.pile_modes).toList()
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Settings",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp),
            textAlign = TextAlign.Start
        )
        DropdownSettingsItem(title = "Pile mode",
            description = "Set the task completion mode for this pile.",
            optionLabel = "Pile Mode",
            selectedValue = pileModeValues[viewState.pile.pileMode.value],
            values = pileModeValues,
            onValueChange = {
                onSetPileMode(PileMode.fromValue(pileModeValues.indexOf(it)))
            })
        SliderSettingsItem(
            title = "Pile Limit",
            description = "Set the limit of tasks in a pile. 0 means no limit",
            value = viewState.pile.pileLimit,
            range = Pair(0, 50),
            steps = 10,
            onValueChange = onSetPileLimit
        )
    }
}

@Preview
@Composable
fun PileDetailSettingsPreview() {
    PileyTheme(useDarkTheme = true) {
        PileDetailSettings(
            viewState = PileDetailViewState(
                Pile(
                    pileMode = PileMode.FIFO,
                    pileLimit = 15
                )
            )
        )
    }
}