package com.altankoc.beuverse.feature.notification.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.theme.DarkNavy
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.core.utils.toTimeAgo
import com.altankoc.beuverse.feature.notification.domain.model.Notification
import com.altankoc.beuverse.feature.notification.presentation.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notificationsState by viewModel.notificationsState.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val context = LocalContext.current

    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val darkNavy = DarkNavy
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                contentDescription = stringResource(R.string.back),
                                tint = if (isDark) Color.White else darkNavy
                            )
                        }

                        Text(
                            text = stringResource(R.string.notifications),
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDark) Color.White else darkNavy
                        )

                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = primaryColor
                            ) {
                                Text(
                                    text = unreadCount.toString(),
                                    fontFamily = JakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text(
                                text = stringResource(R.string.mark_all_read),
                                fontFamily = JakartaSansFontFamily,
                                fontSize = 12.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (notificationsState) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primaryColor)
                        }
                    }
                    is Resource.Success -> {
                        val notifications = (notificationsState as Resource.Success).data
                        if (notifications.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_notifications),
                                    fontFamily = JakartaSansFontFamily,
                                    fontSize = 14.sp,
                                    color = if (isDark) Color.White.copy(alpha = 0.4f) else darkNavy.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(notifications, key = { it.id }) { notification ->
                                    NotificationItem(
                                        notification = notification,
                                        timeAgo = notification.createdAt.toTimeAgo(context),
                                        isDark = isDark,
                                        primaryColor = primaryColor,
                                        darkNavy = darkNavy,
                                        onClick = {
                                            viewModel.markAsRead(notification.id)
                                            notification.postId?.let { postId ->
                                                navController.navigate(Routes.PostDetail.createRoute(postId.toString()))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.error_unknown),
                                color = MaterialTheme.colorScheme.error,
                                fontFamily = JakartaSansFontFamily
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    timeAgo: String,
    isDark: Boolean,
    primaryColor: Color,
    darkNavy: Color,
    onClick: () -> Unit
) {
    val isRead = notification.read

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isRead) {
            if (isDark) Color.White.copy(alpha = 0.03f) else Color.Black.copy(alpha = 0.05f)
        } else {
            if (isDark) primaryColor.copy(alpha = 0.1f) else primaryColor.copy(alpha = 0.12f)
        },
        border = if (isDark) {
            androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
        } else {
            androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.08f))
        },
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.2f),
                                primaryColor.copy(alpha = 0.5f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${notification.sender.firstName.first()}${notification.sender.lastName.first()}".uppercase(),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isDark) primaryColor else darkNavy
                )
                
                if (!notification.sender.profilePhotoUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = notification.sender.profilePhotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = primaryColor,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notificationText(notification),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = if (isRead) FontWeight.Normal else FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = if (isDark) Color.White else darkNavy,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeAgo,
                    fontFamily = JakartaSansFontFamily,
                    fontSize = 11.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
                )
            }

            if (!isRead) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(primaryColor, CircleShape)
                )
            }
        }
    }
}


@Composable
private fun notificationText(notification: Notification): String {
    val fullName = "${notification.sender.firstName} ${notification.sender.lastName}"
    return when (notification.type) {
        "LIKE_POST" -> stringResource(R.string.notif_liked_post, fullName)
        "LIKE_COMMENT" -> stringResource(R.string.notif_liked_comment, fullName)
        "COMMENT" -> stringResource(R.string.notif_commented, fullName)
        "REPLY" -> stringResource(R.string.notif_replied, fullName)
        else -> fullName
    }
}