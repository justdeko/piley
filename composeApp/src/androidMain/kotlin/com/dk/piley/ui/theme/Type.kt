package com.dk.piley.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dk.piley.R

@OptIn(ExperimentalTextApi::class)
private val regular = Font(
    R.font.nunitosans_variable,
    variationSettings = FontVariation.Settings(FontVariation.weight(500))
)

@OptIn(ExperimentalTextApi::class)
private val medium = Font(
    R.font.nunitosans_variable,
    variationSettings = FontVariation.Settings(FontVariation.weight(500))
)

@OptIn(ExperimentalTextApi::class)
private val semiBold = Font(
    R.font.nunitosans_variable,
    variationSettings = FontVariation.Settings(FontVariation.weight(600))
)

@OptIn(ExperimentalTextApi::class)
private val bold = Font(
    R.font.nunitosans_variable,
    variationSettings = FontVariation.Settings(FontVariation.weight(700))
)

val pileyFontFamily = FontFamily(fonts = listOf(regular, medium, semiBold, bold))

val AppTypography = Typography(
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