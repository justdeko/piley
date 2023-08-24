package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.settings.DropdownSettingsItem
import com.dk.piley.ui.settings.SettingsSection
import com.dk.piley.ui.settings.SliderSettingsItem
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.roundedOutline

/**
 * Pile detail settings section
 *
 * @param modifier generic modifier
 * @param viewState pile detail view state
 * @param onSetPileMode on set pile completion mode
 * @param onSetPileLimit on set pile task limit
 */
@Composable
fun PileDetailSettings(
    modifier: Modifier = Modifier,
    viewState: PileDetailViewState,
    onSetPileMode: (PileMode) -> Unit = {},
    onSetPileLimit: (Int) -> Unit = {},
) {
    val pileModeValues = stringArrayResource(R.array.pile_modes).toList()
    SettingsSection(
        modifier = modifier
            .padding(LocalDim.current.medium)
            .roundedOutline(),
        title = stringResource(R.string.pile_settings_section_title),
        icon = Icons.Default.Settings
    ) {
        DropdownSettingsItem(
            title = stringResource(R.string.pile_mode_setting_title),
            description = stringResource(R.string.pile_mode_setting_description),
            optionLabel = stringResource(R.string.pile_mode_setting_dropdown_label),
            selectedValue = pileModeValues[viewState.pile.pileMode.value],
            values = pileModeValues,
            onValueChange = {
                onSetPileMode(PileMode.fromValue(pileModeValues.indexOf(it)))
            }
        )
        SliderSettingsItem(
            title = stringResource(R.string.pile_limit_setting_title),
            description = stringResource(R.string.pile_limit_setting_description),
            value = viewState.pile.pileLimit,
            range = Pair(0, 20),
            steps = 20,
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