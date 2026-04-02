package com.altankoc.beuverse.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.theme.Mist

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    onCreatePostClick: () -> Unit,
    hasUnreadMessages: Boolean = false
) {
    val leftItems = listOf(
        BottomNavItem(stringResource(R.string.nav_home), Icons.Rounded.Home, Routes.Home.route),
        BottomNavItem(stringResource(R.string.nav_search), Icons.Rounded.Search, Routes.Search.route),
    )
    val rightItems = listOf(
        BottomNavItem(stringResource(R.string.nav_messages), Icons.Rounded.Email, Routes.Messages.route),
        BottomNavItem(stringResource(R.string.nav_profile), Icons.Rounded.Person, Routes.Profile.route),
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val darkNavy = Color(0xFF1A2A3A)
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = Color.Transparent,
            shadowElevation = 20.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                surfaceColor.copy(alpha = 0.96f),
                                surfaceColor.copy(alpha = 0.90f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leftItems.forEach { item ->
                        NavBarItem(
                            item = item,
                            isSelected = currentRoute == item.route,
                            onClick = { onItemClick(item.route) }
                        )
                    }

                    Spacer(modifier = Modifier.width(70.dp))

                    rightItems.forEach { item ->
                        val showBadge = item.route == Routes.Messages.route && hasUnreadMessages
                        NavBarItem(
                            item = item,
                            isSelected = currentRoute == item.route,
                            onClick = { onItemClick(item.route) },
                            showBadge = showBadge
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(bottom = 30.dp)
                .navigationBarsPadding()
                .size(62.dp)
                .shadow(15.dp, CircleShape, spotColor = primaryColor)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.85f)
                        )
                    )
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                .clickable { onCreatePostClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = if (isDark) darkNavy else Mist,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun NavBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    showBadge: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.18f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.5f,
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .width(52.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (showBadge) {
                    Badge(containerColor = Color.Red)
                }
            }
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(26.dp)
                    .graphicsLayer(alpha = alpha)
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(width = 14.dp, height = 3.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        } else {
            Spacer(modifier = Modifier.height(7.dp))
        }
    }
}