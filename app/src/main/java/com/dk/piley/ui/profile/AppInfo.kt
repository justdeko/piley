package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dk.piley.BuildConfig
import com.dk.piley.R
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.ui.theme.PileyTheme


/**
 * App info
 *
 * @param modifier generic modifier
 */
@Composable
fun AppInfo(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LocalDim.current.large, vertical = LocalDim.current.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { uriHandler.openUri("https://github.com/justdeko/piley") }) {
            Icon(
                painter = painterResource(id = R.drawable.github),
                "github link",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        Text(
            text = "${context.getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { uriHandler.openUri("https://justdeko.github.io/piley/") }) {
            Icon(
                Icons.Filled.Info,
                "website link",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Preview
@Composable
fun AppInfoPreview() {
    PileyTheme(useDarkTheme = true) {
        AppInfo()
    }
}