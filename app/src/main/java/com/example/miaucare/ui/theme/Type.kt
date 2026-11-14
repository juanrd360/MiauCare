package com.example.miaucare.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val McTitle28 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 27.sp,
    lineHeight = 34.sp,
    color = McTextPrimary
)

val McTitle24 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 23.sp,
    lineHeight = 30.sp,
    color = McTextPrimary
)

val McTitle18 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    color = McTextPrimary
)

val McTitle16 = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    color = McTextPrimary
)

val McBody14Bold = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = McTextPrimary
)

val McBody14Regular = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 13.5.sp,
    lineHeight = 19.sp,
    color = McTextSecondary
)

val McLabel12Bold = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    color = McTextPrimary
)

val McLabel11Regular = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 11.5.sp,
    lineHeight = 15.sp,
    color = McTextSecondary
)

val Typography = Typography(
    headlineLarge = McTitle28,
    headlineMedium = McTitle24,
    titleLarge = McTitle18,
    titleMedium = McTitle16,
    bodyLarge = McBody14Bold,
    bodyMedium = McBody14Regular,
    labelLarge = McLabel12Bold,
    labelSmall = McLabel11Regular
)
