@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.kapav.ridemode.core.ui.theme

import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.kapav.ridemode.R

private val bodyFamily = FontFamily(Font(R.font.roboto_flex, weight = FontWeight.Normal))

// The width axis is supported by Android's variable-font renderer from API 26.
private val titleFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    FontFamily(
        Font(
            R.font.roboto_flex,
            weight = FontWeight.SemiBold,
            variationSettings = FontVariation.Settings(FontVariation.width(125f))
        )
    )
} else {
    bodyFamily
}

private val baseline = Typography(fontFamily = bodyFamily)

val RideTypography = Typography(
    fontFamily = bodyFamily,
    headlineLarge = baseline.headlineLarge.copy(fontFamily = titleFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = titleFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = titleFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = titleFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = titleFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = titleFamily)
)
