package com.altankoc.beuverse.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(stringResource(R.string.nav_home), Icons.Rounded.Home, Routes.Home.route),
        BottomNavItem(stringResource(R.string.nav_search), Icons.Rounded.Search, Routes.Search.route),
        BottomNavItem(stringResource(R.string.nav_messages), Icons.Rounded.Email, Routes.Messages.route),
        BottomNavItem(stringResource(R.string.nav_profile), Icons.Rounded.Person, Routes.Profile.route),
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}