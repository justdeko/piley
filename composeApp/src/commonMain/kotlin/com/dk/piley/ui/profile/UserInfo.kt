package com.dk.piley.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dk.piley.ui.common.LocalDim
import org.jetbrains.compose.resources.painterResource
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.cat

/**
 * User information
 *
 * @param modifier generic modifier
 * @param name user name
 */
@Composable
fun UserInfo(modifier: Modifier = Modifier, name: String) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(Res.drawable.cat),
            contentDescription = "avatar",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.tertiary),
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(
                    LocalDim.current.small,
                    MaterialTheme.colorScheme.inversePrimary,
                    CircleShape
                )
        )
        Text(
            text = name,
            modifier = Modifier
                .padding(top = LocalDim.current.medium)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
