package com.dk.piley.ui.reminder

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.ui.theme.PileyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DelayBottomSheetPreview() {
    val sheetState = rememberStandardBottomSheetState(SheetValue.Expanded)
    PileyTheme {
        DelayBottomSheet(sheetState = sheetState)
    }
}