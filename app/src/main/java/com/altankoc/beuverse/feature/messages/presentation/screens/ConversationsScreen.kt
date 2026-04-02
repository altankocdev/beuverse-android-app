package com.altankoc.beuverse.feature.messages.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.altankoc.beuverse.feature.messages.domain.model.Conversation
import com.altankoc.beuverse.feature.messages.presentation.viewmodel.MessagingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    navController: NavController,
    viewModel: MessagingViewModel = hiltViewModel()
) {
    val conversationsState by viewModel.conversationsState.collectAsState()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary

    val darkNavy = DarkNavy
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    LaunchedEffect(Unit) {
        viewModel.connectWebSocket()
        viewModel.loadConversations()
        viewModel.clearUnreadBadge()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.messages),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = if (isDark) Color.White else darkNavy
                    )
                }
            }

            when (conversationsState) {
                null, is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                }
                is Resource.Success -> {
                    val conversations = (conversationsState as Resource.Success).data
                    val pending = conversations.filter { !it.accepted }
                    val active = conversations.filter { it.accepted }

                    if (conversations.isEmpty()) {
                        EmptyState(primaryColor, isDark)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            if (pending.isNotEmpty()) {
                                item {
                                    SectionTitle(
                                        title = stringResource(R.string.pending_requests),
                                        isDark = isDark
                                    )
                                }
                                items(pending, key = { "pending_${it.id}" }) { conversation ->
                                    ConversationItem(
                                        conversation = conversation,
                                        context = context,
                                        primaryColor = primaryColor,
                                        isPending = true,
                                        isDark = isDark,
                                        onAccept = { viewModel.acceptConversation(conversation.id) },
                                        onDecline = { viewModel.deleteConversation(conversation.id) },
                                        onClick = {},
                                        onDelete = { viewModel.deleteConversation(conversation.id) }
                                    )
                                }
                            }

                            if (active.isNotEmpty()) {
                                item {
                                    SectionTitle(
                                        title = stringResource(R.string.active_conversations),
                                        isDark = isDark
                                    )
                                }
                                items(active, key = { "active_${it.id}" }) { conversation ->
                                    SwipeToDismissConversation(
                                        conversation = conversation,
                                        onDismiss = { viewModel.deleteConversation(conversation.id) }
                                    ) {
                                        ConversationItem(
                                            conversation = conversation,
                                            context = context,
                                            primaryColor = primaryColor,
                                            isPending = false,
                                            isDark = isDark,
                                            onAccept = {},
                                            onDecline = {},
                                            onClick = {
                                                navController.navigate(
                                                    Routes.Chat.createRoute(
                                                        conversation.id.toString(),
                                                        conversation.otherStudent.username
                                                    )
                                                )
                                            },
                                            onDelete = { viewModel.deleteConversation(conversation.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    ErrorState()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissConversation(
    conversation: Conversation,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Color.Red.copy(alpha = 0.7f)
            } else {
                Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color)
            )
        },
        content = { content() }
    )
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
    context: android.content.Context,
    primaryColor: Color,
    isPending: Boolean,
    isDark: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false },
            primaryColor = primaryColor
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { if (!isPending) showDeleteDialog = true },
                    onTap = { onClick() }
                )
            },
        shape = RoundedCornerShape(20.dp),
        color = if (isDark) {
            Color.White.copy(alpha = 0.05f)
        } else {
            Color.White.copy(alpha = 0.9f)
        },
        shadowElevation = if (isDark) 0.dp else 2.dp,
        border = if (isDark) {
            androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
        } else {
            androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.1f))
        }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.3f),
                                    primaryColor.copy(alpha = 0.6f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${conversation.otherStudent.firstName.first()}${conversation.otherStudent.lastName.first()}".uppercase(),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDark) primaryColor else Color(0xFF1A2A3A)
                    )
                    
                    if (!conversation.otherStudent.profilePhotoUrl.isNullOrBlank()) {
                        SubcomposeAsyncImage(
                            model = conversation.otherStudent.profilePhotoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = primaryColor,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        )
                    }
                }

                if (conversation.unreadCount > 0 && !isPending) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .background(Color.Red, CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (isDark) Color(0xFF1A2A3A) else Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (conversation.unreadCount > 9) "9+" else conversation.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${conversation.otherStudent.firstName} ${conversation.otherStudent.lastName}",
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isDark) Color.White else Color(0xFF1A2A3A)
                )
                Text(
                    text = if (isPending && !conversation.requester) {
                        stringResource(R.string.message_request)
                    } else if (isPending && conversation.requester) {
                        stringResource(R.string.message_request_sent)
                    } else {
                        conversation.expiresAt?.toTimeAgo(context) ?: ""
                    },
                    fontFamily = JakartaSansFontFamily,
                    fontSize = 13.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isPending && !conversation.requester) {
                Row {
                    IconButton(onClick = onDecline) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                    IconButton(onClick = onAccept) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = primaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, isDark: Boolean) {
    Text(
        text = title.uppercase(),
        fontFamily = JakartaSansFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
        color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.5f),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

@Composable
private fun DeleteConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit, primaryColor: Color) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isSystemInDarkTheme()) Color(0xFF1E2A38) else Color.White,
        title = {
            Text(
                text = stringResource(R.string.delete_conversation),
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_conversation_confirm),
                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.DarkGray
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.delete_conversation),
                    color = Color.Red
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = primaryColor
                )
            }
        }
    )
}

@Composable
private fun EmptyState(primaryColor: Color, isDark: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.ChatBubbleOutline,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_messages),
                color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray,
                fontFamily = JakartaSansFontFamily
            )
        }
    }
}

@Composable
private fun ErrorState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.error_unknown),
            color = Color.Red,
            fontFamily = JakartaSansFontFamily
        )
    }
}