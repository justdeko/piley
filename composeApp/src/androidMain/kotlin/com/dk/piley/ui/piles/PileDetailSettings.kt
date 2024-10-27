package com.dk.piley.ui.piles

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.settings.DropdownSettingsItem
import com.dk.piley.ui.settings.SettingsSection
import com.dk.piley.ui.settings.SliderSettingsItem
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.roundedOutline
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.pile_limit_setting_description
import piley.composeapp.generated.resources.pile_limit_setting_title
import piley.composeapp.generated.resources.pile_mode_setting_description
import piley.composeapp.generated.resources.pile_mode_setting_dropdown_label
import piley.composeapp.generated.resources.pile_mode_setting_title
import piley.composeapp.generated.resources.pile_modes
import piley.composeapp.generated.resources.pile_settings_section_title

/**
 * Pile detail settings section
 *
 * @param modifier generic modifier
 * @param viewState pile detail view state
 * @param expandedState expanded state
 * @param onSetPileMode on set pile completion mode
 * @param onSetPileLimit on set pile task limit
 */
@Composable
fun PileDetailSettings(
    modifier: Modifier = Modifier,
    viewState: PileDetailViewState,
    expandedState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onSetPileMode: (PileMode) -> Unit = {},
    onSetPileLimit: (Int) -> Unit = {},
) {
    var sliderValue by remember { mutableIntStateOf(viewState.pile.pileLimit) }
    val pileModeValues = stringArrayResource(Res.array.pile_modes).toList()
    SettingsSection(
        modifier = modifier
            .padding(LocalDim.current.medium)
            .roundedOutline(),
        title = stringResource(Res.string.pile_settings_section_title),
        icon = Icons.Default.Settings,
        expandedState = expandedState
    ) {
        DropdownSettingsItem(
            title = stringResource(Res.string.pile_mode_setting_title),
            description = stringResource(Res.string.pile_mode_setting_description),
            optionLabel = stringResource(Res.string.pile_mode_setting_dropdown_label),
            selectedValue = pileModeValues[viewState.pile.pileMode.value],
            values = pileModeValues,
            onValueChange = {
                onSetPileMode(PileMode.fromValue(pileModeValues.indexOf(it)))
            }
        )
        SliderSettingsItem(
            title = stringResource(Res.string.pile_limit_setting_title),
            description = stringResource(Res.string.pile_limit_setting_description),
            value = viewState.pile.pileLimit,
            range = Pair(0, 20),
            steps = 20,
            onValueChange = {
                sliderValue = it
                onSetPileLimit(it)
            }
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
            ),
            expandedState = remember { mutableStateOf(true) }
        )
    }
}