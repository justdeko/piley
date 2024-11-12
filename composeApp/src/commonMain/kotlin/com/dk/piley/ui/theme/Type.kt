package com.dk.piley.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.nunitosans_variable


// TODO fix variable fonts for ios
// https://github.com/JetBrains/compose-multiplatform/issues/3127
// https://markonovakovic.medium.com/from-android-to-multiplatform-real-100-jetpack-compose-app-part-1-resources-a5db60f1ed73
// https://github.com/JetBrains/compose-multiplatform-core/pull/1623
@Composable
private fun getFontFamily(): FontFamily {
    val fonts = listOf(400, 500, 600, 700).map {
        Font(
            Res.font.nunitosans_variable,
            FontWeight(it)
        )
    }
    return FontFamily(fonts = fonts)
}

@Composable
fun getAppTypography(): Typography {
    val pileyFontFamily = getFontFamily()
    return Typography(
        labelLarge = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.10000000149011612.sp,
            lineHeight = 20.sp,
            fontSize = 14.sp
        ),
        labelMedium = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            lineHeight = 16.sp,
            fontSize = 12.sp
        ),
        labelSmall = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            lineHeight = 16.sp,
            fontSize = 11.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.5.sp,
            lineHeight = 24.sp,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.25.sp,
            lineHeight = 20.sp,
            fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.4000000059604645.sp,
            lineHeight = 16.sp,
            fontSize = 12.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
            lineHeight = 40.sp,
            fontSize = 32.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
            lineHeight = 36.sp,
            fontSize = 28.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.sp,
            lineHeight = 32.sp,
            fontSize = 24.sp
        ),
        displayLarge = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = (-0.25).sp,
            lineHeight = 64.sp,
            fontSize = 57.sp
        ),
        displayMedium = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
            lineHeight = 52.sp,
            fontSize = 45.sp
        ),
        displaySmall = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W400,
            letterSpacing = 0.sp,
            lineHeight = 44.sp,
            fontSize = 36.sp
        ),
        titleLarge = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.W500,
            letterSpacing = 0.sp,
            lineHeight = 28.sp,
            fontSize = 22.sp
        ),
        titleMedium = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.15000000596046448.sp,
            lineHeight = 24.sp,
            fontSize = 16.sp
        ),
        titleSmall = TextStyle(
            fontFamily = pileyFontFamily,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.10000000149011612.sp,
            lineHeight = 20.sp,
            fontSize = 14.sp
        ),
    )
}