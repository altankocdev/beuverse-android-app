package com.altankoc.beuverse.feature.messages.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Timer
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
import com.altankoc.beuverse.core.di.TokenManagerEntryPoint
import com.altankoc.beuverse.core.ui.theme.DarkNavy
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.core.utils.toTimeAgo
import com.altankoc.beuverse.feature.messages.domain.model.Message
import com.altankoc.beuverse.feature.messages.presentation.viewmodel.MessagingViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    conversationId: String?,
    username: String?,
    viewModel: MessagingViewModel = hiltViewModel()
) {
    val messagesState by viewModel.messagesState.collectAsState()
    val sendMessageState by viewModel.sendMessageState.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()
    val conversationsState by viewModel.conversationsState.collectAsState()
    
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val listState = rememberLazyListState()
    var messageText by remember { mutableStateOf("") }

    val primaryColor = MaterialTheme.colorScheme.primary
    val darkNavy = DarkNavy
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    var currentUserId by remember { mutableStateOf<Long?>(null) }

    val otherStudent = (conversationsState as? Resource.Success)?.data?.find { it.id == conversationId?.toLongOrNull() }?.otherStudent
    val otherStudentId = otherStudent?.id
    val profilePhotoUrl = otherStudent?.profilePhotoUrl

    val tokenManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            TokenManagerEntryPoint::class.java
        ).tokenManager()
    }

    LaunchedEffect(Unit) {
        currentUserId = tokenManager.userId.first()
        viewModel.connectWebSocket()
        conversationId?.toLongOrNull()?.let { id ->
            viewModel.loadMessages(id)
            viewModel.markMessagesAsRead(id)
        }
    }

    LaunchedEffect(conversationsState) {
        if (conversationsState is Resource.Success) {
            val conv = (conversationsState as Resource.Success).data.find { it.id == conversationId?.toLongOrNull() }
            if (conv != null && conv.accepted) {
                viewModel.startCountdown(conv.expiresAt)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopCountdown()
        }
    }

    LaunchedEffect(sendMessageState) {
        if (sendMessageState is Resource.Success) {
            messageText = ""
            listState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f),
                    tonalElevation = 0.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBackIosNew,
                                    contentDescription = null,
                                    tint = if (isDark) Color.White else darkNavy
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        otherStudentId?.let { id ->
                                            navController.navigate(Routes.UserProfile.createRoute(id.toString()))
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(primaryColor.copy(alpha = 0.2f), CircleShape)
                                        .border(1.dp, primaryColor.copy(alpha = 0.4f), CircleShape)
                                        .clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = username?.take(2)?.uppercase() ?: "?",
                                        fontFamily = JakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isDark) primaryColor else darkNavy
                                    )
                                    
                                    if (!profilePhotoUrl.isNullOrBlank()) {
                                        SubcomposeAsyncImage(
                                            model = profilePhotoUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
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

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "${username ?: ""}",
                                        fontFamily = JakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isDark) Color.White else darkNavy
                                    )
                                }
                            }

                            if (remainingTime != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = primaryColor.copy(alpha = 0.1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Timer,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = remainingTime!!,
                                            fontFamily = JakartaSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = primaryColor
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = {
                                if (it.length <= 1000) messageText = it
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.type_message),
                                    fontFamily = JakartaSansFontFamily,
                                    color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = false,
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor.copy(alpha = 0.5f),
                                unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                                focusedTextColor = if (isDark) Color.White else darkNavy,
                                unfocusedTextColor = if (isDark) Color.White else darkNavy
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        FloatingActionButton(
                            onClick = {
                                conversationId?.toLongOrNull()?.let { id ->
                                    viewModel.sendMessage(id, messageText.trim())
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            containerColor = if (messageText.isNotBlank()) primaryColor else if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                            contentColor = if (isDark) darkNavy else Color.White,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            if (sendMessageState is Resource.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = if (isDark) darkNavy else Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Rounded.Send, null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when {
                    messagesState == null || messagesState is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primaryColor)
                        }
                    }
                    messagesState is Resource.Success -> {
                        val messages = (messagesState as Resource.Success).data
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            reverseLayout = true
                        ) {
                            items(messages, key = { it.id }) { message ->
                                val isMe = message.sender.id == currentUserId
                                MessageBubble(
                                    message = message,
                                    isMe = isMe,
                                    isDark = isDark,
                                    context = context,
                                    primaryColor = primaryColor
                                )
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.error_unknown), color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isMe: Boolean,
    isDark: Boolean,
    context: android.content.Context,
    primaryColor: Color
) {
    val darkNavy = DarkNavy

    val bubbleColor = if (isMe) primaryColor else if (isDark) Color.White.copy(alpha = 0.08f) else Color.White
    val textColor = if (isMe) (if (isDark) darkNavy else Color.White) else (if (isDark) Color.White else darkNavy)
    val alignment = if (isMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMe) 18.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 18.dp
            ),
            color = bubbleColor,
            shadowElevation = if (!isMe && !isDark) 1.dp else 0.dp,
            border = if (!isMe && isDark) androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f)) else null
        ) {
            Text(
                text = message.content,
                fontFamily = JakartaSansFontFamily,
                fontSize = 15.sp,
                color = textColor,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = message.createdAt.toTimeAgo(context),
            fontFamily = JakartaSansFontFamily,
            fontSize = 10.sp,
            color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}