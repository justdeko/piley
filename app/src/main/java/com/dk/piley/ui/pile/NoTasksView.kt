package com.dk.piley.ui.pile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.R
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme
import com.dk.piley.util.BigSpacer

@Composable
fun NoTasksView(
    modifier: Modifier = Modifier,
    noTasksYet: Boolean = false
) {
    BoxWithConstraints(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.width(this@BoxWithConstraints.maxWidth / 2),
                contentScale = ContentScale.Inside,
                painter = painterResource(id = R.drawable.tasks_completed),
                contentDescription = "no tasks found"
            )
            BigSpacer()
            Text(
                modifier = Modifier.padding(horizontal = LocalDim.current.large),
                text = stringResource(if (noTasksYet) R.string.no_tasks_yet_message else R.string.no_tasks_left_message),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun NoTasksViewPreview() {
    PileyTheme(useDarkTheme = true) {
        NoTasksView(modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = false)
@Composable
fun NoTasksYetViewPreview() {
    PileyTheme(useDarkTheme = true) {
        NoTasksView(modifier = Modifier.fillMaxWidth(), noTasksYet = true)
    }
}