package com.dk.piley.ui.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dk.piley.util.BigSpacer

/**
 * User information
 *
 * @param modifier generic modifier
 * @param name user name
 */
@Composable
fun UserInfo(modifier: Modifier = Modifier, name: String) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = "avatar",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(40.dp)
        )
        BigSpacer()
        Text(
            text = name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
