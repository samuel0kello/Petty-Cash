package com.example.pettycash.mai.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.LineHeightStyle
import com.example.pettycash.mai.R

private val MaiFontFamily = FontFamily(
    Font(R.font.gsf_regular, FontWeight.Normal),
    Font(R.font.gsf_medium, FontWeight.Medium),
)

private val MaiDisplayFontFamily = FontFamily(
    Font(R.font.gsf_display_regular, FontWeight.Normal),
    Font(R.font.gsf_display_medium, FontWeight.Medium),
)

private val GeistMonoFamily = FontFamily(
    Font(R.font.geistmono_400, FontWeight.Normal),
    Font(R.font.geistmono_500, FontWeight.Medium),
)

private val BricolageFamily = FontFamily(
    Font(R.font.bricolagegrotesque_400, FontWeight.Normal),
    Font(R.font.bricolagegrotesque_500, FontWeight.Medium),
    Font(R.font.bricolagegrotesque_600, FontWeight.SemiBold),
    Font(R.font.bricolagegrotesque_700, FontWeight.Bold),
)

private fun maiStyle(
    size: Int,
    lineHeight: Int,
    weight: FontWeight,
    tracking: Float,
    display: Boolean = false,
    family: FontFamily = if (display) MaiDisplayFontFamily else MaiFontFamily,
) = TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.sp,
    lineHeightStyle = LineHeightStyle(
        LineHeightStyle.Alignment.Center,
        LineHeightStyle.Trim.Both,
        LineHeightStyle.Mode.Tight,
    ),
)

val MaiTypography = Typography(
    displayLarge = maiStyle(57, 64, FontWeight.Normal, -0.25f, display = true),
    displayMedium = maiStyle(45, 52, FontWeight.Normal, 0f, display = true),
    displaySmall = maiStyle(36, 44, FontWeight.Normal, 0f, display = true),
    headlineLarge = maiStyle(32, 40, FontWeight.Normal, 0f, display = true),
    headlineMedium = maiStyle(28, 36, FontWeight.Normal, 0f, display = true),
    headlineSmall = maiStyle(24, 32, FontWeight.Normal, 0f),
    titleLarge = maiStyle(22, 28, FontWeight.Normal, 0f),
    titleMedium = maiStyle(16, 24, FontWeight.Medium, 0.15f),
    titleSmall = maiStyle(14, 20, FontWeight.Medium, 0.1f),
    bodyLarge = maiStyle(16, 24, FontWeight.Normal, 0.5f),
    bodyMedium = maiStyle(14, 20, FontWeight.Normal, 0.25f),
    bodySmall = maiStyle(12, 16, FontWeight.Normal, 0.4f),
    labelLarge = maiStyle(14, 20, FontWeight.Medium, 0.1f),
    labelMedium = maiStyle(12, 16, FontWeight.Medium, 0.5f),
    labelSmall = maiStyle(11, 16, FontWeight.Medium, 0.5f),
)

data class MaiCustomTextStyles(
    val amountLarge: TextStyle,
    val amountMedium: TextStyle,
    val amountSmall: TextStyle,
    val codeMedium: TextStyle,
    val codeSmall: TextStyle,
    val brandDisplay: TextStyle,
)

val MaiCustomTypography = MaiCustomTextStyles(
    amountLarge = maiStyle(32, 36, FontWeight.Medium, 0f, display = true),
    amountMedium = maiStyle(22, 28, FontWeight.Medium, 0f),
    amountSmall = maiStyle(16, 24, FontWeight.Medium, 0f),
    codeMedium = maiStyle(14, 20, FontWeight.Normal, 0f, family = GeistMonoFamily),
    codeSmall = maiStyle(12, 16, FontWeight.Normal, 0f, family = GeistMonoFamily),
    brandDisplay = maiStyle(36, 44, FontWeight.Medium, 0f, family = BricolageFamily),
)
