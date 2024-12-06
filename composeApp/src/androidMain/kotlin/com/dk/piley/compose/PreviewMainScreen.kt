package com.dk.piley.compose

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark

@PreviewLightDark
/*@Preview(name = "Light Mode Landscape", showBackground = true,
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Preview(name = "Dark Mode Landscape", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true,
    device = "spec:parent=pixel_5,orientation=landscape"
)*/
@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=400", showBackground = true, group = "tablet")
annotation class PreviewMainScreen // TODO: use MultiPreview