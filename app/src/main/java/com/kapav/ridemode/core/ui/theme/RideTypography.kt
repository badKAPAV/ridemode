@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.kapav.ridemode.core.ui.theme

import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.kapav.ridemode.R

// The width axis is supported by Android's variable-font renderer from API 26.
private val titleWeight = FontWeight(648)

// Exact Roboto Flex axes from the approved Google Fonts specimen. Optical size stays automatic.
private fun robotoFlex(weight: Int, grade: Int): FontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    FontFamily(Font(R.font.roboto_flex, weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(
            FontVariation.width(151f), FontVariation.weight(weight), FontVariation.grade(grade),
            FontVariation.slant(0f), FontVariation.Setting("XOPQ", 96f),
            FontVariation.Setting("XTRA", 603f), FontVariation.Setting("YOPQ", 79f),
            FontVariation.Setting("YTAS", 677f), FontVariation.Setting("YTDE", -217f),
            FontVariation.Setting("YTFI", 738f), FontVariation.Setting("YTUC", 712f),
            FontVariation.Setting("YTLC", 514f)
        )))
} else FontFamily(Font(R.font.roboto_flex, weight = FontWeight(weight)))

private val titleFamily = robotoFlex(648, 150)
val RideThinTitleFontFamily = robotoFlex(300, 0)
val RideHeavyTitleFontFamily = robotoFlex(1000, 150)
private val bodyFamily = robotoFlex(400, 0)

private val baseline = Typography(fontFamily = bodyFamily)

val RideTypography = Typography(
    fontFamily = bodyFamily,
    headlineLarge = baseline.headlineLarge.copy(fontFamily = titleFamily, fontWeight = titleWeight),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = titleFamily, fontWeight = titleWeight),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = titleFamily, fontWeight = titleWeight),
    titleLarge = baseline.titleLarge.copy(fontFamily = titleFamily, fontWeight = titleWeight),
    titleMedium = baseline.titleMedium.copy(fontFamily = titleFamily, fontWeight = titleWeight),
    titleSmall = baseline.titleSmall.copy(fontFamily = titleFamily, fontWeight = titleWeight)
)
