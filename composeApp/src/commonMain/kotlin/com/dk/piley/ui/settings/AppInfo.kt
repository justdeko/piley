package com.dk.piley.ui.settings

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import com.dk.piley.ui.common.LocalDim
import com.dk.piley.util.getVersionName
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.app_name
import piley.composeapp.generated.resources.github


/**
 * App info
 *
 * @param modifier generic modifier
 */
@Composable
fun AppInfo(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LocalDim.current.large, vertical = LocalDim.current.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { uriHandler.openUri("https://github.com/justdeko/piley") }) {
            Icon(
                painter = painterResource(Res.drawable.github),
                "github link",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        Text(
            text = "${stringResource(Res.string.app_name)} ${getVersionName()}",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        IconButton(onClick = { uriHandler.openUri("https://denisk.dev/piley/") }) {
            Icon(
                Icons.Filled.Info,
                "website link",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
