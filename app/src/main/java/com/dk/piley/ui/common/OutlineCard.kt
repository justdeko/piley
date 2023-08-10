package com.dk.piley.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.theme.PileyTheme

@Composable
fun OutlineCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(Modifier.padding(LocalDim.current.medium)) {
            content()
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