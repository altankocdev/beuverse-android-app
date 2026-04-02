package com.altankoc.beuverse.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Mist,
    onPrimary = Midnight,
    background = Midnight,
    onBackground = Mist,
    surface = NavyDeep,
    onSurface = Mist,
    secondaryContainer = SteelBlue,
)

private val LightColorScheme = lightColorScheme(
    primary = NavyDeep,
    onPrimary = SnowWhite,
    background = SnowWhite,
    onBackground = Midnight,
    surface = Mist,
    onSurface = Midnight,
    secondaryContainer = NavyDeep,
)

@Composable
fun BeuverseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

val ColorScheme.beuverseBackgroundGradient: List<Color>
    @Composable
    get() = if (isSystemInDarkTheme()) {
        listOf(DarkNavy, DarkBackgroundEnd)
    } else {
        listOf(MatteLightBg1, MatteLightBg2)
    }

val ColorScheme.primaryTextAndIconColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else DarkNavy