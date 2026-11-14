package com.example.miaucare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MiauCareColorScheme = lightColorScheme(
    primary = McCoral,
    onPrimary = McCardWhite,
    secondary = McBlack,
    onSecondary = McCardWhite,
    tertiary = McSoftNeutral,
    onTertiary = McTextPrimary,
    background = McBackground,
    onBackground = McTextPrimary,
    surface = McCardWhite,
    onSurface = McTextPrimary,
    surfaceVariant = McSoftNeutral,
    onSurfaceVariant = McTextSecondary,
    outline = McBorder
)

@Composable
fun MiaucareTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MiauCareColorScheme,
        typography = Typography,
        content = content
    )
}
